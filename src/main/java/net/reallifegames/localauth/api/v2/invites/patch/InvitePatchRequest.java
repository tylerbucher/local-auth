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
package net.reallifegames.localauth.api.v2.invites.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.DataValidation;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.Permissions;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Invite patch request data model.
 *
 * @author Tyler Bucher
 */
public class InvitePatchRequest {

    /**
     * Current email of the invite.
     */
    private final String email;

    /**
     * List of permissions which will be given to the user upon signup.
     */
    private final List<Integer> permissions;

    @JsonCreator
    public InvitePatchRequest(@JsonProperty ("email") @Nonnull final String email,
                              @JsonProperty ("permissions") final List<Integer> permissions) {
        this.email = email;
        this.permissions = permissions;
    }

    /**
     * @return true if the data in this request is valid false otherwise.
     */
    public boolean validate() {
        return DataValidation.isEmailValid(this.email);
    }

    /**
     * @param dbModule        the module instance to use.
     * @param withPermissions should the permissions be updated.
     * @return true if the invite was updated false otherwise.
     */
    public boolean updateInvite(@Nonnull final MongoDbModule dbModule, final boolean withPermissions) {
        return dbModule.updateInvite(this.email, withPermissions ? Permissions.getValidPermissions(this.permissions) : null);
    }
}
