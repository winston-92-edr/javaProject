package com.mynet.questservice.db.queue;

import com.mynet.shared.resource.db.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.mynet.shared.db.generated.Tables.USER_QUESTS;

public class UpdateUserQuestQuery implements QuestQuery {
    private static Logger logger = LoggerFactory.getLogger(UpdateUserQuestQuery.class);
    private String fuid;
    private int questId;
    private int season;
    private int point;
    private boolean isAdd;

    public UpdateUserQuestQuery(String fuid, int questId, int season, int point) {
        this.fuid = fuid;
        this.questId = questId;
        this.season = season;
        this.point = point;
        this.isAdd = false;
    }

    public UpdateUserQuestQuery(String fuid, int questId, int season, int point, boolean isAdd) {
        this.fuid = fuid;
        this.questId = questId;
        this.season = season;
        this.point = point;
        this.isAdd = isAdd;
    }

    @Override
    public void execute() {
        try (DSLContext db = DSL.using(DataSource.getDataSource().get(), SQLDialect.MYSQL)) {

            if (isAdd) {
                db.insertInto(USER_QUESTS)
                        .columns(USER_QUESTS.POINT, USER_QUESTS.FUID, USER_QUESTS.QUESTID, USER_QUESTS.SEASON)
                        .values(point, Long.parseLong(fuid), questId, UInteger.valueOf(season))
                        .execute();
            } else {
               db.update(USER_QUESTS)
                        .set(USER_QUESTS.POINT,point)
                        .where(USER_QUESTS.FUID.eq(Long.parseLong(fuid)))
                        .and(USER_QUESTS.QUESTID.eq(questId))
                        .and(USER_QUESTS.SEASON.eq(UInteger.valueOf(season)))
                        .execute();
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
