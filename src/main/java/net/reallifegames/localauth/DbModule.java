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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

/**
 * An interface to define database agnostic functions.
 *
 * @author Tyler Bucher
 */
public interface DbModule {

    /**
     * Attempts to create the db tables.
     */
    void createTables();

    /**
     * Attempts to create the db tables.
     */
    void createTables(@Nonnull final String... tableStatements);

    /**
     * Checks to see if the user is an admin.
     *
     * @param authUsername the attempted admins username.
     * @return true if the user is an admin false otherwise.
     */
    boolean getAdminStatus(@Nonnull final String authUsername);

    /**
     * Checks to see if a user already exists.
     *
     * @param username the username to check.
     * @return true if the user exists false otherwise.
     */
    boolean userExists(@Nonnull final String username);

    /**
     * Attempts to create a new user.
     *
     * @param username name of the new user.
     * @param password plain text password for the user.
     * @param isAdmin  should the new user be an admin.
     * @param isActive can the new user login.
     * @return true if the user was created false otherwise.
     */
    boolean createUser(@Nonnull final String username,
                       @Nonnull final String password,
                       final boolean isAdmin,
                       final boolean isActive);

    /**
     * @return the list of dash items.
     */
    List<String> getDashItems();

    /**
     * Updates a users information in a db.
     *
     * @param makeAdmin    sets the user as an admin.
     * @param setActive    allows the use to be active (login).
     * @param userToUpdate the username of the user to update.
     * @return true if the user was updated false otherwise.
     */
    boolean updateUser(final boolean makeAdmin, final boolean setActive, @Nonnull final String userToUpdate);

    /**
     * Checks to see if the login info provided is valid.
     *
     * @param username the username to get a password for.
     * @param password the password to hash and check.
     * @return true if the password matches the hashed password in the db.
     */
    boolean isLoginInfoValid(@Nonnull final String username, @Nonnull final String password);

    /**
     * Retrieves a users admin and active status from a database.
     *
     * @param username the name of the user to get info for.
     * @return users admin and active status from a database or null if user not found.
     */
    @Nullable
    Map.Entry<Boolean, Boolean> getUserResponse(@Nonnull final String username);

    /**
     * @return the list of users in the db.
     */
    List<String> getUserList();
}
