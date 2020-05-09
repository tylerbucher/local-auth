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
package net.reallifegames.localauth.api.v1.user;

import com.fasterxml.jackson.core.JsonGenerator;
import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Base users api controller, which dispatches information about users in this application. This is a secure api
 * endpoint.
 *
 * @author Tyler Bucher
 */
public class UserController {

	/**
	 * Returns all users the the client.
	 */
	@SuppressWarnings ("Duplicates")
	public static void getUser(@Nonnull final Context context) throws IOException {
		// Set response type and status code
		final UserRequest userRequest = new UserRequest(context.pathParam(":username"));
		// Check if user is an admin
		final String rawCookie = context.cookie("authToken");
		final String authUsername = ApiController.getJWSUsernameClaim(rawCookie == null ? "" : rawCookie);
		if (!CreateUserRequest.isUserAuthenticated(authUsername)) {
			context.status(401);
			context.result("Unauthorized");
			return;
		}
		final UserResponse userResponse = userRequest.getUserResponse();
		if (userResponse == null) {
			ApiController.LOGGER.debug("Api user controller db error");
			context.status(500);
			context.result("Internal Server Error");
			return;
		}
		context.status(200);
		// Set response payload
		ApiController.jsonContextResponse(userResponse, context);
	}
}
