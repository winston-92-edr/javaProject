package com.mynet.socialserver.processors;

import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.category.SocialCategoryInfo;
import com.mynet.questservice.quests.types.SocialActionType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.AddFriendRequest;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.response.AddFriendResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.SocialController;

public class AddFriendProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            SocialController controller = SocialController.getInstance();

            AddFriendRequest request = NetworkMessage.CreateMessage(message.getData(), AddFriendRequest.class);

            String invitingFuid = request.getId();
            String invitedFuid = message.getId();

            DBController db = DBController.getInstance();
            //db.removeFriendRequest(invitingFuid, invitedFuid);

            ProxyUser inviting = controller.getUser(invitingFuid);
            if (inviting != null) inviting.removeFriendRequest(invitedFuid);

            boolean result = request.isResult();

            ProxyUser invited = controller.getUser(invitedFuid);

            NetworkMessage updateFriendsMessage = new NetworkMessage(GameCommands.UPDATE_FRIENDS);

            controller.getNodeToProxy().addServerMessage(updateFriendsMessage, invited);

            if (result) {

                db.addFriend(invitedFuid, invitingFuid);

                if (inviting != null) {
                    invited.addOnlineFriend(invitingFuid, inviting.getProxyId());
                    inviting.addOnlineFriend(invitedFuid, invited.getProxyId());
//                    CacheController.getInstance().addOnlineFriend(invitedFuid, invitingFuid, invited.getProxyId());
//                    CacheController.getInstance().addOnlineFriend(invitingFuid, invitedFuid, inviting.getProxyId());

                    controller.getNodeToProxy().addServerMessage(updateFriendsMessage, inviting);

                } else {
                    //TODO: If there will be multiple social nodes send message to friends proxy to transfer its social node
                }

                QuestCategoryInfo questCategoryInfoFriend1 = new SocialCategoryInfo(invitedFuid, SocialActionType.ADD_FRIEND.getValue());
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.SOCIAL, questCategoryInfoFriend1);

                QuestCategoryInfo questCategoryInfoFriend2 = new SocialCategoryInfo(invitingFuid, SocialActionType.ADD_FRIEND.getValue());
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.SOCIAL, questCategoryInfoFriend2);

            }

            if(inviting != null){
                String firstName = Utils.getName(invitedFuid, invited.getFirstName());

                NetworkMessage invitedResponse = new NetworkMessage(GameCommands.ADD_FRIEND);

                invitedResponse.setDataAsJSON(new AddFriendResponse(invitedFuid, firstName, result));
                controller.getNodeToProxy().addServerMessage(invitedResponse, inviting);
            }

        } catch (Exception e) {

        }
    }
}
