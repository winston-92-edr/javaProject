package com.mynet.socialserver.processors;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.request.PurchaseNotificationResultRequest;
import com.mynet.socialserver.response.PurchaseNotificationResultResponse;

public class PurchaseNotificationResultProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            SocialController controller = SocialController.getInstance();

            PurchaseNotificationResultRequest request = NetworkMessage.CreateMessage(message.getData(), PurchaseNotificationResultRequest.class);

            ProxyUser sender = controller.getUser(request.getSenderId());

            NetworkMessage response = new NetworkMessage(GameCommands.PURCHASE_NOTIFICATION_RESULT);
            response.setDataAsJSON(new PurchaseNotificationResultResponse(request.getReceiverName(), request.isResult()));

            if (sender != null) {
                controller.getNodeToProxy().addServerMessage(response, sender);
            }

        } catch (Exception e) {

        }
    }
}
