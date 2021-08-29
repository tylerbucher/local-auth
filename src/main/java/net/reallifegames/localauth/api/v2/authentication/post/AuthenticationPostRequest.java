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
package net.reallifegames.localauth.api.v2.authentication.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Jws;
import net.reallifegames.localauth.Config;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.models.UserModel;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Get request model for the /authentication endpoint.
 *
 * @author Tyler Bucher
 */
public class AuthenticationPostRequest {

    /**
     * The user to try and login as.
     */
    private final String email;

    /**
     * Password of this login attempt.
     */
    private final String password;

    /**
     * States if the jws token should live longer then the browsing session.
     */
    public final boolean rememberMe;

    @JsonCreator
    public AuthenticationPostRequest(@JsonProperty ("email") @Nonnull final String email,
                                     @JsonProperty ("password") @Nonnull final String password,
                                     @JsonProperty ("rememberMe") final boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    /**
     * @param dbModule the module instance to use.
     * @return A set of error codes based on the state of the data.<ul>
     * <li>0: if the user is valid, active and the password is correct.</li>
     * <li>-1: if the user is valid, used a correct password but not active.</li>
     * <li>-2: if the user is valid, but used an incorrect password.</li>
     * <li>-3: if the user is not valid.</li>
     * </ul>
     */
    public int validate(@Nonnull final MongoDbModule dbModule) {
        final UserModel userModel = dbModule.getUserModelByEmail(this.email);
        if (userModel == null) {
            return -3;
        } else if (!BCrypt.checkpw(this.password, userModel.passwordHash)) {
            return -2;
        } else if (userModel.active) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * @return a newly created {@link Jws} token for the user.
     */
    String generateToken(@Nonnull final SecurityModule securityModule, @Nonnull final Config config) {
        return securityModule.getJWSToken(this.email, new Date(System.currentTimeMillis() + config.getJwtExpireTime()));
    }
}
