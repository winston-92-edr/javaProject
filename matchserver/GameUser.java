package com.mynet.matchserver;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.gameserver.logs.EventDbLogData;
import com.mynet.gameserver.model.VisitedTable;
import com.mynet.gameserver.okey.Table;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.category.TournamentCategoryInfo;
import com.mynet.shared.analytics.model.SitTableSpecialEvent;
import com.mynet.shared.logs.*;
import com.mynet.shared.model.*;
import com.mynet.shared.resource.db.DatabaseWork;
import com.mynet.shared.resource.db.DatabaseWorker;
import com.mynet.shared.resource.db.work.UpdateExperienceAndScore;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.types.DataSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.IPlayer;
import com.mynet.shared.user.User;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

public class GameUser extends User implements IPlayer {
    private static Logger logger = LoggerFactory.getLogger(GameUser.class);
    private int proxyId;
    private int gameId;
    private int tableId;
    private int roomId;
    private int tournamentId = -1;

    //game play parameters
    private boolean hasMoneyInPot;
    private EventDbLogData event;
    private int notPlayedTurns = 0;
    private int side = -1;
    private ArrayList<Integer> lastTableIds = new ArrayList<>();
    private boolean suspended;

    private SitTableSpecialEvent tableEvent;
    private VisitedTable visitedTable;

    public GameUser(UserModel model) {
        super(model);
        processLock = new ReentrantLock();
        tournamentModels = new Hashtable<>();
        refreshNodeInfo();
    }

    public void refreshNodeInfo(){
        UserModel userGameModel = CacheController.getInstance().getUserGameModel(this.id);
        this.gameId = userGameModel.gameID;
        this.proxyId = userGameModel.proxyID;
        this.tableId = userGameModel.tableID;
    }

    public void updateBasicUser(BasicUserModel model){
        if(this.userModel != null){
            this.userModel.money = model.getMoney();
            this.userModel.vip = model.isVip();
            this.userModel.tickets = model.getTicket();
        }
    }

    public void storeTableId(int tableId){
        if(visitedTable == null){
            visitedTable = new VisitedTable();
        }

        visitedTable.storeTable(tableId);
    }

    public boolean isStoredTable(int tableId){
        return visitedTable != null && visitedTable.isStored(tableId);
    }

    public void resetStoredTable(){
        visitedTable = null;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
        this.userModel.tableID = tableId;
        CacheController.getInstance().setTableId(this.id, tableId);
    }

    public void setGameId(int gameId){
        if(this.userModel != null){
            this.userModel.gameID = gameId;
        }
        this.gameId = gameId;
    }

    public void resetTableId(){
        setTableId(-1);
        this.userModel.tableID = -1;
        CacheController.getInstance().setTableId(this.id, -1);
    }

