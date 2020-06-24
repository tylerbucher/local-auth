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
package net.reallifegames.localauth.api.v1.login;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Jws;
import net.reallifegames.localauth.DbModule;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.SecurityDbModule;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.sql.Date;

/**
 * A login request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class LoginRequest {

    /**
     * The username to check the password for.
     */
    private final String username;

    /**
     * The password to verify.
     */
    private final String password;

    /**
     * Constructor for Jackson json marshalling.
     *
     * @param username the requested login username.
     * @param password the requested login password.
     */
    @JsonCreator
    public LoginRequest(@Nonnull @JsonProperty ("username") final String username,
                        @Nonnull @JsonProperty ("password") final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return true if a user exists in the database false otherwise.
     */
    boolean userExists(@Nonnull final DbModule dbModule) {
        return dbModule.userExists(this.username);
    }

    /**
     * @param dbModule the module instance to use.
     * @return true if the user is able to login false otherwise.
     */
    boolean isUserAuthenticated(@Nonnull final DbModule dbModule) {
        return dbModule.isLoginInfoValid(this.username, this.password);
    }

    /**
     * @return a newly created {@link Jws} token for the user.
     */
    String generateAuthToken(@Nonnull final SecurityModule securityModule) {
        return securityModule.getJWSToken(this.username, new Date(System.currentTimeMillis() +  LocalAuth.getJwtExpireTime()));
    }
}
