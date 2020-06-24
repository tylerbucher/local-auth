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

import io.javalin.http.Context;
import net.reallifegames.localauth.*;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Edit user Api controller, which processes post information for editing a user. This is a secure endpoint.
 *
 * @author Tyler Bucehr
 */
public class EditUserController {

    /**
     * Default edit user success response.
     */
    private static final EditUserResponse successResponse = new EditUserResponse(ApiController.apiResponse, "success");

    /**
     * Default edit user error response.
     */
    private static final EditUserResponse errorResponse = new EditUserResponse(ApiController.apiResponse, "error");

    /**
     * Updates the user via the patch request.
     *
     * @param context the REST request context to modify.
     * @throws IOException the object could not be marshaled.
     */
    public static void patchUser(@Nonnull final Context context) throws IOException {
        patchUser(context, SecurityDbModule.getInstance(), LocalAuth.getDbModule());
    }

    /**
     * Updates the user via the patch request.
     *
     * @param context        the REST request context to modify if the user is not an admin.
     * @param securityModule the module instance to use.
     * @param dbModule       the module instance to use.
     * @throws IOException if the object could not be marshaled.
     */
    public static void patchUser(@Nonnull final Context context, @Nonnull final SecurityModule securityModule, @Nonnull final DbModule dbModule) throws IOException {
        final EditUserRequest userRequest;
        try {
            userRequest = LocalAuth.objectMapper.readValue(context.body(), EditUserRequest.class);
        } catch (IOException e) {
            ApiController.LOGGER.debug("Api login controller request marshall error", e);
            context.status(400);
            context.result("Bad Request");
            return;
        }
        final EditUserResponse userResponse;
        // Check if user is an admin
        if (ApiController.isUserAdminWithWebContext(context, securityModule)) {
            if (!userRequest.updateUser(dbModule)) {
                context.status(500);
                context.result("Internal Server Error");
                return;
            }
            userResponse = successResponse;
            context.status(200);
        } else {
            userResponse = errorResponse;
        }
        // Return payload
        ApiController.jsonContextResponse(userResponse, context);
    }
}
