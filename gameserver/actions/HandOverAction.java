package com.mynet.gameserver.actions;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.logs.move.MoveDestination;
import com.mynet.gameserver.logs.move.MoveDirection;
import com.mynet.gameserver.model.HandOverCardModel;
import com.mynet.gameserver.model.HandOverResultModel;
import com.mynet.gameserver.model.KickModel;
import com.mynet.gameserver.okey.CardMap;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.response.GameIdResponse;
import com.mynet.gameserver.response.HandOverResponse;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.GameEndStatus;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.OkeyCard;
import com.mynet.gameserver.table.HandOverController;
import com.mynet.matchserver.GameUser;
import com.mynet.questservice.quests.category.*;
import com.mynet.questservice.quests.types.DifferentDaysType;
import com.mynet.questservice.quests.types.StreakType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.logs.WinnerDbLogType;
import com.mynet.shared.logs.WinnerLogDbData;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.builders.ErrorResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.launchers.GameServerLauncher;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.types.DataSourceType;

import java.util.*;
import java.util.stream.Collectors;

public class HandOverAction extends TableAction {
    private static Logger logger = LoggerFactory.getLogger(HandOverAction.class);
    private final String tableId;
    private final int side;
    private final String cId;
    private final String userId;
    private final boolean goDouble;
    private List<HandOverCardModel> finishedHand;
    private int longestTile;
    private boolean withFakeOkey;
    private boolean withOkey;

    public HandOverAction(Table table, GameUser user, String tableId, int side, String cId, String userId, boolean goDouble, List<HandOverCardModel> finishedHand) {
        super(table, user);
        this.tableId = tableId;
        this.side = side;
        this.cId = cId;
        this.goDouble = goDouble;
        this.userId = userId;
        this.finishedHand = finishedHand;
        this.longestTile = 0;
        this.withFakeOkey = false;
    }

