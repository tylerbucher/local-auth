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
package net.reallifegames.localauth.api.v1.editUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.DbModule;

import javax.annotation.Nonnull;

/**
 * A edit user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class EditUserRequest {

    /**
     * Username for user to update.
     */
    private final String updateUsername;

    /**
     * Requested admin status for user.
     */
    public final boolean admin;

    /**
     * Requested account status.
     */
    private final boolean active;

    /**
     * Constructor for Jackson json marshalling.
     *
     * @param updateUsername username for user to update.
     * @param admin          requested admin status for user.
     * @param active         requested account status.
     */
    @JsonCreator
    public EditUserRequest(@Nonnull @JsonProperty ("updateUsername") final String updateUsername,
                           @JsonProperty ("admin") final boolean admin,
                           @JsonProperty ("active") final boolean active) {
        this.updateUsername = updateUsername;
        this.admin = admin;
        this.active = active;
    }

    /**
     * Attempts to update a users status.
     *
     * @param dbModule the module instance to use.
     */
    public boolean updateUser(@Nonnull final DbModule dbModule) {
        return dbModule.updateUser(this.admin, this.active, this.updateUsername);
    }
}
