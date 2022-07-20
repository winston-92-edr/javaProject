package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.response.InviteResponse;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class CheckAvailableInviteProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());

        if(controller.isGeneric() && user != null){
            InviteResponse response = NetworkMessage.CreateMessage(message.getData(), InviteResponse.class);

            if(user.getTableId() != -1 || (response.isVip() && !user.getIsVip())) return;

            Room room = controller.getRoom(response.getRoomId());

            if(room != null && room.isLowBet(user.getMoney())) return;

            controller.sendNetworkMessage(user, GameCommands.SEND_INVITE_REQUEST, NetworkMessage.getGson().toJson(response));
        }
    }
}
