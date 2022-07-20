package com.mynet.gameserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;

public class GameStatusProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        int tableId = user.getTableId();
        if (tableId == -1) {
            controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.TABLE_NULL)));
        }
        Table table = controller.getTable(tableId);
        if (table != null) {
            table.sendGameStatus(user);
        }else{
            controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.TABLE_NULL)));
        }
    }
}
