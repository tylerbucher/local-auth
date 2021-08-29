/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 - Present, Tyler Bucher
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
package net.reallifegames.localauth.api.v2.users.delete;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.MongoDbModule;

import javax.annotation.Nonnull;

/**
 * Delete data request model.
 *
 * @author Tyler Bucher
 */
public class UserDeleteRequest {

    /**
     * User email to delete.
     */
    private final String email;

    /**
     * @param email the email to delete.
     */
    public UserDeleteRequest(@JsonProperty ("id") @Nonnull final String email) {
        this.email = email;
    }

    /**
     * Attempts to create a new user.
     *
     * @param dbModule the module instance to use.
     * @return true if the user was created false otherwise.
     */
    public boolean deletePlayer(@Nonnull final MongoDbModule dbModule) {
        return dbModule.deleteUser(this.email);
    }
}
