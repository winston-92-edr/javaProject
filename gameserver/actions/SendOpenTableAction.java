package com.mynet.gameserver.actions;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendOpenTableAction extends TableAction {
    private static Logger logger = LoggerFactory.getLogger(SendOpenTableAction.class);

    private final int side;
    private final long tableId;

    public SendOpenTableAction(Table table, GameUser user, long tableId, int side) {
        super(table, user);
        this.tableId = tableId;
        this.side = side;
    }

    @Override
    public boolean process() {
//        GameController controller = GameController.getInstance();
//
//        ErrorCode errorCode = null;
//
//        try {
//            int roomId = table.getRoomId();
//            Room room = controller.getRoom(roomId);
//            errorCode = Quick_Start_Game.canSendOpenTable(user, room);
//            if (errorCode == null) {
//                room.AddUser(user);
//                String data = roomId + "_|_" + table.getTableId()
//                        + "_|_" + Quick_Start_Game.joinAnAudience(user, table)
//                        + "_|_" + Quick_Start_Game.sitTableSide(table, user, side, true);
//                controller.sendNetworkMessage(user, GameCommands.OPEN_TABLE_2, data);
//            } else {
//                ErrorResponse errorResponse = new ErrorResponse(errorCode);
//                controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(errorResponse));
//            }
//        } catch (Exception e) {
//            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_OPEN_TABLE);
//            controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(errorResponse));
//            logger.error(e.getMessage(), e);
//        }
        return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Send Open Table";
    }
}
