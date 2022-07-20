package com.mynet.shared.resource.db.work;

import com.mynet.shared.resource.db.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.mynet.shared.db.generated.tables.TournamentHistory.TOURNAMENT_HISTORY;

public class IncrementTotalTournament implements Callable {
    Logger logger = LoggerFactory.getLogger(IncrementTotalTournament.class);

    String userId;
    int tournamentId;

    public IncrementTotalTournament(String userId, int tournamentId) {
        this.userId = userId;
        this.tournamentId = tournamentId;
    }

    @Override
    public Object call() throws Exception {
        HikariDataSource dataSource = DataSource.getDataSource().get();

        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
            context.update(TOURNAMENT_HISTORY)
                    .set(TOURNAMENT_HISTORY.TOTAL_TOURNAMENT, TOURNAMENT_HISTORY.TOTAL_TOURNAMENT.add(1))
                    .set(TOURNAMENT_HISTORY.UPDATE_DATE, DSL.currentTimestamp())
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .executeAsync();
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }
}
