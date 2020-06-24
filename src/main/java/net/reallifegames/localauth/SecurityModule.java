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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * An interface to define data agnostic functions.
 *
 * @author Tyler Bucher
 */
public interface SecurityModule {

    /**
     * Checks to see if the user is an admin.
     *
     * @param authUsername the attempted admins username.
     * @return true if the user is an admin false otherwise.
     */
    boolean isUserAdmin(@Nonnull final String authUsername);

    /**
     * Checks if a {@link Jws} token is valid.
     *
     * @param authCookie the authentication token to verify.
     * @return true if the token is valid false otherwise.
     */
    boolean isJWSTokenValid(@Nullable final String authCookie);

    /**
     * Gets a {@link Jws} token for a username, and sets its expire time.
     *
     * @param username       the username for this token.
     * @param expirationDate the date for the token to expire at.
     * @return the generated token.
     */
    String getJWSToken(@Nonnull final String username, @Nonnull final Date expirationDate);

    /**
     * Extract the username {@link Claims claim} for the {@link Jws} token payload.
     *
     * @param token the JWS string token to process.
     * @return the obtained username or "".
     */
    String getJWSUsernameClaim(@Nonnull final String token);
}
