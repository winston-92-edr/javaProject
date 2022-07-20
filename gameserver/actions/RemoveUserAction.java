package com.mynet.gameserver.actions;

import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.gameserver.enums.RemoveUserMessages;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.response.LeftTableResponse;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;

import java.util.ArrayList;
import java.util.Collection;

public class RemoveUserAction extends TableAction {

    private static Logger logger = LoggerFactory.getLogger(RemoveUserAction.class);

    private String processName;
    public RemoveUserAction(Table table, GameUser user, String processName) {
        super(table, user);
        this.processName = processName;
    }

    @Override
    public boolean process() {
        try {
            if (!table.containsUser(user)) {
                logger.warn(String.format("TABLE: %d NOT CONTAINS USER: %s WHILE LEFT_TABLE", table.getTableId(), user.getId()));
                return true;
            }
            GameController gameLobby = GameController.getInstance();

            gameLobby.sendNetworkMessage(user, GameCommands.CHAT_LEFT_TABLE, "");

            if (!gameLobby.isGeneric()) {

                ArrayList<String> potList = table.getPotList();
                Collection<GameUser> users = table.getGamers();
                GameStatus status = table.getGameStatus();
                long gameId = table.getGameId();
                long bet = table.getBet();
                long handOverTime = table.getHandOver_time();
                long potValue = table.getPotValue();

                user.setSuspended(false);

                if (status == GameStatus.PLAYING || table.getHandOver_time() > 0) {
                    for(GameUser user : users) {
                        try {
                            // table.removeUserProcess(user, true);
                            // sen kaldır , bu fonksiyon status u değiştiriyor
                            long userPotValue = 0;
                            if (!user.getfuid().equals(user.getfuid())) {
                                // this player didn't want to intent to quit the game, she is innocent, we have shown mercy on her
                                if (user.getHasMoneyInPot() && potList.contains(user.getfuid())) {
                                    // she has gave money to pot
                                    userPotValue = potValue;
                                }
                                user.setLastTableId(user.getTableId());
                                user.resetEvent(false, table);
                            }

                        }  catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    // stops the game, and sends winning screens for others if the game is playing
                    table.removeUserProcess(user, false);
                } else {
                    user.changeEventType(EventDbLogType.MATCHMAKING_CANCEL);
                    table.removeUserProcess(user, false);
                    // the game is already stopped or never started
                    if (table.getGameId() == 0) {
                        for(GameUser user : users) {
                            if (!user.getfuid().equals(user.getfuid())) {
                                user.changeEventType(EventDbLogType.MATCHMAKING_CANCEL_BY_OP);
                                table.removeUserProcess(user, false);
                                gameLobby.sendNetworkMessage(user, GameCommands.LEFT_TABLE, NetworkMessage.getGson().toJson(new LeftTableResponse(RemoveUserMessages.DEFAULT)));
                            }
                        }
                    }
                }
            } else {
                user.setLastTableId(user.getTableId());
                user.resetEvent(false,table);
                if (table.containsUser(user)) {
//                    if (this.processName == "removeUser2Process") {
                        Room room = gameLobby.getRoom(user.getRoomId());
                        table.removeUser2Process(user, false);

                        gameLobby.sendNetworkMessage(user, GameCommands.LEFT_TABLE, NetworkMessage.getGson().toJson(new LeftTableResponse(RemoveUserMessages.DEFAULT)));

                        //TODO:TOLGA table service will send table info
                        //Oyuncu cıkıgı zaman bulundugu room un masalarını dön.
//                    } else {
//                        gameLobby.sendNetworkMessage(user, GameCommands.LEFT_TABLE_VOLUNTEER, NetworkMessage.getGson().toJson(new LeftTableResponse(RemoveUserMessages.DEFAULT)));
//                        table.removeUserProcess(user, false);
//                    }
                }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Remove User";
    }
}
