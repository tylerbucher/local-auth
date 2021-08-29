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
package net.reallifegames.localauth.api.v2.users.get;

import net.reallifegames.localauth.models.UserModel;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A safe version of the UserModel so passwords are not exposed.
 *
 * @author Tyler Bucher
 */
public class SafeUserModel {

    /**
     * Identification for the account.
     */
    public final String email;

    /**
     * Is this user allowed to login.
     */
    public final boolean active;

    /**
     * Is this user pending acceptance.
     */
    public final boolean pending;

    /**
     * The list of permissions for this user.
     */
    public final List<Integer> permissions;

    /**
     * Meta data of nodes for the client side.
     */
    public final String nodeMetaData;

    public SafeUserModel(@Nonnull final String email,
                         final boolean active,
                         final boolean pending,
                         @Nonnull final List<Integer> permissions,
                         @Nonnull final String nodeMetaData) {
        this.email = email;
        this.active = active;
        this.pending = pending;
        this.permissions = permissions;
        this.nodeMetaData = nodeMetaData;
    }

    /**
     * @param userModel the UserModel to convert.
     * @return the converted UserModel.
     */
    public static SafeUserModel fromUserModel(@Nonnull final UserModel userModel) {
        return new SafeUserModel(userModel.email, userModel.active, userModel.pending, userModel.permissions, userModel.nodeMetaData);
    }
}
