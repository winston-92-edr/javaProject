package com.mynet.socialserver.processors;

import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.FriendRequestRequest;
import com.mynet.shared.response.FriendRequestResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.model.FriendRequestModel;

public class FriendRequestProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            SocialController controller = SocialController.getInstance();

            FriendRequestRequest request = NetworkMessage.CreateMessage(message.getData(), FriendRequestRequest.class);
            String invitedFuid = request.getId();

            ProxyUser invited = controller.getUser(invitedFuid);
            ProxyUser inviting = controller.getUser(message.getId());

            if (invited != null && !invited.containFriendRequest(invitedFuid)) {
                FriendRequestModel friendRequest = new FriendRequestModel(inviting.getId(), invited.getId(), 0);
                inviting.addFriendRequest(friendRequest);
                //DBController.getInstance().addFriendRequest(friendRequest);

                String firstName = Utils.getName(inviting.getId(), inviting.getFirstName());

                NetworkMessage response = new NetworkMessage(GameCommands.FRIEND_REQUEST);
                response.setDataAsJSON(new FriendRequestResponse(firstName, inviting.getId()));
                controller.getNodeToProxy().addServerMessage(response, invited);
            }
        } catch (Exception e) {

        }

    }
}
