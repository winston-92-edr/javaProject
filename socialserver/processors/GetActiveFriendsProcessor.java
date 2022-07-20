//package com.mynet.socialserver.processors;
//
//import com.mynet.proxyserver.user.UserModel;
//import com.mynet.shared.network.GameCommands;
//import com.mynet.shared.network.InvalidServerMessage;
//import com.mynet.shared.network.MessageProcessor;
//import com.mynet.shared.network.NetworkMessage;
//import com.mynet.shared.resource.CacheController;
//import com.mynet.shared.response.GetActiveFriendsResponse;
//import com.mynet.shared.user.ProxyUser;
//import com.mynet.socialserver.SocialController;
//import com.mynet.socialserver.model.FriendModel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//
//public class GetActiveFriendsProcessor implements MessageProcessor {
//    Logger logger = LoggerFactory.getLogger(GetActiveFriendsProcessor.class);
//
//    @Override
//    public void process(NetworkMessage message) throws InvalidServerMessage {
//        try {
//            SocialController controller = SocialController.getInstance();
//
//            ProxyUser user = controller.getUser(message.getId());
//            ArrayList<String> friendsIds = user.getFriends();
//            ArrayList<FriendModel> friends = new ArrayList<>();
//
//            for (int i = 0; i < friendsIds.size(); i++) {
//                String fid = friendsIds.get(i);
//                ProxyUser u = controller.getUser(fid);
//
//                if (u != null) {
//                    CacheController cacheController = CacheController.getInstance();
//                    UserModel um = cacheController.getUserGameModel(fid);
//                    friends.add(new FriendModel(fid, um.roomID, um.tableID, um.gameID));
//                }
//
//            }
//
//            NetworkMessage response = new NetworkMessage(GameCommands.GET_ONLINE_FRIENDS);
//            message.setDataAsJSON(new GetActiveFriendsResponse(friends));
//            controller.getNodeToProxy().addServerMessage(response, user);
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//    }
//}

