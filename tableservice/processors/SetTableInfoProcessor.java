package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.request.TablesInfoRequest;
import com.mynet.tableservice.response.TablesInfoResponse;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SetTableInfoProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(SetTableInfoProcessor.class);
    private Gson gson;

    public SetTableInfoProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TableService tableService = TableService.getInstance();
        ServiceProxyDataWrapper wrapper = gson.fromJson(message.getData(), ServiceProxyDataWrapper.class);
        String userId = message.getId();

        int proxyId = wrapper.getProxyId();
        TablesInfoRequest request = NetworkMessage.CreateMessage(wrapper.getData(), TablesInfoRequest.class);
        int roomId = request.roomId;

        if (request.fullness == null){
            request.fullness = TableFullnessFilter.ALL;
        }

        System.out.println("Tables Fullness: " + request.fullness);

        List tables = tableService.getTablesInfo(userId, roomId, request.paired, request.robot, request.fullness);

        System.out.println("Tables: " + NetworkMessage.getGson().toJson(tables));

        NetworkMessage response = new NetworkMessage(GameCommands.TABLES_INFO);
        int roomCount = tableService.getRoomCount(roomId);
        response.setDataAsJSON(new TablesInfoResponse(tables, roomCount, roomId));
        tableService.sendNetworkMessage(userId, proxyId, response);
    }
}
