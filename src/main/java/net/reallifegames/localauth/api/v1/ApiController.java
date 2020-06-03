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
package net.reallifegames.localauth.api.v1;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import io.javalin.http.Context;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.SecurityDbModule;
import net.reallifegames.localauth.SecurityModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

/**
 * Base Api controller, handles initial authentication and api versioning responses.
 *
 * @author Tyler Bucher
 */
public class ApiController {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    /**
     * The SHA-256 secret key. This will be regenerated every time the application starts up.
     */
    private static SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * The amount of time in the future the token will expire.
     */
    public static final long DEFAULT_EXPIRE_TIME_EXT = 604800000L;

    /**
     * Global api version response.
     */
    public static final ApiResponse apiResponse = new ApiResponse("v1");

    /**
     * Should be called before all secure api end-points.
     *
     * @param context the REST request context to modify.
     */
    public static void beforeApiAuthentication(@Nonnull final Context context) {
        // Set response type
        context.contentType("application/json");
        // Check if user is authenticated
        if (!isJWSTokenValid(context.cookie("authToken"))) {
            context.status(401);
            context.result("Unauthorized");
        }
    }

    /**
     * Returns the current version of this api.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    @SuppressWarnings ("Duplicates")
    public static void getApiInformation(@Nonnull final Context context) throws IOException {
        // Set response status
        context.status(200);
        // Prep Jackson-JSON
        ApiController.jsonContextResponse(apiResponse, context);
    }

    /**
     * Pre packaged json response with an object.
     *
     * @param marshallObject the response to marshall.
     * @param context        the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    public static void jsonContextResponse(@Nonnull final Object marshallObject, @Nonnull final Context context) throws IOException {
        context.contentType("application/json");
        // Prep Jackson-JSON
        final SegmentedStringWriter stringWriter = new SegmentedStringWriter(LocalAuth.jsonFactory._getBufferRecycler());
        final JsonGenerator jsonGenerator = LocalAuth.jsonFactory.createGenerator(stringWriter);
        // Append api response
        LocalAuth.objectMapper.writeValue(jsonGenerator, marshallObject);
        jsonGenerator.flush();
        // Return payload
        context.result(stringWriter.getAndClear());
        jsonGenerator.close();
    }

    /**
     * Gets a {@link Jws} token for a username, and sets its expire time.
     *
     * @param username       the username for this token.
     * @param expirationDate the date for the token to expire at.
     * @return the generated token.
     */
    public static String getJWSToken(@Nonnull final String username, @Nonnull final Date expirationDate) {
        return Jwts.builder()
                .setExpiration(expirationDate)
                .claim("username", username)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Extract the username {@link Claims claim} for the {@link Jws} token payload.
     *
     * @param token the JWS string token to process.
     * @return the obtained username or "".
     */
    public static String getJWSUsernameClaim(@Nonnull final String token) {
        // Attempt to extract the username
        if (token.length() == 0) {
            LOGGER.debug("JWS Token Parse Error");
            return "";
        }
        try {
            final Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return jws.getBody().get("username", String.class);
        } catch (JwtException ex) {
            LOGGER.debug("JWS Token Parse Error", ex);
            return "";
        }
    }

    /**
     * Checks if a {@link Jws} token is valid.
     *
     * @param authCookie the authentication token to verify.
     * @return true if the token is valid false otherwise.
     */
    private static boolean isJWSTokenValid(@Nullable final String authCookie) {
        if (authCookie == null || authCookie.length() == 0) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authCookie);
        } catch (JwtException e) {
            LOGGER.debug("JWS Token Parse Error", e);
            return false;
        }
        return true;
    }

    /**
     * Checks if a user is an admin and modifies the context
     *
     * @param context        the REST request context to modify if the user is not an admin.
     * @param securityModule the module instance to use.
     * @return true if the user is and admin false otherwise.
     */
    public static boolean isUserAdmin(@Nonnull final Context context, @Nonnull final SecurityModule securityModule) {
        final String rawCookie = context.cookie("authToken");
        final String authUsername = ApiController.getJWSUsernameClaim(rawCookie == null ? "" : rawCookie);
        if (!securityModule.isUserAdmin(authUsername)) {
            context.status(401);
            context.result("Unauthorized");
            return false;
        }
        return true;
    }
}
