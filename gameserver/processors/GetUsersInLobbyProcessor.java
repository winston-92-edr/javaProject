package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.model.TableUserShortModel;
import com.mynet.gameserver.response.GetUsersInLobbyResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetUsersInLobbyProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(GetUsersInLobbyProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            long tableId = user.getTableId();
            if (tableId < 0) {
                return;
            }

            List<TableUserShortModel> users = controller.getUsersInLobby(user);
            GetUsersInLobbyResponse response = new GetUsersInLobbyResponse(users);
            controller.sendNetworkMessage(user, GameCommands.GET_USERS_IN_LOBBY, NetworkMessage.getGson().toJson(response));

        }catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
