package com.mynet.proxyserver.processors;

import com.google.gson.Gson;
import com.mynet.gameserver.model.AvailableTableModel;
import com.mynet.observer.RequestObserverCenter;
import com.mynet.proxyserver.observer.RequestData;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyQuickPlayProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(ProxyQuickPlayProcessor.class);
    private Gson gson;

    public ProxyQuickPlayProcessor() {
        this.gson = new Gson();
    }

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            UserController userController = UserController.getInstance();
            ProxyUser user = userController.getUser(message.getId());

            if(message.getData().equals("-1")){
                NetworkMessage request = new NetworkMessage(GameCommands.QUICK_PLAY_2);
                request.setData("");
                RequestObserverCenter.getInstance().emit(RequestType.GAME, new RequestData(user, request));
                return;
            }

            Node gameNode = user.getGameNode();
            AvailableTableModel availableTableModel = gson.fromJson(message.getData(), AvailableTableModel.class);
            if(gameNode != null){
                if(gameNode.getId() != availableTableModel.getGameServerId()){
                    gameNode.removeUser(user);
                    userController.addToGameNode(availableTableModel.getGameServerId(), user);
                }
            }else{
                userController.addToGameNode(availableTableModel.getGameServerId(), user);
            }

            //TODO: open here after clients fix
//            NetworkMessage sitRequest = new NetworkMessage(GameCommands.SIT_TABLE_2);
//            String sitData = availableTableModel.getSide() + ";" + availableTableModel.getTableId();
//            sitRequest.setData(sitData);
//            userController.processGameRequest(user, sitRequest);

            //TODO: remove here after clients fix
            NetworkMessage request = new NetworkMessage(GameCommands.QUICK_PLAY_2);
            request.setData("");
            RequestObserverCenter.getInstance().emit(RequestType.GAME, new RequestData(user, request));

        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
