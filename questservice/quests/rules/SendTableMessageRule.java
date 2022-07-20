package com.mynet.questservice.quests.rules;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.SocialCategoryInfo;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.questservice.quests.models.UserQuestModel;
import com.mynet.questservice.quests.types.ExpSourceType;
import com.mynet.questservice.quests.types.SocialActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendTableMessageRule implements QuestRule{
    Logger logger = LoggerFactory.getLogger(SendTableMessageRule.class);

    @Override
    public void check(QuestModel model, String info, int day) {
        try {
            SocialCategoryInfo socialCategoryInfo = QuestController.getGson().fromJson(info, SocialCategoryInfo.class);

            if (socialCategoryInfo.getSocialActionType() != SocialActionType.SEND_TABLE_MESSAGE.getValue()) return;

            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.getUser(socialCategoryInfo.getUserId());

            if(user == null) {
                logger.warn("USER IS NULL while check: " + socialCategoryInfo.getUserId());
                return;
            }


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
        }
        catch (Exception e){
            logger.error("Error at SendTableMessageRule>> "+e.getMessage());
        }
    }
}
