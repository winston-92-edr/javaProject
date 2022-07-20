package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.gameserver.request.InviteRequest;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.category.SocialCategoryInfo;
import com.mynet.questservice.quests.types.SocialActionType;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendInviteProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(SendInviteProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();

            InviteRequest request = NetworkMessage.CreateMessage(message.getData(), InviteRequest.class);

            GameUser sender = controller.getUser(message.getId());

            String invitedId = request.getId();

            controller.inviteUser(String.valueOf(sender.getTableId()), invitedId, sender.getFirstName(), sender.getfuid());

            QuestCategoryInfo questCategoryInfo = new SocialCategoryInfo(sender.getfuid(), SocialActionType.INVITE_FRIEND_TABLE.getValue());
            RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.SOCIAL,questCategoryInfo);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
