package com.mynet.questservice.processors;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.ruleSets.RuleSet;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckQuestProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(CheckQuestProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            QuestController questController = QuestController.getInstance();
            JSONObject jsonObject = new JSONObject(message.getData());
            RuleSet ruleSet = questController.getQuestRulesMap().get(QuestCategory.forCode(jsonObject.getInt("questCategory")));
            ruleSet.checkRules(jsonObject.getString("categoryInfo"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
