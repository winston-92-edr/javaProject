package com.mynet.proxyserver.processors;

import com.mynet.observer.RequestObserverCenter;
import com.mynet.proxyserver.observer.RequestData;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class CheckAvailableInviteProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        UserController controller = UserController.getInstance();

        ProxyUser user = controller.getUser(message.getId());

        if(user != null && user.getServerType() == ServerType.GENERIC){
            RequestObserverCenter.getInstance().emit(RequestType.GAME, new RequestData(user, message));
        }
    }
}
