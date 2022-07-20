package com.mynet.socialserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.request.StartPrivateChatRequest;
import com.mynet.socialserver.response.StartPrivateChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartPrivateChatProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(StartPrivateChatProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            SocialController controller = SocialController.getInstance();
            ProxyUser sender = controller.getUser(message.getId());

            if (sender != null) {
                StartPrivateChatRequest request = NetworkMessage.CreateMessage(message.getData(), StartPrivateChatRequest.class);

                if (!sender.didChatBefore(request.getId())) {
                    if (!sender.isVip()) {
                        ErrorResponse vipError = new ErrorResponse(ErrorCode.PRIVATE_CHAT_NOT_VIP);
                        NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                        response.setDataAsJSON(vipError);

                        controller.getNodeToProxy().addServerMessage(response, sender);
                    } else {
                        NetworkMessage statusResponse = new NetworkMessage(GameCommands.START_PRIVATE_CHAT);
                        statusResponse.setDataAsJSON(new StartPrivateChatResponse(request.getId(), true));
                        controller.getNodeToProxy().addServerMessage(statusResponse, sender);
                    }
                } else {
                    NetworkMessage statusResponse = new NetworkMessage(GameCommands.START_PRIVATE_CHAT);
                    statusResponse.setDataAsJSON(new StartPrivateChatResponse(request.getId(), true));
                    controller.getNodeToProxy().addServerMessage(statusResponse, sender);
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
