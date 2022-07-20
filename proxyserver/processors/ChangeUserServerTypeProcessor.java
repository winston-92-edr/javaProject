package com.mynet.proxyserver.processors;

import com.mynet.proxyserver.model.ChangeUserServerTypeRequest;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class ChangeUserServerTypeProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            UserController userController = UserController.getInstance();
            ProxyUser user = userController.getUser(message.getId());
            ChangeUserServerTypeRequest request = NetworkMessage.CreateMessage(message.getData(), ChangeUserServerTypeRequest.class);

            if (user != null) {
                if (!user.getServerType().equals(request.getType())) {
                    user.setServerType(request.getType());
                    user.setGameNode(null);
                    userController.setUserNodes(user);

                    UserModel userModel = DBController.getInstance().getUser(user.getId());

                    user.updateBasicUser(userModel);

                    userController.sendAuthorize(user, false);
                }
            }
        }catch(Exception e){

        }
    }
}
