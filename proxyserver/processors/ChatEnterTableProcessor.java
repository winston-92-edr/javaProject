package com.mynet.proxyserver.processors;

import com.mynet.observer.RequestObserverCenter;
import com.mynet.proxyserver.observer.RequestData;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class ChatEnterTableProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        UserController controller = UserController.getInstance();

        ProxyUser user = controller.getUser(message.getId());

        if(user != null){
            RequestObserverCenter.getInstance().emit(RequestType.CHAT, new RequestData(user, message));
        }
    }
}
