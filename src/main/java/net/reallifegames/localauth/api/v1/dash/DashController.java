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
package net.reallifegames.localauth.api.v1.dash;

import io.javalin.http.Context;
import net.reallifegames.localauth.DbModule;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.SqlModule;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Base dash api controller, which dispatches information about dash endpoints in this application. This is a secure api
 * endpoint.
 *
 * @author Tyler Bucher
 */
public class DashController {

    /**
     * Gets the list of dash endpoints.
     *
     * @param context the REST request context to modify.
     */
    public static void getEndpoints(@Nonnull final Context context) throws IOException {
        getEndpoints(context, LocalAuth.getDbModule());
    }

    /**
     * Gets the list of dash endpoints.
     *
     * @param context  the REST request context to modify.
     * @param dbModule the module instance to use.
     */
    public static void getEndpoints(@Nonnull final Context context, @Nonnull final DbModule dbModule) throws IOException {
        // Set response type and status code
        context.status(200);
        // Set response payload
        ApiController.jsonContextResponse(new DashResponse(ApiController.apiResponse, dbModule.getDashItems()), context);
    }
}
