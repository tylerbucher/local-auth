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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SqLiteModule extends SqlModule {

    /**
     * The sql query for creating a users table.
     */
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS `users` (`username` VARCHAR(25) " +
            "NOT NULL, `password` VARCHAR(60) NOT NULL, `admin` BOOLEAN NOT NULL, `active` BOOLEAN NOT NULL, " +
            "PRIMARY KEY (`username`))";

    /**
     * The sql query for creating a dash table.
     */
    private static final String CREATE_DASH_TABLE = "CREATE TABLE IF NOT EXISTS `dash` ( `id` INT UNSIGNED NOT NULL , `value` " +
            "VARCHAR(1024) NOT NULL , UNIQUE(`id`))";

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The Sql module singleton.
         */
        private static final SqLiteModule INSTANCE = new SqLiteModule();
    }

    /**
     * @return {@link SecurityDbModule} singleton.
     */
    public static SqLiteModule getInstance() {
        return SqLiteModule.SingletonHolder.INSTANCE;
    }

    @Override
    public void createTables() {
        this.createTables(CREATE_USERS_TABLE, CREATE_DASH_TABLE);
    }

    @Override
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(LocalAuth.getJdbcUrl());
    }
}
