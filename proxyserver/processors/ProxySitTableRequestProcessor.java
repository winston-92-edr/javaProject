package com.mynet.proxyserver.processors;

import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.request.SitTableRequest;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxySitTableRequestProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProxySitTableRequestProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            UserController controller = UserController.getInstance();
            ProxyUser user = controller.getUser(message.getId());

            if(user != null) {
                SitTableRequest request = NetworkMessage.CreateMessage(message.getData(), SitTableRequest.class);

                int gameNodeId = request.getGameNodeId();

                if(user.getGameNode().getId() != gameNodeId){
                    Node gameNode = controller.getNode(gameNodeId);
                    user.setGameNode(gameNode);
                    user.setGameId(gameNodeId);
                }

                user.getGameNode().sendRequest(message, user);
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }
}
