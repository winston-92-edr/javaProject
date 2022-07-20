package com.mynet.shared.resource.db.work;

import com.mynet.shared.model.ServerVariable;
import com.mynet.shared.resource.db.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.mynet.shared.db.generated.tables.Settings.SETTINGS;

public class GetServerVariables implements Callable {
    private static final Logger logger = LoggerFactory.getLogger(GetServerVariables.class);

    public GetServerVariables() {
    }

    @Override
    public Object call() throws Exception {
        HikariDataSource dataSource = DataSource.getDataSource().get();
        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
            Result<Record> records = context.select().from(SETTINGS).fetch();
            return records.into(ServerVariable.class);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
