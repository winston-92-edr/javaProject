package com.mynet.socialserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.shared.logs.PrivateChatThreadLog;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.request.PrivateChatRequest;
import com.mynet.socialserver.response.PrivateChatResponse;
import com.mynet.socialserver.response.PrivateChatStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivateChatProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(PrivateChatProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            SocialController controller = SocialController.getInstance();
            ProxyUser sender = controller.getUser(message.getId());

            int status = 0;

            PrivateChatRequest request = NetworkMessage.CreateMessage(message.getData(), PrivateChatRequest.class);

            String senderId = sender.getId();
            if (sender != null) {

                Date muteDate= null;
                boolean muted = false;

                if (sender.getUserModel().muteDate != null) {
                    muteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sender.getUserModel().muteDate);
                    muted = System.currentTimeMillis() < muteDate.getTime();
                }

                if (!muted) {

                    ProxyUser receiver = controller.getUser(request.getReceiverId());

                    if (receiver != null && receiver.isConnected()) {
                        //if(receiver.getUserModel().privateChat) {
                            if (!sender.didChatBefore(receiver.getId())) {
                                if (!sender.isVip()) {
                                    ErrorResponse vipError = new ErrorResponse(ErrorCode.PRIVATE_CHAT_NOT_VIP);
                                    NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                                    response.setDataAsJSON(vipError);

                                    controller.getNodeToProxy().addServerMessage(response, sender);
                                } else {
                                    String[] info = request.getMessageId().split("_");
                                    long date = System.currentTimeMillis();
                                    if (info.length > 1) date = Long.parseLong(info[2]);

                                    String firstName = Utils.getName(senderId, sender.getFirstName());

                                    PrivateChatResponse privateChatResponse = new PrivateChatResponse(senderId, firstName, request.getMessage(), request.getMessageId(), date);
                                    NetworkMessage response = new NetworkMessage(GameCommands.PRIVATE_CHAT);
                                    response.setDataAsJSON(privateChatResponse);
                                    controller.getNodeToProxy().addServerMessage(response, receiver);

                                    sender.addChatUser(receiver.getId());
                                    receiver.addChatUser(senderId);

                                    status = 1;
                                }
                            } else {
                                String[] info = request.getMessageId().split("_");
                                long date = System.currentTimeMillis();
                                if (info.length > 1) date = Long.parseLong(info[2]);

                                String firstName = Utils.getName(senderId, sender.getFirstName());

                                PrivateChatResponse privateChatResponse = new PrivateChatResponse(senderId, firstName, request.getMessage(), request.getMessageId(), date);
                                NetworkMessage response = new NetworkMessage(GameCommands.PRIVATE_CHAT);
                                response.setDataAsJSON(privateChatResponse);
                                controller.getNodeToProxy().addServerMessage(response, receiver);

                                sender.addChatUser(receiver.getId());
                                receiver.addChatUser(senderId);

                                status = 1;
                            }
                        //}
                    }else{
                        ErrorResponse vipError = new ErrorResponse(ErrorCode.PRIVATE_CHAT_NOT_ONLINE);
                        NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                        response.setDataAsJSON(vipError);

                        controller.getNodeToProxy().addServerMessage(response, sender);
                    }

                }else {
                    if(muteDate != null) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = format.format(muteDate);
                        ErrorResponse muteError = new ErrorResponse(date + " tarihine kadar susturuldunuz", ErrorCode.MUTED);
                        NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                        response.setDataAsJSON(muteError);
                        controller.getNodeToProxy().addServerMessage(response, sender);
                    }
                }
            }

            NetworkMessage statusResponse = new NetworkMessage(GameCommands.PRIVATE_CHAT_STATUS);
            statusResponse.setDataAsJSON(new PrivateChatStatusResponse(request.getMessageId(),status == 1));
            controller.getNodeToProxy().addServerMessage(statusResponse, sender);

            RabbitMQLogController.getInstance().addPrivateChatLog(new PrivateChatThreadLog(senderId, request.getReceiverId(), request.getMessage(), request.getMessageId(), status));

        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
