package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.shared.connection.NodeToService;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.actions.AddTableAction;
import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableService;

public class AddTableProcessor implements MessageProcessor {
    private Gson gson;

    public AddTableProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TableService tableService = TableService.getInstance();

        String data = message.getData();
        ServiceTableModel model = gson.fromJson(data, ServiceTableModel.class);
        tableService.addTable(model);
    }
}
