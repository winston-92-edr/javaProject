package com.mynet.questservice.processors;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.messages.AddUserMessage;
import com.mynet.questservice.quests.messages.QuestUserInfoMessage;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddUserProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(AddUserProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.createUser(message.getId());
            String platform = CacheController.getInstance().getUserLastPlatform(user.getId());
            user.setPlatform(platform);

            QuestUserInfoMessage questUserInfoMessage = questController.getQuestUserInfo(user,false);

            NetworkMessage response = new NetworkMessage(GameCommands.QUEST_USER_INFO);
            response.setDataAsJSON(questUserInfoMessage);
            QuestController.getInstance().sendMessage(response, user);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
