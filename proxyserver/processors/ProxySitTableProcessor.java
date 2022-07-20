package com.mynet.proxyserver.processors;

import com.google.gson.Gson;
import com.mynet.observer.RequestObserverCenter;
import com.mynet.proxyserver.observer.RequestData;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.request.SitTableRequest;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxySitTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProxySitTableProcessor.class);
    private Gson gson;

    public ProxySitTableProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            UserController userController = UserController.getInstance();
            ProxyUser user = userController.getUser(message.getId());

            Node gameNode = user.getGameNode();
            SitTableRequest request = gson.fromJson(message.getData(), SitTableRequest.class);
            if(gameNode != null){
                if(gameNode.getId() != request.getGameNodeId()){
                    gameNode.removeUser(user);
                    userController.addToGameNode(request.getGameNodeId(), user);
                }
            }else{
                userController.addToGameNode(request.getGameNodeId(), user);
            }

            NetworkMessage sitRequest = new NetworkMessage(GameCommands.SIT_TABLE_2);
            String sitData = request.getSide() + ";" + request.getTableId();
            sitRequest.setData(sitData);
            RequestObserverCenter.getInstance().emit(RequestType.GAME, new RequestData(user, sitRequest));
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
