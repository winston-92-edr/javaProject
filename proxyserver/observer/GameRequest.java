package com.mynet.proxyserver.observer;

import com.mynet.gameserver.actions.GameAction;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.observer.ObserverRunnable;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class GameRequest extends ObserverRunnable {
    ProxyUser user;
    NetworkMessage request;
    String id;

    public GameRequest() {
        this.id = "GameRequest_" + System.currentTimeMillis();
    }

    @Override
    protected void setData(Object data) {
        RequestData message = (RequestData) data;
        this.user = message.user;
        this.request = message.request;
    }


    @Override
    protected String getId() {
        return this.id;
    }

    @Override
    public void run() {
        try {
            if (UserController.getInstance().isChatMessageLimitExceed(user, request)) return;

            Node node = user.getGameNode();

            if (node == null && request.getCmd() != GameCommands.PING) {
                // assign node
                UserController nodeController = UserController.getInstance();
                node = nodeController.getAvailableGameNode(user.getServerType());
                if (node != null) {
                    user.setGameNode(node);
                    node.addUser(user);
                } else {
                    NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
                    message.setDataAsJSON(new ErrorResponse("Şu an sunucularımızda bakım çalışması yapılmaktadır.\nDaha sonra tekrar deneyiniz..", ErrorCode.NODE_NULL));
                    user.send(message);
                }
            }


            user.setLastPingTime(0); // reset ping timer
            int debugAction = ServerConfiguration.getInt("debug.action", 0);
            if (debugAction == 1) {
                if (request.getCmd().equals(GameCommands.SEND_USER_ACTION) || request.getCmd().equals(GameCommands.GET_CARD_FROM_DECK)) {
                    user.setLastGameAction(new GameAction(request.getCmd()));
                }
            }

            if (node != null) {
                node.sendRequest(request, user);
            }
        } catch (Exception e) {

        }
    }

}
