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
package net.reallifegames.localauth.api.v1.adminStatus;

import io.javalin.http.Context;
import net.reallifegames.localauth.SecurityDbModule;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Base token check api controller, which checks if the provided token is valid.
 *
 * @author Tyler Bucher
 */
public class AdminStatusController {

    /**
     * Default edit user success response.
     */
    private static final AdminStatusResponse userResponse = new AdminStatusResponse(ApiController.apiResponse, "User is admin.");

    /**
     * Returns the current version of this api.
     *
     * @param context the module instance to use.
     */
    public static void getAdminStatus(@Nonnull final Context context) throws IOException {
        getAdminStatus(context, SecurityDbModule.getInstance());
    }

    /**
     * Returns the current version of this api.
     *
     * @param context        the REST request context to modify.
     * @param securityModule the module instance to use.
     * @throws IOException if the object could not be marshaled.
     */
    public static void getAdminStatus(@Nonnull final Context context, @Nonnull final SecurityModule securityModule) throws IOException {
        // Check if user is an admin
        if (!ApiController.isUserAdminWithWebContext(context, securityModule)) {
            return;
        }
        // Set response stat us
        context.status(200);
        // Prep Jackson-JSON
        ApiController.jsonContextResponse(userResponse, context);
    }
}
