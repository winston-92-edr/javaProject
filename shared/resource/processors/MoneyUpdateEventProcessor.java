package com.mynet.shared.resource.processors;

import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.model.ServerEventModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DBEventProcessor;
import com.mynet.shared.response.UserReceivedMoneyResponse;
import com.mynet.shared.types.MessageTypes;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class MoneyUpdateEventProcessor implements DBEventProcessor {
    private static Logger logger = LoggerFactory.getLogger(MoneyUpdateEventProcessor.class);

    @Override
    public void process(ServerEventModel event) {
        try {
            String userId = event.getEventData();
            UserController controller = UserController.getInstance();
            ProxyUser user = controller.getUser(userId);

            long userMoney = DBController.getInstance().getUserMoney(userId);
            if(userMoney == -1) return;

            user.setMoney(userMoney);

            NetworkMessage message = new NetworkMessage(GameCommands.USER_RECEIVED_MONEY);
            message.setDataAsJSON(NetworkMessage.getGson().toJson(new UserReceivedMoneyResponse(userMoney)));
            controller.sendNetworkMessage(message, userId);

            NetworkMessage networkMessage = new NetworkMessage(GameCommands.SERVER_USER_STATUS_UPDATED);
            BasicUserModel userModel = new BasicUserModel(userId, userMoney, user.isVip(), user.getTickets());
            networkMessage.setDataAsJSON(userModel);

            Collection<Node> nodes = controller.getNodes();
            for (Node node: nodes){
                node.sendRequest(networkMessage, user);
            }


        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
