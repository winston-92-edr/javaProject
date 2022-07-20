package com.mynet.questservice.db.queue;

import com.mynet.shared.resource.db.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.mynet.shared.db.generated.Tables.USER_QUEST_SEASONS;

public class UpdateUserSeasonQuery implements QuestQuery {
    Logger logger = LoggerFactory.getLogger(UpdateUserSeasonQuery.class);
    private String fuid;
    private int season;
    private int doubleXp;
    private int level;
    private int xp;
    private boolean isAdd;

    public UpdateUserSeasonQuery(String fuid, int season, int doubleXp, int level, int xp) {
        this.fuid = fuid;
        this.season = season;
        this.doubleXp = doubleXp;
        this.level = level;
        this.xp = xp;
    }

    public UpdateUserSeasonQuery(String fuid, int season, int doubleXp, int level, int xp, boolean isAdd) {
        this.fuid = fuid;
        this.season = season;
        this.doubleXp = doubleXp;
        this.level = level;
        this.xp = xp;
        this.isAdd = isAdd;
    }

    @Override
    public void execute() {

        try (DSLContext db = DSL.using(DataSource.getDataSource().get(), SQLDialect.MYSQL)) {

            if (isAdd) {
                db.insertInto(USER_QUEST_SEASONS)
                        .columns(USER_QUEST_SEASONS.XP, USER_QUEST_SEASONS.LEVEL, USER_QUEST_SEASONS.DOUBLEXP, USER_QUEST_SEASONS.FUID, USER_QUEST_SEASONS.SEASON)
                        .values(xp, level, doubleXp, Long.parseLong(fuid), UInteger.valueOf(season))
                        .execute();
            } else {
                db.update(USER_QUEST_SEASONS)
                        .set(USER_QUEST_SEASONS.XP, xp)
                        .set(USER_QUEST_SEASONS.LEVEL,level)
                        .set(USER_QUEST_SEASONS.DOUBLEXP,doubleXp)
                        .where(USER_QUEST_SEASONS.FUID.eq(Long.parseLong(fuid)))
                        .and(USER_QUEST_SEASONS.SEASON.eq(UInteger.valueOf(season)))
                        .execute();
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
