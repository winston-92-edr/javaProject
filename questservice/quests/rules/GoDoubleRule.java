package com.mynet.questservice.quests.rules;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.GameActionCategoryInfo;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.questservice.quests.models.UserQuestModel;
import com.mynet.questservice.quests.types.ExpSourceType;
import com.mynet.questservice.quests.types.GameActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoDoubleRule implements QuestRule {
    Logger logger = LoggerFactory.getLogger(GoDoubleRule.class);

    @Override
    public void check(QuestModel model, String info, int day) {
        try {
            GameActionCategoryInfo gameActionCategoryInfo = QuestController.getGson().fromJson(info, GameActionCategoryInfo.class);

            if (gameActionCategoryInfo.getGameActionType() != GameActionType.GO_DOUBLE.getValue()) return;

            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.getUser(gameActionCategoryInfo.getUserId());

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
            logger.error("Error at GoDoubleRule>> " + e.getMessage());
        }
    }
}
