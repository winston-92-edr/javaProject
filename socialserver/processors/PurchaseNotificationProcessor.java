package com.mynet.socialserver.processors;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.request.PurchaseNotificationRequest;
import com.mynet.socialserver.response.PurchaseNotificationResponse;

public class PurchaseNotificationProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            SocialController controller = SocialController.getInstance();

            PurchaseNotificationRequest request = NetworkMessage.CreateMessage(message.getData(), PurchaseNotificationRequest.class);

            ProxyUser sender = controller.getUser(message.getId());

            if(sender != null) {

                ProxyUser receiver = controller.getUser(request.getReceiverId());

                NetworkMessage response = new NetworkMessage(GameCommands.PURCHASE_NOTIFICATION);
                response.setDataAsJSON(new PurchaseNotificationResponse(sender.getFirstName(), request.getGiftName(), request.getOrderId(), message.getId()));

                if (receiver != null) {
                    controller.getNodeToProxy().addServerMessage(response, receiver);
                }
            } 

        } catch (Exception e) {

        }
    }
}