    @Override
    public boolean process() {
        GameController gameController = GameController.getInstance();
        try {
            if (table.getGameTurn() != user.getSide()) {
                ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_YOUR_TURN).createErrorResponse();
                table.sendErrorMessage(user,NetworkMessage.getGson().toJson(error));

                handOverLog(
                        user.getfuid(),
                        table.getGameId() + "",
                        "-",
                        "-",
                        "IsNotYourTurn",
                        "-",
                        user.getPlatform(),
                        GameServerLauncher.currentNode.getId(),
                        table.getTableId(),
                        table
                );
                return true;
            }

            OkeyCard okeyCard = CardMap.getInstance().getCard(cId);
            int cardColor = okeyCard.getCardType();
            int cardNumber = okeyCard.getCardNumber();

            String card = cardColor + ";" + cardNumber;

            if (table != null) {

                if (table.getGameStatus() != GameStatus.PLAYING) {
                    return true;
                }
                GameEndStatus gameEndStatus = GameEndStatus.NORMAL;

                // Get Okey
                OkeyCard okey = table.getCardHandler().getOkey();
                String lastCardStr = cardColor + "#" + cardNumber;
                String okeyStr = okey.getCardType() + ";"
                        + okey.getCardNumber();

                String okeyStrLog = okey.getCardType() + "#"
                        + okey.getCardNumber();

                int result;

                OkeyCard lastCardObj = new OkeyCard(1, cardNumber, cardColor, 0 ); // bucket is not real, because client not sending it..


                HandOverController handOverController = HandOverController.getInstance();
                String finishedHandLogData = getFinishedHandLogData();
                if (goDouble) {
                    List<HandOverCardModel> fde = checkCompleteDouble(card, okey, table, lastCardObj, finishedHand);
                    if (fde == null) {
                        //Error
                        return true;
                    }
                } else {
                    //hand over cards validation
                    HandOverResultModel cardValidation = handOverController.checkValidCards(table, finishedHand, lastCardObj);
                    if(!cardValidation.isOk()){
                        handOverLog(
                                user.getfuid(),
                                table.getGameId() + "",
                                table.getCardHandler().getSideHand(table.getGameTurn()),
                                finishedHandLogData,
                                cardValidation.getLogText(),
                                cardValidation.getDeckErrorText(),
                                user.getPlatform(),
                                GameServerLauncher.currentNode.getId(),
                                table.getTableId(),
                                table
                        );

                        table.sendErrorMessage(user, NetworkMessage.getGson().toJson(cardValidation.getError()));

                        return true;
                    }

                    //hand over pile validation
                    HandOverResultModel deckValidation = handOverController.checkHandComplete(finishedHand, okey);
                    if(!deckValidation.isOk()){
                        handOverLog(
                                user.getfuid(),
                                table.getGameId() + "",
                                table.getCardHandler().getSideHand(table.getGameTurn()),
                                finishedHandLogData,
                                deckValidation.getLogText(),
                                deckValidation.getDeckErrorText(),
                                user.getPlatform(),
                                GameServerLauncher.currentNode.getId(),
                                table.getTableId(),
                                table
                        );

                        table.sendErrorMessage(user, NetworkMessage.getGson().toJson(deckValidation.getError()));

                        return true;
                    }else {
                        longestTile = deckValidation.getLongestTile();
                    }
                }


                user.hasPlayed();
                int isPartnerGame = table.getIsPartner();
                int partnerSide = -1;

                GameUser partner = null;
                if (isPartnerGame == 1) {
                    partner = getPartner(side, table);
                    partnerSide = getPartnerSide(side);
                }

                if (goDouble) {
                    gameEndStatus = GameEndStatus.DOUBLE_FINISH;
                } else {
                    if (card.equals(okeyStr)) {
                        gameEndStatus = GameEndStatus.DROPPED_OKEY;
                    }
                }

                boolean isPotWin = gameEndStatus != GameEndStatus.NORMAL;

                table.setHandOver_time(new Date().getTime());

                table.getEarnedExperienceAndScore(userId, isPotWin, partner);

                long potValue = 0;
                long finalHandOverMoney = 0;
                long logPot = 0;
                long podId = 0;
                if (gameController.getServerType() != ServerType.TOURNAMENT) {
                    table.getWinningMoney(userId, partner);

                    potValue = table.getPotValue();
                    podId = table.getPodId();
                    if (gameEndStatus != GameEndStatus.NORMAL) {
                        table.cutPotMoneyBeforeGiveIt();
                        potValue = table.getPotValue();
                        logPot = potValue;
                        table.BreakPot(userId, partner);
                    }
                    finalHandOverMoney = table.getCutWinningMoney();
                }

                boolean isPaired = partner != null;

                checkOkeyInHand();

                QuestCategoryInfo wonGame = new GameEndCategoryInfo(userId,GameController.getInstance().getServerType() != ServerType.TOURNAMENT, isPaired,gameEndStatus != GameEndStatus.NORMAL && GameController.getInstance().getServerType() != ServerType.TOURNAMENT,gameEndStatus == GameEndStatus.DOUBLE_FINISH,withOkey,longestTile, table.getBet(), withFakeOkey);
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.WON_GAME,wonGame);


                if(partner != null){
                    wonGame = new GameEndCategoryInfo(partner.getfuid(),GameController.getInstance().getServerType() != ServerType.TOURNAMENT, true,false,false,false,0, table.getBet(), false);
                    RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.WON_GAME,wonGame);
                }

                table.sendGameId(NetworkMessage.getGson().toJson(new GameIdResponse(table.getGameId())));
                String data2 = "";
                int game_type = 1;

                HandOverResponse.Builder responseBuilder = new HandOverResponse.Builder();
                responseBuilder.setTableId(tableId)
                        .setWinnerId(userId)
                        .setWhoFinishedSide(side)
                        .setGameEndStatus(gameEndStatus)
                        .setUsersMoneyAndExperience(table.getUsersMoneyAndExperience())
                        .setFinishedHand(finishedHand)
                        .setPotValue(potValue)
                        .setFinalHandOverMoney(finalHandOverMoney)
                        .setPotBreak(gameEndStatus != GameEndStatus.NORMAL)
                        .setWinningMoney(finalHandOverMoney)
                        .setPromotion(true)
                        .setNoMoreGameAtThisTable(gameController.getServerType() == ServerType.TOURNAMENT);

                if (partner == null) {
                    data2 = userId + ";" + gameEndStatus + ";" + ";" + finishedHandLogData + ";" + potValue + ";" + ";" + table.getUsersNameMessage();
                    responseBuilder.setWinneIdList(new String[] { userId });
                } else {
                    game_type = 2;
                    responseBuilder.setPartnerId(partner.getfuid());
                    responseBuilder.setWinneIdList(new String[] { userId, partner.getfuid() });

                    data2 = userId + ";" + gameEndStatus + ";" + ";" + finishedHandLogData + ";" + potValue + ";" + partner.getfuid() + ";" + table.getUsersNameMessage();
                }

