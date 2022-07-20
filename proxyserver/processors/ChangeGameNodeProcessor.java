package com.mynet.proxyserver.processors;

import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeGameNodeProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ChangeGameNodeProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
       try {
           UserController controller = UserController.getInstance();
           ProxyUser user = controller.getUser(message.getId());
           if(user == null){
               logger.warn("ChangeGameNodeProcessor | user is null : " + message.getId());
               return;
           }

           int gameNodeId;

           if(message.getData() == "-1"){
                user.setGameNode(null);
                user.setGameId(-1);
                CacheController.getInstance().setGameNode(user.getId(),-1);

           }else{
               gameNodeId = Integer.parseInt(message.getData());
               Node gameNode = controller.getNode(gameNodeId);
               user.setGameNode(gameNode);
               user.setGameId(gameNodeId);
           }

       }catch (Exception ex){
           logger.error(ex.getMessage(), ex);
       }

    }
}
