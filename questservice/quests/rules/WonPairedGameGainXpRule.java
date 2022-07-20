package com.mynet.questservice.quests.rules;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.GameEndCategoryInfo;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.questservice.quests.models.UserQuestModel;
import com.mynet.questservice.quests.types.ExpSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WonPairedGameGainXpRule implements QuestRule{
    Logger logger = LoggerFactory.getLogger(WonPairedGameGainXpRule.class);

    @Override
    public void check(QuestModel model, String info, int day) {
        try {
            GameEndCategoryInfo categoryInfo = QuestController.getGson().fromJson(info, GameEndCategoryInfo.class);

            if(categoryInfo.getBet() != model.getPremise()) return;
            if(!categoryInfo.isPaired()) return;

            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.getUser(categoryInfo.getUserId());

            if(user == null) return;

            UserQuestModel quest = user.getAndCreateQuest(model.getId());
            int updatedPoint = quest.increment();

            questController.addUpdateUserQuestQuery(user.getId(), model.getId(), updatedPoint);

            questController.updateUserXp(model, user, quest, ExpSourceType.PAIRED);

        } catch (Exception e) {
            logger.error("Error at WonPairedGameGainXpRule>> " + e.getMessage());
        }
    }
}
