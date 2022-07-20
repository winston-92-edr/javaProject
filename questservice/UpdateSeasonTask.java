package com.mynet.questservice;

import com.mynet.questservice.quests.models.QuestSeasonModel;
import com.mynet.shared.resource.CacheController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class UpdateSeasonTask extends TimerTask {
    private static Logger logger = LoggerFactory.getLogger(UpdateSeasonTask.class);

    @Override
    public void run() {
        QuestSeasonModel oldSeason = QuestController.getInstance().getSeason();
        QuestSeasonModel season = CacheController.getInstance().getSeason();
        if (season == null) return;

        if (oldSeason == null || !oldSeason.equals(season)) {
            try {
                QuestController.getInstance().setSeason(season);
                logger.error("New Season:" + QuestController.getInstance().getSeason().toString());
            } catch (Exception e) {
                logger.error("Error at setSeason at UpdateSeasonThread: " + e.getMessage());
            }

//            try {
//                if (oldSeason.getId() != season.getId()) {
//                    QuestController.getInstance().sendSeasonFinished();
//                }
//            } catch (Exception e) {
//                logger.error("Error at at sendSeasonFinished at UpdateSeasonThread: " + e.getMessage());
//            }
        }
    }
}
