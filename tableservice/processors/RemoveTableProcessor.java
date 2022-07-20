package com.mynet.tableservice.processors;

import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.actions.RemoveTableAction;
import com.mynet.tableservice.service.TableService;

public class RemoveTableProcessor implements MessageProcessor {

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TableService tableService = TableService.getInstance();
        String[] split = message.getData().split(";");
        String tableId = split[0];
        String roomId = split[1];
        tableService.removeTable(Integer.parseInt(tableId), Integer.parseInt(roomId));
    }
}
