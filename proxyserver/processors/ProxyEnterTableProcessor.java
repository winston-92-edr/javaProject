package com.mynet.proxyserver.processors;

import com.mynet.observer.RequestObserverCenter;
import com.mynet.proxyserver.observer.RequestData;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.request.EnterTableRequest;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyEnterTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProxyEnterTableProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            UserController userController = UserController.getInstance();
            ProxyUser user = userController.getUser(message.getId());

            Node gameNode = user.getGameNode();
            EnterTableRequest request = NetworkMessage.getGson().fromJson(message.getData(), EnterTableRequest.class);

            Integer tableId = request.getTableId();
            if (tableId == null || tableId == -1) {
                NetworkMessage sitRequest = new NetworkMessage(GameCommands.GAME_ENTER_TABLE);
                sitRequest.setDataAsJSON(request);
                RequestObserverCenter.getInstance().emit(RequestType.GAME, new RequestData(user, sitRequest));
                return;
            }

            if(gameNode != null){
                if(gameNode.getId() != request.getGameNodeId()){
                    gameNode.removeUser(user);
                    userController.addToGameNode(request.getGameNodeId(), user);
                }
            }else{
                userController.addToGameNode(request.getGameNodeId(), user);
            }

            NetworkMessage sitRequest = new NetworkMessage(GameCommands.GAME_ENTER_TABLE);
            sitRequest.setDataAsJSON(request);
            RequestObserverCenter.getInstance().emit(RequestType.GAME, new RequestData(user, sitRequest));

        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
