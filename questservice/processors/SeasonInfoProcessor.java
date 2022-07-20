package com.mynet.questservice.processors;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.messages.SeasonInfoRequestMessage;
import com.mynet.questservice.quests.messages.SeasonInfoResponseMessage;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeasonInfoProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(SeasonInfoProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.getUser(message.getId());

            if (user == null) return;

            boolean open = true;
            boolean previousSeasonOpen = false;
            boolean differentSeason = user.getSeasonId() != questController.getSeason().getId();

            if (differentSeason) {
                previousSeasonOpen = true;
                QuestController.getInstance().setUser(user, previousSeasonOpen);

            } else if (message.getData() != null && !message.getData().isEmpty()) {
                SeasonInfoRequestMessage seasonInfoRequestMessage = NetworkMessage.getGson().fromJson(message.getData(), SeasonInfoRequestMessage.class);

                String updateKey = seasonInfoRequestMessage.getUpdateKey();
                if (updateKey != null && !updateKey.isEmpty()) {
                    String[] keyParts = updateKey.split(":");

                    if (keyParts.length > 1) {
                        if (keyParts[1].equals("multi")) {
                            open = false;
                        } else if (keyParts[1].equals("previousOpen")) {
                            updateKey = keyParts[0];
                            previousSeasonOpen = true;
                        }
                    }

                    boolean isUserQuestUpdate = CacheController.getInstance().isUserQuestUpdate(user.getId(), updateKey);

                    if (isUserQuestUpdate) {
                        QuestController.getInstance().setUser(user, previousSeasonOpen);
                    }
                }
            }

            SeasonInfoResponseMessage seasonInfoResponseMessage = questController.getSeasonInfo(user, open, previousSeasonOpen);

            NetworkMessage response = new NetworkMessage();
            response.setDataAsJSON(seasonInfoResponseMessage);
            response.setCmd(GameCommands.SEASON_INFO);
            QuestController.getInstance().sendMessage(response, user);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
