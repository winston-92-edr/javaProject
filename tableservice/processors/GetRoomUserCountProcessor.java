package com.mynet.tableservice.processors;

import com.google.gson.Gson;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.socialserver.model.RoomUserCountModel;
import com.mynet.tableservice.response.GetRoomsUsersCountResponse;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class GetRoomUserCountProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GetRoomUserCountProcessor.class);
    private Gson gson;

    public GetRoomUserCountProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TableService tableService = TableService.getInstance();
        ServiceProxyDataWrapper wrapper = gson.fromJson(message.getData(), ServiceProxyDataWrapper.class);
        String userId = message.getId();
        int proxyId = wrapper.getProxyId();
        ArrayList<RoomUserCountModel> info = tableService.getRoomUserCount();

        NetworkMessage response = new NetworkMessage(GameCommands.GET_ROOMS_USERS_COUNT);
        response.setDataAsJSON(new GetRoomsUsersCountResponse(info));
        tableService.sendNetworkMessage(userId, proxyId, response);
    }
}
