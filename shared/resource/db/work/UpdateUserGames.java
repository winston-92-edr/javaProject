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

public class UpdateUserGames implements Callable {
    Logger logger = LoggerFactory.getLogger(UpdateUserGames.class);

    String id;
    int wonGames;
    int lostGames;
    int totalGames;

    public UpdateUserGames(String id, int wonGames, int lostGames, int totalGames) {
        this.id = id;
        this.wonGames = wonGames;
        this.lostGames = lostGames;
        this.totalGames = totalGames;
    }

    @Override
    public Object call() throws Exception {
        HikariDataSource dataSource = DataSource.getDataSource().get();

        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
            context.update(OVERALL_SCORES)
                    .set(OVERALL_SCORES.GAMES_WON, UInteger.valueOf(wonGames))
                    .set(OVERALL_SCORES.GAMES_LOST,UInteger.valueOf(lostGames))
                    .set(OVERALL_SCORES.GAMES_TOTAL,UInteger.valueOf(totalGames))
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id)))
                    .execute();
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
