package com.mynet.shared.resource.db;

import com.mynet.shared.config.ServerConfiguration;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Optional;

public class DataSource {
    private static HikariDataSource dataSource;
    private static HikariDataSource dataSourceFriends;

    public static Optional<HikariDataSource> getDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return dataSource == null ? Optional.empty() : Optional.of(dataSource);
        }

        String url = ServerConfiguration.get("db.main.url");
        String userName = ServerConfiguration.get("db.main.user");
        String password = ServerConfiguration.get("db.main.pass");


        if(url != null) {

            HikariDataSource ds = new HikariDataSource();
            ds.setPoolName("canakokey");

            ds.setMaximumPoolSize(8);
            ds.setMaxLifetime(60000);
            ds.setMinimumIdle(2);
            ds.setIdleTimeout(30000);
            ds.setLeakDetectionThreshold(48000);


            ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            ds.addDataSourceProperty("url", url);
            ds.addDataSourceProperty("user", userName);
            ds.addDataSourceProperty("password", password);
            ds.addDataSourceProperty("cachePrepStmts", true);
            ds.addDataSourceProperty("prepStmtCacheSize", 250);
            ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            ds.addDataSourceProperty("useServerPrepStmts", true);
            ds.addDataSourceProperty("verifyServerCertificate", false);

            dataSource = ds;

        }

        return dataSource == null ? Optional.empty() : Optional.of(dataSource);
    }

    public static Optional<HikariDataSource> getFriendsDataSource() {
        if (dataSourceFriends != null && !dataSourceFriends.isClosed()) {
            return dataSourceFriends == null ? Optional.empty() : Optional.of(dataSourceFriends);
        }

        String url = ServerConfiguration.get("db.friends.url");
        String userName = ServerConfiguration.get("db.friends.user");
        String password = ServerConfiguration.get("db.friends.pass");


        if(url != null) {

            HikariDataSource ds = new HikariDataSource();
            ds.setPoolName("canakokeyfriends");

            ds.setMaximumPoolSize(8);
            ds.setMaxLifetime(60000);
            ds.setMinimumIdle(2);
            ds.setIdleTimeout(30000);
            ds.setLeakDetectionThreshold(48000);


            ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            ds.addDataSourceProperty("url", url);
            ds.addDataSourceProperty("user", userName);
            ds.addDataSourceProperty("password", password);
            ds.addDataSourceProperty("cachePrepStmts", true);
            ds.addDataSourceProperty("prepStmtCacheSize", 250);
            ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            ds.addDataSourceProperty("useServerPrepStmts", true);
            ds.addDataSourceProperty("verifyServerCertificate", false);

            dataSourceFriends = ds;

        }

        return dataSourceFriends == null ? Optional.empty() : Optional.of(dataSourceFriends);
    }
}
