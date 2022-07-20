package com.mynet.shared.resource.db.work;

import com.mynet.shared.resource.db.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.mynet.shared.db.generated.tables.OverallScores.OVERALL_SCORES;

public class UpdateExperienceAndScore implements Callable {
    private Logger logger = LoggerFactory.getLogger(UpdateExperienceAndScore.class);

    private String id;
    private int gamesWon;
    private int gamesLost;
    private int gamesTotal;
    private long potMax;
    private long gamesPot;
    private int experience;

    public UpdateExperienceAndScore(String id, int gamesWon, int gamesLost,int gamesTotal, long potMax, long gamesPot, int experience) {
        this.id = id;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gamesTotal = gamesTotal;
        this.potMax = potMax;
        this.gamesPot = gamesPot;
        this.experience = experience;
    }

    @Override
    public Object call() throws Exception {
        HikariDataSource dataSource = DataSource.getDataSource().get();

        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
            context.update(OVERALL_SCORES)
                    .set(OVERALL_SCORES.GAMES_WON, UInteger.valueOf(gamesWon))
                    .set(OVERALL_SCORES.GAMES_LOST, UInteger.valueOf(gamesLost))
                    .set(OVERALL_SCORES.GAMES_TOTAL, UInteger.valueOf(gamesTotal))
                    .set(OVERALL_SCORES.GAMES_POT, UInteger.valueOf(gamesPot))
                    .set(OVERALL_SCORES.POT_MAX,UInteger.valueOf(potMax))
                    .set(OVERALL_SCORES.EXPERIENCE,UInteger.valueOf(experience))
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id)))
                    .executeAsync();
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
