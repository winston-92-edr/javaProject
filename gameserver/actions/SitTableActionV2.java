package com.mynet.gameserver.actions;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.response.TableInfoResponse;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.types.GamePlayStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SitTableActionV2 extends TableAction {
    private static final Logger logger = LoggerFactory.getLogger(SitTableActionV2.class);

    private long tableId;
    private int side;
    private boolean isFromQuickPlay;

    public SitTableActionV2(Table table, GameUser user, long tableId, int side, boolean isFromQuickPlay) {
        super(table, user);
        this.tableId = tableId;
        this.side = side;
        this.isFromQuickPlay = isFromQuickPlay;
    }

    @Override
    public boolean process() {
        try {
            NetworkMessage response = new NetworkMessage(GameCommands.SIT_TABLE_2);

            if (table.checkGamersIp(user)) {
                GameController.getInstance().sendNetworkMessage(user, GameCommands.SAME_IP, "");
                return false;
            }

            if (user.getTableId() > 0) {
                response.setData("-1;NotAllowed");
                GameController.getInstance().getNodeToProxy().addServerMessage(response, user);
            }
            else {
                Room room = GameController.getInstance().getRoom(table.getRoomId());
                if(room != null) {
                    boolean canSit = true;
                    if(user.getRoomId() != table.getRoomId()){
                        canSit = GameController.getInstance().enterRoom(table.getRoomId(), user);
                    }

                    if (canSit) {
                        String sitTableSit = Quick_Start_Game.sitTableSide(table, user, side, isFromQuickPlay);
                        response.setData(sitTableSit);
                        GameController.getInstance().getNodeToProxy().addServerMessage(response, user);
                        if (!sitTableSit.startsWith("-1")) {
                            if (table.getGameStatus() == GameStatus.PLAYING) {
                                TableInfoResponse tableData = table.getTableData(side);
                                table.continueGame(user, tableData, side);
                            }
                        }
                    }else{
                        response.setData("-1;NotAllowed");
                        GameController.getInstance().getNodeToProxy().addServerMessage(response, user);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return true;
    }

    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Sit Table2";
    }
}
