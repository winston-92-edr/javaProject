package com.mynet.shared.resource.processors;

import com.google.gson.Gson;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.model.ServerEventModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.DBEventProcessor;
import com.mynet.shared.response.UpdateTicketResponse;
import com.mynet.shared.types.MessageTypes;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class TicketUpdateEventProcessor implements DBEventProcessor {
    private static Logger logger = LoggerFactory.getLogger(TicketUpdateEventProcessor.class);
    private Gson gson;

    public TicketUpdateEventProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(ServerEventModel event) {
        try {
            String userId = event.getEventData();
            UserController controller = UserController.getInstance();
            ProxyUser user = controller.getUser(userId);

            int userTickets = DBController.getInstance().getUserTickets(userId);
            if(userTickets == -1) return;

            user.setTickets(userTickets);

            UpdateTicketResponse dataResponse = new UpdateTicketResponse(user.getTickets(), UpdateTicketResponse.UpdateTicketReason.PURCHASE);

            NetworkMessage message = new NetworkMessage(GameCommands.USER_TICKET_UPDATED);
            message.setDataAsJSON(dataResponse);
            controller.sendNetworkMessage(message, userId);

            NetworkMessage networkMessage = new NetworkMessage(GameCommands.SERVER_USER_STATUS_UPDATED);
            BasicUserModel userModel = new BasicUserModel(userId, user.getMoney(), user.isVip(), userTickets);
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
