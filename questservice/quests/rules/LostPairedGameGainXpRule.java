package com.mynet.questservice.quests.rules;
import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.LostGameCategoryInfo;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.questservice.quests.models.UserQuestModel;
import com.mynet.questservice.quests.types.ExpSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LostPairedGameGainXpRule implements QuestRule{
    Logger logger = LoggerFactory.getLogger(LostPairedGameGainXpRule.class);

    @Override
    public void check(QuestModel model, String info, int day) {
        try {
            LostGameCategoryInfo categoryInfo = QuestController.getGson().fromJson(info, LostGameCategoryInfo.class);

            if(!categoryInfo.isClassic()) return;
            if(categoryInfo.getBet() != model.getPremise()) return;
            if(!categoryInfo.isPaired()) return;

            QuestController questController = QuestController.getInstance();

            QuestUser user = questController.getUser(categoryInfo.getUserId());

            if(user == null) return;

            boolean differentSeason = (user.getSeasonId() != questController.getSeason().getId());
            if(differentSeason) questController.setUser(user, true);

            UserQuestModel quest = user.getAndCreateQuest(model.getId());
            int updatedPoint = quest.increment();

            questController.addUpdateUserQuestQuery(user.getId(), model.getId(), updatedPoint);

            questController.updateUserXp(model, user, quest, ExpSourceType.PAIRED);

        } catch (Exception e) {
            logger.error("Error at LostPairedGameGainXpRule>> " + e.getMessage());
        }
    }
}
