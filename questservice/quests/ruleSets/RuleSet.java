package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.questservice.quests.rules.QuestRule;
import com.mynet.questservice.quests.types.QuestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class RuleSet {
    private static Logger logger = LoggerFactory.getLogger(RuleSet.class);
    protected HashMap<QuestType, QuestRule> rules;

    public RuleSet() {
        this.rules = new HashMap<>();
        registerRules();
    }

    public abstract void registerRules();
    public void checkRules(String info){
        QuestController controller = QuestController.getInstance();

        try {
            QuestCategoryInfo categoryInfo = QuestController.getGson().fromJson(info, QuestCategoryInfo.class);

            QuestUser user = controller.getUser(categoryInfo.fuid);
            if(user == null){
                controller.createUser(categoryInfo.fuid);
            }

            int day = QuestController.getInstance().getSeason().getDay();

            for (Map.Entry<QuestType, QuestRule> entry: rules.entrySet()){
                QuestType type = entry.getKey();
                QuestRule rule = entry.getValue();
                ArrayList<QuestModel> models = controller.getModels(type);
                if(models != null){
                    for (QuestModel model: models){
                        rule.check(model, info, day);
                    }
                }else {
                    logger.error("type: "+ type +" is NULL!");
                }

            }
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