                if(gameController.getServerType() == ServerType.GENERIC) {
                    updateUsersWinningStreak(userId, partner);
                    checkFinishAsBotQuest();
                }

                Collection<GameUser> gamers = table.getGamers();

                for(GameUser u : gamers){
                    QuestCategoryInfo gameEndCategoryInfo = new GameEndCategoryInfo(u.getfuid(),GameController.getInstance().getServerType() != ServerType.TOURNAMENT, isPaired, false, false, false, longestTile, table.getBet(),false);
                    RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.END_GAME,gameEndCategoryInfo);

                    QuestCategoryInfo differentDaysCategoryInfo = new DifferentDaysCategoryInfo(u.getfuid(), DifferentDaysType.PLAY_GAME.getValue());
                    RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.DIFFERENT_DAYS,differentDaysCategoryInfo);

                    if (!u.getfuid().equals(userId)) {
                        if (partner == null) {
                            LostGameCategoryInfo lostGameCategoryInfo = new LostGameCategoryInfo(u.getfuid(),table.getBet(),GameController.getInstance().getServerType() != ServerType.TOURNAMENT, isPaired);
                            RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.LOST_GAME,lostGameCategoryInfo);
                        } else {
                            if (!u.getfuid().equals(partner.getfuid())) {
                                LostGameCategoryInfo lostGameCategoryInfo = new LostGameCategoryInfo(u.getfuid(),table.getBet(),GameController.getInstance().getServerType() != ServerType.TOURNAMENT, isPaired);
                                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.LOST_GAME,lostGameCategoryInfo);
                            }
                        }
                    }
                }

                sendGameLogs(partner, isPotWin, potValue, gamers);

                // update tournament processes of users
                if (gameController.getServerType() == ServerType.TOURNAMENT) {
                    for (GameUser u : gamers) {
                        try {
                            int tournamentId = u.getTournamentId();

                            if (u.getfuid().equals(userId) || (partner != null && partner.getfuid().equals(u.getfuid()))) {
                                u.increaseTournamentWinning(tournamentId, table.getGameId());
                            } else {
                                u.increaseTournamentLosing(tournamentId, table.getGameId());
                            }
                            responseBuilder.setUserTournamentModel(u.getTournament(tournamentId));
                            HandOverResponse response = responseBuilder.build();

                            // send hand_over one by one
                            table.sendHandOverMessage(u, NetworkMessage.getGson().toJson(response));

                        } catch (Exception ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }

                } else {
                    HandOverResponse response = responseBuilder.build();
                    table.sendHandOverMessage(NetworkMessage.getGson().toJson(response));
                }


                table.logMove(side, -1, lastCardObj, false, MoveDirection.OUT, MoveDestination.DECK);

                long how_long = table.getHandOver_time() - table.getStart_time();
                TableType tableType = table.getTableType();

                try {
                    GameUser[] sides = new GameUser[3];
                    int sideCounter = 0;
                    for (GameUser u : gamers) {
                        if (!u.getfuid().equals(user.getfuid())) {
                            sides[sideCounter] = u;
                            sideCounter++;
                        }
                        u = null;
                    }

                    StringBuilder potUsers = new StringBuilder();

                    for (String s : table.getPotList()) {
                        potUsers.append(s);
                        potUsers.append("|");
                    }

                    int doubleFinishhed = goDouble ? 1 : 0;
                    RabbitMQLogController.getInstance().winnerLog(user, sides[0], sides[1], sides[2], table.getTableId(), table.getBet(), potValue, Long.toString(table.getGameId()), game_type, doubleFinishhed, how_long, finishedHandLogData, okeyStrLog, lastCardStr, partnerSide, potUsers.toString(),  table.getSideCount(), gameController.getServerType().getValue(), podId, isPotWin, tableType.getValue());

                    try {
                        long totalMoney = table.getBet() * table.getSideCount() + potValue;
                        this.prepareAchievementLog(table, Long.parseLong(user.getfuid()), totalMoney, potValue, lastCardStr.equals(okeyStrLog), goDouble, partnerSide != -1);

                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(), e);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                table.addWinners(userId, logPot, table.getGamersFuidsString());

                if (gameController.getServerType() == ServerType.TOURNAMENT) {
                    table.setGameStatus(GameStatus.CANT_START); // to refrain play on same table again
                    table.resetAfterHandOver(false);

                    try {
                        if (table.isEmpty() && table.isDynamical()) {
                            table.setTableDeleteTimer(System.currentTimeMillis());
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    table.setGameStatus(GameStatus.NOTSTARTED);

                    KickModel kickModel = new KickModel(gameEndStatus, user.getfuid(), user.getName(), !gameEndStatus.equals(GameEndStatus.NORMAL), isPartnerGame == 1,finishedHand, potValue, CardMap.getInstance().getCardId(table.getCardHandler().getOkey()), table.getBet());
                    if(partner != null){
                        kickModel.setPartnerFuid(partner.getfuid());
                        kickModel.setPartnerName(partner.getName());
                    }
                    table.kickPoorPlayers(kickModel);
                    table.resetAfterHandOver(false);
                }

                if (gameController.isInMaintenance()) {
                    //TODO: send maintanence message
                }
                table.resetGameId();
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return true;
    }

    private void sendGameLogs(GameUser partner, boolean isPotWin, long potValue, Collection<GameUser> gamers) {
        for (GameUser u : gamers) {
            if (!u.getfuid().equals(userId)) {
                if (partner == null) {
                    table.sendUserGameLog(0, false, u, potValue, isPotWin);
                } else {
                    if (!u.getfuid().equals(partner.getfuid())) {
                        table.sendUserGameLog(0, false, u, potValue, isPotWin);
                    } else {
                        table.sendUserGameLog(1, false, u, potValue, isPotWin);
                    }
                }
            } else {
                table.sendUserGameLog(1, false, u, potValue, isPotWin);
            }
        }
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Hand Over";
    }

    private List<HandOverCardModel> checkCompleteDouble(String fCard, OkeyCard okey, Table table, OkeyCard lastCardObj, List<HandOverCardModel> finishedHand) throws Exception {

        Vector<OkeyCard> cards = table.getCurrentHand();

        if (cards.size() < 14) {
            ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LESS_THAN_14).createErrorResponse();
            table.sendErrorMessage(user,NetworkMessage.getGson().toJson(error));

            handOverLog(
                    user.getfuid(),
                    table.getGameId() + "",
                    table.getCardHandler().getSideHand(table.getGameTurn()),
                    fCard,
                    "cardSizeLessThen14d",
                    cards.size() + "",
                    user.getPlatform(),
                    GameServerLauncher.currentNode.getId(),
                    table.getTableId(),
                    table
            );

            return null;
        }

        List<List<OkeyCard>> cardGroups = new ArrayList<>();
        List<OkeyCard> chkcards = new ArrayList<>();
        List<OkeyCard> okeys = new ArrayList<>();
        OkeyCard chkAtilan = null;

        Map<String,Integer> positionMap = new HashMap<>();

        for (OkeyCard okeyCard: cards){
            String okeyCardId = CardMap.getInstance().getCardId(okeyCard);

            List<HandOverCardModel> handOverCardModelList = finishedHand.stream().filter(x-> x.getId().equals(okeyCardId)).collect(Collectors.toList());

            if(!handOverCardModelList.isEmpty()) {
                HandOverCardModel handOverCardModel = handOverCardModelList.get(0);

                if (handOverCardModel != null) {
                    positionMap.put(okeyCardId, handOverCardModel.getX());
                }
            }else{
                positionMap.put(okeyCardId, okeyCard.getXPos());
            }
        }


        for (OkeyCard card : cards) { // Cift grup icin
            if ((card.getCardType() + ";" + card.getCardNumber()).equals(fCard) && chkAtilan == null) {
                chkAtilan = card;
                continue;
            }

            if (card.isOkey(okey)) {
                okeys.add(card);
                continue;
            }

            for (OkeyCard chkcard : cards) {
                if (card.isMatchingSameCard(chkcard) && (!chkcards.contains(chkcard) && !chkcards.contains(card)) && !chkcard.equals(chkAtilan)) {

                    int cardXpos = positionMap.get(CardMap.getInstance().getCardId(card));
                    int chkcardXpos = positionMap.get(CardMap.getInstance().getCardId(chkcard));

                    if(Math.abs(cardXpos-chkcardXpos)== 1){
                        List<OkeyCard> cardGroup = new ArrayList<>();
                        cardGroup.add(card);
                        cardGroup.add(chkcard);
                        cardGroups.add(cardGroup);
                        chkcards.add(card);
                        chkcards.add(chkcard);
                        break;
                    }
                }
            }
        }

        if (okeys.size() > 2) {
            ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.OKEY_MORE_THAN_2).createErrorResponse();
            table.sendErrorMessage(user,NetworkMessage.getGson().toJson(error));

            handOverLog(
                    user.getfuid(),
                    table.getGameId() + "",
                    table.getCardHandler().getSideHand(table.getGameTurn()),
                    fCard,
                    "okeyMoreThen2",
                    okeys.size() + "",
                    user.getPlatform(),
                    GameServerLauncher.currentNode.getId(),
                    table.getTableId(),
                    table
            );

            return null;
        }

        if (cardGroups.size() == 6 && okeys.size() == 2) {


            int okeyFirstXpos = positionMap.get(CardMap.getInstance().getCardId(okeys.get(0)));
            int okeySecXpos = positionMap.get(CardMap.getInstance().getCardId(okeys.get(1)));

            if(Math.abs(okeyFirstXpos-okeySecXpos) == 1){
                cardGroups.add(okeys);
            }
        }

        if (okeys.size() > 0 && okeys.size() + chkcards.size() < 14 && cardGroups.size() != 7) {
            for (OkeyCard ok : okeys) {
                for (OkeyCard card : cards) {
                    if (!chkcards.contains(card)) {
                        List<OkeyCard> cardGroup = new ArrayList<>();


                        int okXpos = positionMap.get(CardMap.getInstance().getCardId(ok));
                        int cardXpos = positionMap.get(CardMap.getInstance().getCardId(card));

                        if(Math.abs(okXpos - cardXpos) == 1){
                            cardGroup.add(ok);
                            cardGroup.add(card);
                            cardGroups.add(cardGroup);
                            chkcards.add(card);
                            break;
                        }
                    }
                }
            }
        }

        if (cardGroups.size() != 7) {
            ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LESS_THAN_7_DOUBLE).createErrorResponse();
            table.sendErrorMessage(user,NetworkMessage.getGson().toJson(error));

            handOverLog(
                    user.getfuid(),
                    table.getGameId() + "",
                    table.getCardHandler().getSideHand(table.getGameTurn()),
                    fCard,
                    "doubleSizeLessThen7",
                    cardGroups.size() + "",
                    user.getPlatform(),
                    GameServerLauncher.currentNode.getId(),
                    table.getTableId(),
                    table
            );

            return null;
        }

        return sortForIstaka(cardGroups);
    }

    public static void handOverLog(String gamer_id, String gid, String gHand, String sHand, String wErr, String dErr, String plat, int lobbyID, long tableID, Table table) {
        try {
            if (dErr.equals("")) {
                dErr = "-";
            }
            if (sHand.equals("")) {
                sHand = "-";
            }

            dErr = dErr.replace(",", "-");
            dErr = dErr.replace("'", "#");

            String okey = table.getCardHandler().getOkey().toString();

            String parameters = gamer_id + "," + gid + "," + gHand + ", " + sHand + " ," + wErr + ", " + dErr + " , " + plat + "," + lobbyID + "," + tableID + "," + okey + "," + GameController.getInstance().getVersion();
            RabbitMQLogController.getInstance().addWinnerLogDbLog(new WinnerLogDbData(WinnerDbLogType.HAND_OVER_LOG, parameters, DataSourceType.WINNER_LOGS));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private void prepareAchievementLog(Table table, long winner, long totalMoney, long potMoney, boolean isLastCardOkey, boolean isPairOrder, boolean hasPartner) {
        short tableId = (short) table.getTableId();
        short roomId = (short) table.getRoomId();
        long gameId = table.getGameId();

        long[] players = new long[4];
        long[] botHolders = new long[4];
        int index = 0;
        int botHoldersIndex = 0;
        for (GameUser u : table.getGamers()) {
            long user = Long.parseLong(u.getfuid());
            players[index++] = user;
            if (table.getBotHolders().contains(user)) {
                botHolders[botHoldersIndex++] = user;
            }
        }

        long owner = 0;
        try {
            owner = Long.parseLong(table.getOwner());
        } catch (Exception ex) {
            owner = -1;
        }

        long[] quickPlayers = new long[4];
        for (int i = 0; i < players.length; i++) {
            long player = players[i];
            if (table.getQuickPlayers().contains(player)) {
                quickPlayers[i] = player;
            }
        }

    }


    private GameUser getPartner(int side, Table table) {
        return table.getGamer(getPartnerSide(side));
    }

    private int getPartnerSide(int side) {

        int pSide = 0;
        switch (side) {
            case 0:
                pSide = 1;
                break;
            case 1:
                pSide = 0;
                break;
            case 2:
                pSide = 3;
                break;
            case 3:
                pSide = 2;
                break;
            default:
                break;
        }
        return pSide;
    }

    public OkeyCard extractLastCard(Vector<OkeyCard> allCards, ArrayList<OkeyCard> finishingCards){
        OkeyCard myShouldBeLastCard = null;
        for(OkeyCard c : allCards){
            boolean found = false;
            for(OkeyCard c2 : finishingCards){
                if(c.isEqual(c2)){
                    found = true;
                    break;
                }
            }

            if(!found){
                myShouldBeLastCard = c;
                break;
            }
        }

        return myShouldBeLastCard;
    }

    private List<OkeyCard> swapToRealPer(List<OkeyCard> tc) {

        List<OkeyCard> ntc = new ArrayList<OkeyCard>();
        int i = 0;

        for (OkeyCard card : tc) {
            if (card.getFakeOkey() == 1) {

                String[] fCard = new String[5];

                //[i+"","4","14"];
                fCard[0] = card.getRealBucket() + "";
                fCard[1] = "4";
                fCard[2] = "14";
                fCard[3] = card.getXPos() + "";
                fCard[4] = card.getYPos() + "";

                OkeyCard newCard = new OkeyCard(fCard);
                ntc.add(newCard);
                i++;
            } else {
                ntc.add(card);
            }

        }

        return ntc;
    }

    private List<HandOverCardModel> sortForIstaka(List<List<OkeyCard>> finishCardGroups) {

        List<HandOverCardModel> deck = new ArrayList<>();

        int x = 1;
        int y = 1;
        StringBuilder str = new StringBuilder();

        for (List<OkeyCard> cards : finishCardGroups) {
            for (OkeyCard card : cards) {

                if (x == 13) {
                    y = 2;
                    x = 1;
                }

                HandOverCardModel handOverCard = new HandOverCardModel(CardMap.getInstance().getCardId(card),card.getXPos(),card.getYPos());
                deck.add(handOverCard);
                x++;
            }
            x++;
        }

        return deck;
    }

    private void updateUsersWinningStreak(String userId, GameUser partner){
        Collection<GameUser> gamers = table.getGamers();

        for (GameUser u : gamers) {
            if(u.getfuid() == userId || (partner != null && partner.getfuid().equals(u.getfuid()))){
                QuestCategoryInfo questCategoryInfo = new StreakCategoryInfo(u.getfuid(), StreakType.WINNING.getValue(), false);
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.STREAK,questCategoryInfo);
            }
            else {
                QuestCategoryInfo questCategoryInfo = new StreakCategoryInfo(u.getfuid(), StreakType.WINNING.getValue(), true);
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.STREAK,questCategoryInfo);
            }
        }
    }

    private void checkOkeyInHand(){
        for(OkeyCard c :table.getCurrentHand()){
            if(c.isOkey(table.getCardHandler().getOkey())) withOkey = true;
            if(c.getCardType() == OkeyCard.Type.FAKE.getValue()) withFakeOkey = true;
        }
    }

    private void checkFinishAsBotQuest(){
        Collection<GameUser> gamers = table.getGamers();

        for (GameUser u : gamers) {
            if(GameController.getInstance().getServerType() != ServerType.TOURNAMENT && !u.getHasMoneyInPot()){
                QuestCategoryInfo questCategoryInfo = new BotCategoryInfo(u.getfuid());
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.BOT,questCategoryInfo);
            }
        }
    }

    public String getFinishedHandLogData(){
        String data = "";

        if(finishedHand != null) {
            for (HandOverCardModel card : finishedHand) {
                data += card.serialize();
            }
        }

        return data;
    }
}
