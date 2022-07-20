package com.mynet.questservice.quests.rules;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.GameEndCategoryInfo;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.questservice.quests.models.UserQuestModel;
import com.mynet.questservice.quests.types.ExpSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WonGameFakeOkeyRule implements QuestRule {
    Logger logger = LoggerFactory.getLogger(WonGameFakeOkeyRule.class);

    @Override
    public void check(QuestModel model, String info, int day) {
        try {
            GameEndCategoryInfo categoryInfo = QuestController.getGson().fromJson(info, GameEndCategoryInfo.class);

            if (!categoryInfo.isWithFakeOkey()) {
                return;
            }

            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.getUser(categoryInfo.getUserId());

            if(user == null) return;


           if (questController.invalidQuestDay(model)) return;

            UserQuestModel quest = user.getAndCreateQuest(model.getId());
            if (quest.getPoint() == model.getGoal()) return;

            int updatedPoint = quest.increment();
            if(user.getQuest(quest.getQuestId()) != null) user.getQuest(quest.getQuestId()).setPoint(updatedPoint);

            questController.addUpdateUserQuestQuery(user.getId(), model.getId(), updatedPoint);

            boolean complete = quest.isComplete();

            if (complete) {
               ExpSourceType source = model.isDaily() ? ExpSourceType.DAILY : ExpSourceType.SEASONAL;
               questController.updateUserXp(model, user, quest,source);
            }
        } catch (Exception e) {
            logger.error("Error at WonGameFakeOkeyRule>> " + e.getMessage());
        }
    }
}
