package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class SendTableInfoProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

//        GameController controller = GameController.getInstance();
//        GameUser user = controller.getUser(message.getId());
//
//        if (user == null) return;
//        if (user.getTableId() == -1) return;
//
//        Table table = controller.getTable(user.getTableId());
//        String tableData = table.getTableData(table.getGamerSide(user.getfuid()));
//        controller.sendNetworkMessage(user, GameCommands.SEND_TABLE_INFO, tableData);
    }
}
