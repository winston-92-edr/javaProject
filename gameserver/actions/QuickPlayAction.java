package com.mynet.gameserver.actions;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.resource.CacheController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickPlayAction extends TableAction {
    private static Logger logger = LoggerFactory.getLogger(QuickPlayAction.class);

    private long tableId;
    private int side;

    public QuickPlayAction(Table table, GameUser user, long tableId, int side) {
        super(table, user);
        this.tableId = tableId;
        this.side = side;
    }

    @Override
    public boolean process() {
        GameController lobby = GameController.getInstance();
        try {

            if (user.getTableId() > 0) {
                Table table = lobby.getTable(user.getTableId());
                if (table != null) {
                    table.removeUserProcess(user, true);
                }
            }

            int roomId =  Quick_Start_Game.enterRoom(table.getRoomId(), user);
            String sitTableSideStr = "";

            String audienceStr = Quick_Start_Game.joinAnAudience(user, table);
            if (table.getGameStatus() == GameStatus.NOTSTARTED) {
                sitTableSideStr = Quick_Start_Game.sitTableSide(table, user, side, true);
            }

            String data =  roomId + "_|_" + table.getTableId()
                    + "_|_" + audienceStr
                    + "_|_" + sitTableSideStr;

            lobby.sendNetworkMessage(user, GameCommands.QUICK_PLAY_2, data);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (table != null) {
                lobby.sendNetworkMessage(user, GameCommands.QUICK_PLAY_2, "-1");
            } else {
                logger.error("table is null " + tableId);
            }
        }
        return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Quick Play";
    }
}
