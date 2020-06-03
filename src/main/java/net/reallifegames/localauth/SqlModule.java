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

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A module to handle all database functions in mysql.
 *
 * @author Tyler Bucher
 */
public final class SqlModule implements DbModule {

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The Sql module singleton.
         */
        private static final SqlModule INSTANCE = new SqlModule();
    }

    /**
     * @return {@link SecurityDbModule} singleton.
     */
    public static SqlModule getInstance() {
        return SqlModule.SingletonHolder.INSTANCE;
    }

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SqlModule.class);

    /**
     * Sql query for checking if a user is an admin.
     */
    private static final String QUERY_USER_ADMIN = "SELECT `admin` FROM `users` WHERE `username`=?;";

    /**
     * Sql query for checking if a user exists.
     */
    private static final String QUERY_USER_EXISTS = "SELECT `username` FROM `users` WHERE `username`=?;";

    /**
     * Sql query for creating a new user.
     */
    private static final String CREATE_USER = "INSERT INTO `users`(`username`, `password`, `admin`, `active`) VALUES (?, ?, ?, ?);";

    /**
     * Default dash response.
     */
    private static final String GET_DASH_ITEMS_SQL = "SELECT * FROM `dash` ORDER BY `id` ASC;";

    /**
     * Sql update string for updating a users status.
     */
    private static final String PATCH_USER_STATUS = "UPDATE `users` SET `admin`=?, `active`=? WHERE `username`=?";

    /**
     * The SQL query for user information.
     */
    private static final String QUERY_USER_PASSWORD = "SELECT `password`, `active` FROM `users` WHERE `username`=?;";

    /**
     * Sql query for obtaining a users info.
     */
    private static final String QUERY_USER_INFO = "SELECT `admin`, `active` FROM `users` WHERE `username`=?;";

    /**
     * Sql query for obtaining all users.
     */
    private static final String QUERY_ALL_USERS = "SELECT `username` FROM `users` WHERE 1;";

    @Override
    public boolean getAdminStatus(@Nonnull final String authUsername) {
        boolean isAdmin = false;
        // Query if user is an admin
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_ADMIN);
            queryStatement.setString(1, authUsername);
            // Process results
            final ResultSet result = queryStatement.executeQuery();
            if (result.next()) {
                isAdmin = result.getBoolean("admin");
            }
            result.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user admin status sql error", e);
        }
        return isAdmin;
    }

    @Override
    public boolean userExists(@Nonnull final String username) {
        boolean returnResult = true;
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_EXISTS);
            queryStatement.setString(1, username);
            // Parse result information
            final ResultSet result = queryStatement.executeQuery();
            returnResult = result.next();
            result.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Check if user exists sql error", e);
        }
        return returnResult;
    }

    @Override
    public boolean createUser(@Nonnull final String username,
                              @Nonnull final String password,
                              final boolean isAdmin,
                              final boolean isActive) {
        // Hash the users password
        final String userPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        // Prep user INSERT query
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(CREATE_USER);
            // Set query data
            queryStatement.setString(1, username);
            queryStatement.setString(2, userPassword);
            queryStatement.setBoolean(3, isAdmin);
            queryStatement.setBoolean(4, isActive);
            // Execute insert statement
            queryStatement.executeUpdate();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Create user sql error", e);
            return false;
        }
        return true;
    }

    @Override
    public List<String> getDashItems() {
        final List<String> strings = new ArrayList<>();
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(GET_DASH_ITEMS_SQL);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                strings.add(resultSet.getString("value"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            LOGGER.error("Dash sql error", e);
        }
        return strings;
    }

    @Override
    public boolean updateUser(final boolean makeAdmin, final boolean setActive, @Nonnull final String newUsername) {
        // Prep sql query
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(PATCH_USER_STATUS);
            // Set sql query params
            queryStatement.setBoolean(1, makeAdmin);
            queryStatement.setBoolean(2, setActive);
            queryStatement.setString(3, newUsername);
            // Execute update
            queryStatement.executeUpdate();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Update user info sql error", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean isLoginInfoValid(@Nonnull final String username, @Nonnull final String password) {
        // Set initial variables
        String hashPassword = "";
        boolean isActive = false;
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            // Query database
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_PASSWORD);
            queryStatement.setString(1, username);
            final ResultSet result = queryStatement.executeQuery();
            // Obtain results
            if (result.next()) {
                hashPassword = result.getString("password");
                isActive = result.getBoolean("active");
            }
            result.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Check user info sql error", e);
        }
        // Check if user is active and their password matches
        return isActive && hashPassword.length() != 0 && BCrypt.checkpw(password, hashPassword);
    }

    @Override
    @Nullable
    public Map.Entry<Boolean, Boolean> getUserResponse(@Nonnull final String username) {
        AbstractMap.SimpleEntry<Boolean, Boolean> response = null;
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_INFO);
            // Set sql query params
            queryStatement.setString(1, username);
            // Execute update
            final ResultSet resultSet = queryStatement.executeQuery();
            if (resultSet.next()) {
                response = new AbstractMap.SimpleEntry<>(
                        resultSet.getBoolean("active"),
                        resultSet.getBoolean("admin")
                );
            }
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user information sql error.", e);
        }
        return response;
    }

    @Override
    public List<String> getUserList() {
        final List<String> userList = new ArrayList<>();
        try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
            final PreparedStatement queryStatement = connection.prepareStatement(QUERY_ALL_USERS);
            final ResultSet resultSet = queryStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(resultSet.getString(1));
            }
            resultSet.close();
            queryStatement.close();
        } catch (SQLException e) {
            LOGGER.debug("Get user list sql error.", e);
        }
        return userList;
    }
}
