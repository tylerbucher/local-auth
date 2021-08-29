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
package net.reallifegames.localauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Date;

/**
 * A module to handle all security and authentication related functions.
 *
 * @author Tyler Bucher
 */
public final class SecurityModule {

    /**
     * The static logger for the application.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SecurityModule.class);

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The security module singleton.
         */
        private static final SecurityModule INSTANCE = new SecurityModule();
    }

    /**
     * @return {@link SecurityModule} singleton.
     */
    public static SecurityModule getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Checks if a {@link Jws} token is valid.
     *
     * @param authCookie the authentication token to verify.
     * @return true if the token is valid false otherwise.
     */
    public boolean isJWSTokenValid(@Nonnull final Config config, @Nullable final String authCookie) {
        if (authCookie == null || authCookie.length() == 0) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(config.getSecretKey()).parseClaimsJws(authCookie);
        } catch (JwtException e) {
            LOGGER.debug("JWS Token Parse Error", e);
            return false;
        }
        return true;
    }

    /**
     * Checks if a {@link Jws} token is valid.
     *
     * @param authCookie the authentication token to verify.
     * @return true if the token is valid false otherwise.
     */
    public boolean isJWSTokenValid(@Nullable final String authCookie) {
        return isJWSTokenValid(LocalAuth.getConfig(), authCookie);
    }

    /**
     * Gets a {@link Jws} token for a email, and sets its expire time.
     *
     * @param email          the email for this token.
     * @param expirationDate the date for the token to expire at.
     * @return the generated token.
     */
    public String getJWSToken(@Nonnull final Config config, @Nonnull final String email, @Nonnull final Date expirationDate) {
        return Jwts.builder()
                .setExpiration(expirationDate)
                .claim("email", email)
                .signWith(config.getSecretKey())
                .compact();
    }

    /**
     * Gets a {@link Jws} token for a email, and sets its expire time.
     *
     * @param email          the email for this token.
     * @param expirationDate the date for the token to expire at.
     * @return the generated token.
     */
    public String getJWSToken(@Nonnull final String email, @Nonnull final Date expirationDate) {
        return getJWSToken(LocalAuth.getConfig(), email, expirationDate);
    }

    /**
     * Extract the email {@link Claims claim} for the {@link Jws} token payload.
     *
     * @param token the JWS string token to process.
     * @return the obtained email or "".
     */
    public String getJWSEmailClaim(@Nonnull final Config config, @Nullable final String token) {
        // Attempt to extract the username
        if (token == null || token.length() == 0) {
            LOGGER.debug("JWS Token Parse Error");
            return "";
        }
        try {
            final Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(config.getSecretKey())
                    .parseClaimsJws(token);
            return jws.getBody().get("email", String.class);
        } catch (JwtException ex) {
            LOGGER.debug("JWS Token Parse Error", ex);
            return "";
        }
    }

    /**
     * Extract the email {@link Claims claim} for the {@link Jws} token payload.
     *
     * @param token the JWS string token to process.
     * @return the obtained email or "".
     */
    public String getJWSEmailClaim(@Nullable final String token) {
        return getJWSEmailClaim(LocalAuth.getConfig(), token);
    }

    /**
     * @param hexString the string to convert.
     * @return the byte array for a hex string.
     *
     * @throws NumberFormatException if string is not valid hex.
     */
    public static byte[] hexToBytes(@Nonnull final String hexString) {
        final String correctedString = hexString.toUpperCase();
        // Validate if sting is hex if not throw exception.
        new BigInteger(correctedString, 16);

        final int length = correctedString.length();
        final byte[] byteArray = new byte[length / 2];
        for (int i = 0, j = 0; i < length; i += 2, j++) {
            byteArray[i / 2] = (byte) ((base16CharToInt(correctedString.charAt(i)) << 4) + base16CharToInt(correctedString.charAt(i + 1)));
        }
        return byteArray;
    }

    /**
     * Quick converts a valid base 16 character to an int.
     *
     * @param c the character to convert.
     * @return the character as a base 16 number.
     */
    public static int base16CharToInt(final char c) {
        return c < 58 ? c - 48 : c - 55;
    }
}
