package com.mynet.socialserver.processors;

import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

public class SocketDisconnectProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        ProxyUser user  = SocialController.getInstance().getUser(message.getId());
        System.out.println("Disconnect user:" + message.getId());

        if(user != null) {
            System.out.println("User not null:" + message.getId());
            user.setConnected(false);
        }
    }
}
