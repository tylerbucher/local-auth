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
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * A module to handle all security and authentication related functions.
 *
 * @author Tyler Bucher
 */
public final class SecurityDbModule implements SecurityModule {

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The security module singleton.
         */
        private static final SecurityDbModule INSTANCE = new SecurityDbModule();
    }

    /**
     * @return {@link SecurityDbModule} singleton.
     */
    public static SecurityDbModule getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public boolean isUserAdmin(@Nonnull final String authUsername) {
        return LocalAuth.getDbModule().getAdminStatus(authUsername);
    }

    /**
     * Checks to see if the user is an admin.
     *
     * @param authUsername the attempted admins username.
     * @param dbModule     the module instance to use.
     * @return true if the user is an admin false otherwise.
     */
    public boolean isUserAdmin(@Nonnull final String authUsername, @Nonnull final DbModule dbModule) {
        return dbModule.getAdminStatus(authUsername);
    }

    @Override
    public boolean isJWSTokenValid(@Nullable final String authCookie) {
        if (authCookie == null || authCookie.length() == 0) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(LocalAuth.getSecretKey()).parseClaimsJws(authCookie);
        } catch (JwtException e) {
            ApiController.LOGGER.debug("JWS Token Parse Error", e);
            return false;
        }
        return true;
    }

    @Override
    public String getJWSToken(@Nonnull final String username, @Nonnull final Date expirationDate) {
        return Jwts.builder()
                .setExpiration(expirationDate)
                .claim("username", username)
                .signWith(LocalAuth.getSecretKey())
                .compact();
    }

    @Override
    public String getJWSUsernameClaim(@Nonnull final String token) {
        // Attempt to extract the username
        if (token.length() == 0) {
            ApiController.LOGGER.debug("JWS Token Parse Error");
            return "";
        }
        try {
            final Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(LocalAuth.getSecretKey())
                    .parseClaimsJws(token);
            return jws.getBody().get("username", String.class);
        } catch (JwtException ex) {
            ApiController.LOGGER.debug("JWS Token Parse Error", ex);
            return "";
        }
    }
}
