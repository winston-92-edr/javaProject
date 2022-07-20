package com.mynet.shared.resource.db.work;


import com.mynet.shared.resource.db.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.mynet.shared.db.generated.tables.TournamentHistory.TOURNAMENT_HISTORY;

public class SetMaxLevel implements Callable {
    Logger logger = LoggerFactory.getLogger(IncrementTotalGameTournament.class);

    String userId;
    int tournamentId;
    int level;
    int currentMaxLevel;

    public SetMaxLevel(String userId, int tournamentId, int level) {
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.level = level;
    }

    @Override
    public Object call() throws Exception {
        HikariDataSource dataSource = DataSource.getDataSource().get();

        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)) {
            Record record = context.select(TOURNAMENT_HISTORY.MAX_LEVEL)
                    .from(TOURNAMENT_HISTORY)
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .fetchOne();

            currentMaxLevel = record.get(TOURNAMENT_HISTORY.MAX_LEVEL);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        if(currentMaxLevel < level){
            try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)) {
                context.update(TOURNAMENT_HISTORY)
                        .set(TOURNAMENT_HISTORY.MAX_LEVEL, level)
                        .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                        .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                        .executeAsync();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return null;
    }
}
