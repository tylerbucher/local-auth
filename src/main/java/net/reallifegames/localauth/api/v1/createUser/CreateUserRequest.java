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
package net.reallifegames.localauth.api.v1.createUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.DbModule;

import javax.annotation.Nonnull;

/**
 * A create user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class CreateUserRequest {

    /**
     * Requested username for new user.
     */
    private final String username;

    /**
     * Requested password for new user.
     */
    private final String Password;

    /**
     * Constructor for Jackson json marshalling.
     *
     * @param username requested username for new user.
     * @param password requested password for new user.
     */
    @JsonCreator
    public CreateUserRequest(@Nonnull @JsonProperty ("username") final String username,
                             @Nonnull @JsonProperty ("password") final String password) {
        this.username = username;
        Password = password;
    }

    /**
     * @param dbModule the module instance to use.
     * @return true if a user exists in the database false otherwise.
     */
    boolean userExists(@Nonnull final DbModule dbModule) {
        return dbModule.userExists(this.username);
    }

    /**
     * @return false if the username or password length is zero.
     */
    boolean isDataValid() {
        return !(this.username.length() == 0 || this.Password.length() == 0);
    }

    /**
     * Attempts to create a new user.
     *
     * @param dbModule the module instance to use.
     * @return true if the user was created false otherwise.
     */
    public boolean createUser(@Nonnull final DbModule dbModule) {
        return dbModule.createUser(this.username, this.Password, false, false);
    }
}
