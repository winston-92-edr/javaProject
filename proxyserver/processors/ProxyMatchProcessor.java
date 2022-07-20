package com.mynet.proxyserver.processors;

import com.mynet.matchserver.response.MatchingResponse;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyMatchProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProxyMatchProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        MatchingResponse request = NetworkMessage.CreateMessage(message.getData(), MatchingResponse.class);
        BasicUserModel[] users = request.getUsers();
        Node availableGameNode = UserController.getInstance().getAvailableGameNode(ServerType.TOURNAMENT);

        for (BasicUserModel basicUserModel : users) {
            ProxyUser user = UserController.getInstance().getUser(basicUserModel.getId());
            Node node = user.getGameNode();
            if (node != null) {
                if(availableGameNode != null) {
                    if (availableGameNode.getId() != node.getId()) {
                        node.removeUser(user);
                        availableGameNode.addUser(user);
                        user.setGameNode(availableGameNode);
                    }
                }else{
                    logger.warn("NO AVAILABLE GAME NODE");
                }
            }else{
                logger.warn("GAME NODE IS NULL FOR USER: " + user.getId());
            }
        }
    }
}
