package com.mynet.gameserver.actions;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.response.TableInfoResponse;
import com.mynet.gameserver.room.Room;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.UserStateResponse;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.network.StringUtil;

public class Quick_Start_Game {
    private static final Logger logger = LoggerFactory.getLogger(Quick_Start_Game.class);

    public static String getIdleUsersInRoom() {
        //TODO:CISOT fix this for generic mode
        return null;
    }

    public static boolean canEnterRoom(Room room, GameUser user) {
        try {
            if (room == null) {
                return false;
            }
            if (room.isVip() && !user.getIsVip()) {
                room.RemoveUser(user);
                return false;
            }
            if (user.getMoney() < room.getMinBet() || room.isLowBet(user.getMoney())) {
                return false;
            }
            return true;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }


    public static int enterRoom(int roomId, GameUser user) {
        try {
            GameController gameController = GameController.getInstance();
            Room room = gameController.getRoom(roomId);
            if (room == null) {
                logger.error("gameserver.room is null " + roomId);
                return -1;
            }

            if (room.isVip() && !user.getIsVip()) {
                gameController.getRoom(roomId).RemoveUser(user);
                return -1;
            }

            if (user.getRoomId() != 0) {
                gameController.getRoom(roomId).RemoveUser(user);
            }

            if (canEnterRoom(room, user)) {
                room.AddUser(user);
                return roomId;
            }
            return -1;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    public static String joinAnAudience(GameUser user, Table table) {
        try {
            GameController gameController = GameController.getInstance();
            if (user.getTableId() > 0 && user.getTableId() != table.getTableId()) {
                Table t = gameController.getTable(user.getTableId());
                t.removeUser(user, true);
            }

            //CHECK: step 1
            if (table.addAudience(user)) {

                String userdata = user.getfuid() + "#"
                        + StringUtil.correctTurkish(user.getName()) + "#"
                        + user.getMoney() + "#" + 1 // 1 is experience fix this
                        + "#" + user.getIsVipAsInt() + "#" + user.getUserGift()
                        + "#" + 0 + "#"
                        + 0 + "#" + 0 + "#" + user.getPlatform() + "#" + user.getTournamentBadge();

                //table.notifyForAudience(userdata);

                String joinData = table.getTableId() + "|"
                        + table.getUsersOfTheTable() + "|"
                        + table.getGameStatus() + "|"
                        + table.getIsPartner() + "|"
                        + table.getPotValue();
                return joinData;

            } else {
                return "-1";
            }

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
            return "-1";

        }

    }

    public static String sitTableSide(Table table, GameUser user, int side, Boolean qp) {
        try {

            if (table != null) {
                int requiredMoney = table.getMinBet();

                if (side == -1) {
                    for (int i = 0; i < 4; i++) {
                        if (table.getGamer(i) == null) {
                            side = i;
                            break;
                        }
                    }
                }

                //CHECK: step 3
                boolean canAfford = user.canAfford(requiredMoney);

                if (table.canEnterToVipTable(user) && canAfford) {

                    if (table.getGamer(side) != null) {
                        if (table.getGamer(side).getfuid().equals(user.getfuid())) {
                            return "-2";
                        } else {
                            return "-1;NotEmpty";
                        }

                    } else {
                        String result;
                        if (!qp) {
                            result = table.getRoomId() + "_|_" + joinAnAudience(user, table) + "_|_" + table.sitTableFromQP(side, user);
                        } else {
                            result = table.sitTableFromQP(side, user);

                            long fuid = Long.parseLong(user.getfuid());
                            if (!table.getQuickPlayers().contains(fuid)) {
                                table.getQuickPlayers().add(fuid);
                            }
                        }
                        return result;
                    }

                } else if (!table.canEnterToVipTable(user) && canAfford) {
                    return "-1;NotVip";
                } else {
                    return "-1;NotMoney";
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return "-1;Err";
        }
        return "-1;Err";
    }

    private static String sitTableEmtySide(Table table, GameUser user) {
        return sitTableSide(table, user, -1, true);
    }

    public static String JoinInviteTable(GameUser user, int roomId, Table tbl) {
        try {

            String data = "";
            int er = enterRoom(roomId, user);

            if (er != -1) {
                data = er + "_|_" + tbl.getTableId() + "_|_" + joinAnAudience(user, tbl);
            } else {
                data = -1 + "_|_" + -1 + "_|_" + -1;
            }

            return data;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "-1;err";
        }
    }

    public static ErrorCode canSendOpenTable(GameUser user, Room room) {
        ErrorCode errorCode = null;

        try {
            if (user.getTableId() > 0) {
                errorCode = ErrorCode.ALREADY_HAVE_TABLE;
            } else if (room == null) {
                errorCode = ErrorCode.ROOM_NULL;
            } else if (room.isVip() && !user.getIsVip()) {
                errorCode = ErrorCode.NOT_VIP_OPEN_TABLE;
            } /*else if (room.getRoomType().getId() < 1) {
                errorCode = ErrorCode.ROOM_NULL;
            }*/ else if (user.getMoney() < room.getMinBet()) {
                errorCode = ErrorCode.NOT_ENOUGH_MONEY_OPEN_TABLE;
            }
        } catch (Exception e) {
            errorCode = ErrorCode.CANNOT_OPEN_TABLE;
            logger.error(e.getMessage(), e);
        }

        return errorCode;
    }

    public static UserStateResponse sendReconnectTable(GameUser user, Table table, boolean reconnect) {
        try {
            GameController gameController = GameController.getInstance();
            logger.info(String.format("[sendReconnectTable] user: %s | table: %d | roomId: %d", user.getfuid(), user.getTableId(), user.getRoomId()));

            Room room = gameController.getRoom(table.getRoomId());

            if (room != null) {
                boolean hasUser = room.hasUser(user.getfuid());
                if (!hasUser) {
                    room.AddUser(user);
                }
            }

            if (table.getGameStatus() == GameStatus.NOTSTARTED || table.getGameStatus() == GameStatus.STOPPED || table.getGameStatus() == GameStatus.GET_READY) {
            } else {
                // add extra time if the turn belongs to the user
                if (table.getGameStatus() == GameStatus.PLAYING && table.getCurrentUser() == user && reconnect) {
                    table.tableWatch.addTimeForReconnect();
                }
            }

            boolean isAudience = table.isAudienceOrGamer(user.getfuid()) == 2;
            int timeToStart = table.tableWatch.getRemainTimeToStart();

            int side = table.getGamerSide(user.getfuid());
            TableInfoResponse tablesInfo = table.getTableData(side);

            String roomName = gameController.getRoomName(table.getRoomId());
            boolean partner = table.getIsPartner() == 1;

            int tournamentId = user.getTournamentId();

            UserStateResponse userState = new UserStateResponse(tablesInfo, isAudience, table.getRoomId(), timeToStart, partner, table.getTableGamers(), table.getTableAudiences(),roomName, tournamentId ,table.getSideCount(), table.getTotalTime());

            return userState;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }
}
