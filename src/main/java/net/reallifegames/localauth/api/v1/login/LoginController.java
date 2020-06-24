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

import io.javalin.http.Context;
import io.jsonwebtoken.Jws;
import net.reallifegames.localauth.DbModule;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.SqlModule;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import java.io.IOException;

import static net.reallifegames.localauth.api.v1.ApiController.apiResponse;

/**
 * Login Api controller, which processes post information for logging-in.
 *
 * @author Tyler Bucher
 */
public class LoginController {

    /**
     * Login controller api error response.
     */
    private static final LoginResponse loginErrorResponse = new LoginResponse(apiResponse, "error", "");

    /**
     * Attempts to login a user and return their {@link Jws} token if the information is valid.
     *
     * @param context the REST request context to modify.
     * @throws IOException if the object could not be marshaled.
     */
    public static void postLoginUser(@Nonnull final Context context) throws IOException {
        postLoginUser(context, LocalAuth.getDbModule(), LocalAuth.getSecurityModule());
    }

    /**
     * Attempts to login a user and return their {@link Jws} token if the information is valid.
     *
     * @param context  the REST request context to modify.
     * @param dbModule the module instance to use.
     * @throws IOException if the object could not be marshaled.
     */
    public static void postLoginUser(@Nonnull final Context context, @Nonnull final DbModule dbModule, @Nonnull final SecurityModule securityModule) throws IOException {
        // Set response type
        final LoginRequest userRequest;
        try {
            userRequest = LocalAuth.objectMapper.readValue(context.body(), LoginRequest.class);
        } catch (IOException e) {
            ApiController.LOGGER.debug("Api login controller request marshall error", e);
            context.status(400);
            context.result("Bad Request");
            return;
        }
        final LoginResponse userResponse;
        // Check if user exists and is verified
        if (userRequest.userExists(dbModule) && userRequest.isUserAuthenticated(dbModule)) {
            // Return user token and success response
            userResponse = new LoginResponse(apiResponse, "success", userRequest.generateAuthToken(securityModule));
        } else {
            // Return error response
            userResponse = loginErrorResponse;
        }
        // Set response code
        context.status(userResponse.equals(loginErrorResponse) ? 409 : 200);
        final Cookie cookie = new Cookie("authToken", userResponse.token);
        cookie.setDomain(LocalAuth.DOMAIN);
        cookie.setPath("/");
        cookie.setMaxAge(604800);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        context.cookie(cookie);
        // Prep Jackson-JSON
        ApiController.jsonContextResponse(userResponse, context);
    }
}