    public void resetRoom(){
        setRoomId(0);
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getId() {
        return id;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getTableId() {
        return tableId;
    }

    public boolean hasTable(){
        return tableId != -1;
    }

    public BasicUserModel getBasicUser() {
        BasicUserModel userModel = new BasicUserModel(id, this.userModel.name, platform, this.userModel.money, this.userModel.vip);
        userModel.setProxyId(proxyId);
        userModel.setRoomId(roomId);
        userModel.setTournamentId(tournamentId);
        return userModel;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public boolean getHasMoneyInPot() {
        return hasMoneyInPot;
    }

    public void setLastTableId(int tableId) {
        if (!this.lastTableIds.contains(tableId)) {
            if (this.lastTableIds.size() >= 3) {
                this.lastTableIds.remove(0);
            }
            this.lastTableIds.add(tableId);
        }
    }

    public Boolean isInLastTableIds(int tableId) {
        return this.lastTableIds.contains(tableId);
    }

    public boolean hasRoom() {
        return roomId > 0;
    }
    public void setRoomId(int roomId){
        this.roomId = roomId;
        CacheController.getInstance().setUserRoomID(roomId, id);
    }

    public int getRoomId() {
        return roomId;
    }

    public void increaseTournamentWinning(int tournamentId, long gameId) {
        try {
            ErrorCode error = increaseTournamentLevel(tournamentId);
            if (error != null) {
                NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                response.setDataAsJSON(new ErrorResponse(error));
                logger.error("increaseTournamentWinning " + error.getValue());
                GameController.getInstance().getNodeToProxy().addServerMessage(response, this);
            } else {

                beginTournamentEvent(TournamentEventLog.Type.WIN);
                fillTournamentEvent(tournamentId, true, gameId, 0, 0);
                endTournamentEvent();

                this.incrementWonTournamentGames(tournamentId);
                this.incrementTotalTournamentGames(tournamentId);

            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private ErrorCode increaseTournamentLevel(int tournamentId) {

        ErrorCode error = null;

        UserTournamentModel tModel = getTournament(tournamentId);
        if (tModel == null) {
            return ErrorCode.TOURNAMENT_NOT_AVAILABLE;
        }

        if (tModel.isOver()) {
            // tournament is over
            return ErrorCode.GENERAL_ERROR;
        }

        TournamentModel tournament = TournamentLevelController.getInstance().getTournament(tournamentId);

        int nextLevelId = tModel.level + 1;

        TournamentLevel nextLevelInfo = tournament.getLevel(nextLevelId);
        if (nextLevelInfo == null) {
            // something wrong
            logger.error("ERROR increaseTournamentLevel: " + nextLevelId);
            return ErrorCode.GENERAL_ERROR;
        } else {
            // increase level
            tModel.level = nextLevelId;
            tModel.title = nextLevelInfo.getTitle();
            tModel.desc = nextLevelInfo.getDescription();
            tModel.award = nextLevelInfo.getAward();
            tModel.isLastLevel = nextLevelId >= tournament.getMaxLevelId();

            if (tModel.isOver()) {
                tModel.state = tModel.isSucceed() ? UserTournamentState.SUCCESS : UserTournamentState.FAILED;
            }

            if(!tournament.isActive() || tournament.getStartDate() > tModel.endDate){
                tModel.state = UserTournamentState.FAILED;
                tModel.lostGames = tournament.getFailPoint();
                tModel.remainingTryCount = 0 ;
                tModel.expired = true;
            }

            QuestCategoryInfo questCategoryInfo = new TournamentCategoryInfo(getfuid(),tournament.getTournamentId(),tournament.getSideCount(),tModel.level,tournament.getType() == 1,tModel.isSucceed());
            RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.TOURNAMENT,questCategoryInfo);

            if(tournament.getLevels().size() < tModel.level){
                tModel.level = tournament.getLevels().size();
            }

            setTournament(tournament.getTournamentId(), tModel);
        }

        this.setMaxLevel(tournamentId, tModel.level);

        return error;
    }

    public void increaseTournamentLosing(int tournamentId, long gameId) {
        try {
            UserTournamentModel tModel = getTournament(tournamentId);


            TournamentModel tournament = TournamentLevelController.getInstance().getTournament(tournamentId);
            if(tournament.getFailPoint() > tModel.lostGames){
                tModel.lostGames += 1;
            }

            if (tModel.isOver()) {
                tModel.state = tModel.isSucceed() ? UserTournamentState.SUCCESS : UserTournamentState.FAILED;
            }

            if(!tournament.isActive() || tournament.getStartDate() > tModel.endDate) {
                tModel.state = UserTournamentState.FAILED;
                tModel.lostGames = tournament.getFailPoint();
                tModel.remainingTryCount = 0 ;
                tModel.expired = true;
            }

            if(tournament.getLevels().size() < tModel.level){
                tModel.level = tournament.getLevels().size();
            }

            setTournament(tournamentId, tModel);

            beginTournamentEvent(TournamentEventLog.Type.LOST);
            fillTournamentEvent(tournamentId, true, gameId, 0, 0);
            endTournamentEvent();

            this.incrementTotalTournamentGames(tournamentId);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public String getfuid() {
        return id;
    }


    @Override
    public void changeEventType(EventDbLogType type) {

    }

    public void beginTournamentEvent(TournamentEventLog.Type type) {
        this.tournamentEventLog = new TournamentEventLog(Long.parseLong(this.getId()), type);
    }

    public void fillTournamentEvent(int tournamentId, boolean success, long gameId, int cost, long amount) {
        if (tournamentEventLog != null) {
            UserTournamentModel tournamentModel = getTournament(tournamentId);
            TournamentModel tournament = TournamentLevelController.getInstance().getTournament(tournamentId);

            tournamentEventLog.setPlatform(this.getPlatform());
            tournamentEventLog.setLevel(tournamentModel.level);
            tournamentEventLog.setTid(tournamentModel.tid);
            tournamentEventLog.setTournament_id(tournamentId);
            tournamentEventLog.setSuccess(success);
            tournamentEventLog.setGame_id(gameId);
            tournamentEventLog.setLosses(tournamentModel.lostGames);
            tournamentEventLog.setRem_try_count(tournamentModel.remainingTryCount);
            tournamentEventLog.setPlayer_count(tournament.getSideCount());
            tournamentEventLog.setCost(cost);
            tournamentEventLog.setAmount(amount);
        }
    }

    public void endTournamentEvent() {
        if (tournamentEventLog != null) {
            tournamentEventLog.setEnd_time(System.currentTimeMillis());
            RabbitMQLogController.getInstance().addTournamentEventLog(tournamentEventLog);
        }
        tournamentEventLog = null;
    }


    @Override
    public void setEvent(EventDbLogType type) {
        setEvent(type, false);
    }

    public void setEvent(EventDbLogType type, boolean botSit) {
        this.event = new EventDbLogData(this, type, botSit ? 1 : 0, ServerType.TOURNAMENT);
    }

    @Override
    public EventDbLogData getEvent() {
        return event;
    }

    @Override
    public void resetEvent(boolean status, Table table) {
        //TODO : reset events
//        try {
//            if (event != null) {
//                event.fillTableData(table);
//                event.setSuccess(status);
//                event.setEnd(System.currentTimeMillis());
//                event.setGameID(table != null ? table.getGameId() : 0);
//                if (table != null || !lobby.isGeneric()) {
//                    lobby.addEventLogDbLog(event);
//                }
//                event = null;
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//            event = null;
//        }
    }

    @Override
    public BasicUserModel getBasicUserResponse() {
        return null;
    }

    @Override
    public void setHasMoneyInPot(boolean value) {
        hasMoneyInPot = value;
    }

    @Override
    public boolean getIsVip() {
        return userModel.vip;
    }

    @Override
    public long getMoney() {
        return userModel.money;
    }

    @Override
    public boolean isFakeUser() {
        return false;
    }

    public int notPlayed() {
        return notPlayedTurns++;
    }

    public void hasPlayed() {
        notPlayedTurns = 0;
    }

    public String getPlatform() {
        return platform;
    }

    public String getName() {
        return userModel.name;
    }

    public int getIsVipAsInt() {
        return userModel.vip ? 1 : 0;
    }

    public boolean canAfford(int requiredMoney) {
        return userModel.money >= Math.abs(requiredMoney);
    }

    public void writeUserMoneyLog(String from, long game_id, long amount, long log_time) {
        try {
            if (!id.equals("-1")) {
                String parameters = id + "," + amount + "," + log_time + "," + from + "," + game_id + "," + this.userModel.money + "," + this.platform;
                RabbitMQLogController.getInstance().addWinnerLogDbLog(new WinnerLogDbData(WinnerDbLogType.UPDATE_USER_MONEY_LOG, parameters, DataSourceType.WINNER_LOGS));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setSuspended(boolean value){
        this.suspended = value;
    }

    public boolean isSuspended(){
        //return this.suspended;
        return false;
    }

    public SitTableSpecialEvent getTableEvent() {
        return tableEvent;
    }

    public void setTableEvent(SitTableSpecialEvent tableEvent) {
        this.tableEvent = tableEvent;
    }

    public void resetTableEvent() {
        this.setTableEvent(null);
    }

    public void dbUpdateExperienceAndScore() {
        try {
            UpdateExperienceAndScore updateExperienceAndScore = new UpdateExperienceAndScore(this.id, this.userModel.gamesWon, this.userModel.gamesLost, this.userModel.gamesTotal, this.userModel.potMax, this.userModel.gamesPot, this.userModel.experience);
            DatabaseWorker.getInstance().addWork(new DatabaseWork(updateExperienceAndScore,null));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public String toString() {
        return "GameUser{" +
                "proxyId=" + proxyId +
                ", gameId=" + gameId +
                ", tableId=" + tableId +
                ", roomId=" + roomId +
                ", hasMoneyInPot=" + hasMoneyInPot +
                ", notPlayedTurns=" + notPlayedTurns +
                ", side=" + side +
                '}';
    }
}
