/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.localauth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class MySqlModule extends SqlModule {

    /**
     * The sql query for creating a users table.
     */
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS `users` (`username` VARCHAR(25) " +
            "NOT NULL, `password` VARCHAR(60) NOT NULL, `admin` BOOLEAN NOT NULL, `active` BOOLEAN NOT NULL, " +
            "PRIMARY KEY (`username`)) ENGINE = InnoDB;";

    /**
     * The sql query for creating a dash table.
     */
    private static final String CREATE_DASH_TABLE = "CREATE TABLE IF NOT EXISTS `dash` ( `id` INT UNSIGNED NOT NULL , `value` " +
            "VARCHAR(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL , UNIQUE `id_unique` (`id`)) ENGINE = InnoDB;";

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The Sql module singleton.
         */
        private static final MySqlModule INSTANCE = new MySqlModule();
    }

    /**
     * @return {@link SecurityDbModule} singleton.
     */
    public static MySqlModule getInstance() {
        return MySqlModule.SingletonHolder.INSTANCE;
    }

    /**
     * Config for sql pool information
     */
    private HikariConfig hikariConfig;

    /**
     * Sql pool.
     */
    private HikariDataSource dataSource;

    private MySqlModule() {
        loadConnectionPool();
    }

    private void loadConnectionPool() {
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(LocalAuth.getJdbcUrl());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void createTables() {
        this.createTables(CREATE_USERS_TABLE, CREATE_DASH_TABLE);
    }

    /**
     * @return the sql connection pool.
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
