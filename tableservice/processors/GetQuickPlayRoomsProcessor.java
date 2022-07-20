package com.mynet.tableservice.processors;

import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.model.QuickPlayRoomModel;
import com.mynet.tableservice.response.GetQuickPlayRoomsResponse;
import com.mynet.tableservice.service.ServiceUser;
import com.mynet.tableservice.service.TableService;
import java.util.List;

public class GetQuickPlayRoomsProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TableService tableService = TableService.getInstance();
        ServiceUser user = tableService.getUser(message.getId());

        if(user != null){
            List<QuickPlayRoomModel> rooms = tableService.getQuickPlayRooms();
            GetQuickPlayRoomsResponse response = new GetQuickPlayRoomsResponse(rooms);
            NetworkMessage networkMessage = new NetworkMessage(GameCommands.GET_QUICK_PLAY_ROOMS);
            networkMessage.setDataAsJSON(response);
            tableService.sendNetworkMessage(message.getId(), user.getUser().getProxyId(), networkMessage);
        }
    }
}
