//package com.mynet.socialserver.processors;
//
//import com.mynet.shared.network.GameCommands;
//import com.mynet.shared.network.InvalidServerMessage;
//import com.mynet.shared.network.MessageProcessor;
//import com.mynet.shared.network.NetworkMessage;
//import com.mynet.shared.request.SetActiveFriendRequest;
//import com.mynet.shared.response.SetActivateFriendResponse;
//import com.mynet.shared.user.ProxyUser;
//import com.mynet.socialserver.SocialController;
//
//import java.util.ArrayList;
//
//public class SetActiveFriendProcessor implements MessageProcessor {
//    @Override
//    public void process(NetworkMessage message) throws InvalidServerMessage {
//        try {
//            SetActiveFriendRequest request = NetworkMessage.CreateMessage(message.getData(),SetActiveFriendRequest.class);
//            ArrayList<String> friendList = request.getFriendList();
//
//            SocialController controller = SocialController.getInstance();
//            ProxyUser user = controller.getUser(message.getId());
//
//            for (int i = 0; i < friendList.size(); i++) {
//                String friendId = friendList.get(i);
//                if (!user.containsFriend(friendId)) {
//                    user.addFriend(friendId);
//
//                    ProxyUser friend = controller.getUser(friendId);
//                    if (friend != null) {
//                        NetworkMessage response = new NetworkMessage(GameCommands.ACTIVE_FRIEND);
//                        response.setDataAsJSON(new SetActivateFriendResponse(friendId));
//                        controller.getNodeToProxy().addServerMessage(response,friend);
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//
//        }
//    }
//}
