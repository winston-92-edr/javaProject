package com.mynet.proxyserver.processors;

import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class PingProcessor implements MessageProcessor {

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        UserController controller = UserController.getInstance();
        ProxyUser user = controller.getUser(message.getId());

        if(user != null) {
            NetworkMessage request = new NetworkMessage(GameCommands.PING);
            request.setData("PONG");
            controller.sendNetworkMessage(request, message.getId());

            user.setLastPingTime(System.currentTimeMillis());
        }

    }
}
