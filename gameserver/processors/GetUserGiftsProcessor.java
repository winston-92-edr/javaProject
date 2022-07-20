package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.response.UserGiftsResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GetUserGiftsProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());
            HashSet gifts = user.getGifts();

            controller.sendNetworkMessage(user, GameCommands.GET_USER_GIFTS, NetworkMessage.getGson().toJson(new UserGiftsResponse(new ArrayList<String>(gifts))));
        }catch (Exception e){

        }
    }
}
