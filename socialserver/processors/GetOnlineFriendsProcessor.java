package com.mynet.socialserver.processors;

import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.model.FriendModel;
import com.mynet.socialserver.request.OnlineFriendsRequest;
import com.mynet.socialserver.response.OnlineFriendsResponse;

import java.util.ArrayList;
import java.util.List;

public class GetOnlineFriendsProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        SocialController controller = SocialController.getInstance();
        ProxyUser user = controller.getUser(message.getId());

        if (user != null) {

//            CacheController cacheController = CacheController.getInstance();
//            String userId = user.getId();
            int userProxyId = user.getProxyId();

            OnlineFriendsRequest request = NetworkMessage.CreateMessage(message.getData(), OnlineFriendsRequest.class);

            user.setFriendsCount(request.getIds().size());

            for (String id : request.getIds()) {

                //TODO: If multiple social nodes look from cache when friend is not in this social node
                ProxyUser friend = controller.getUser(id);

                int proxyId;

                if (friend != null) proxyId = user.getProxyId();
                else continue;

                if (proxyId != -1) {
                    user.addOnlineFriend(id, proxyId);
                }
            }


            if (!user.isOnlineFriendsSet()) {
                controller.notifyFriendStatus(user, userProxyId, true);
            }

            NetworkMessage response = new NetworkMessage(GameCommands.GET_ONLINE_FRIENDS);

            List<FriendModel> friends = new ArrayList<>();

            for (String id : user.getOnlineFriends().keySet()) {
                ProxyUser friend = controller.getUser(id);

                if (friend != null && friend.isConnected()) {
                    UserModel friendInfo = CacheController.getInstance().getUserGameModel(id);
                    friends.add(new FriendModel(id, friendInfo.roomID, friendInfo.tableID, friendInfo.gameServerId));
                }
            }

            response.setDataAsJSON(new OnlineFriendsResponse(friends));

            controller.getNodeToProxy().addServerMessage(response, user);


        }
    }


}
