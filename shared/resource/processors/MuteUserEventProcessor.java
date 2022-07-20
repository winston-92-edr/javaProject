package com.mynet.shared.resource.processors;

import com.mynet.shared.model.ServerEventModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBEventProcessor;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

public class MuteUserEventProcessor implements DBEventProcessor {

    @Override
    public void process(ServerEventModel event) {

        CacheController cacheController = CacheController.getInstance();
        UserController userController = UserController.getInstance();

        String fuid = event.getEventData();
        int gameNodeId =  cacheController.getUserGameNode(fuid);

        if(gameNodeId != -1){

            ProxyUser user = userController.getUser(fuid);

            if(user != null){
                Node gameNode = userController.getNode(gameNodeId);

                NetworkMessage muteRequest = new NetworkMessage(GameCommands.MUTE_USER);
                gameNode.sendRequest(muteRequest, user);
            }
        }
    }
}
