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
package net.reallifegames.localauth.api.v1.users;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base users api controller, which dispatches information about users in this application. This is a secure api
 * endpoint.
 *
 * @author Tyler Bucher
 */
public class UsersController {

	/**
	 * Sql query for obtaining all users.
	 */
	private static final String QUERY_ALL_USERS = "SELECT `username` FROM `users` WHERE 1;";

	/**
	 * Returns all users the the client.
	 */
	@SuppressWarnings ("Duplicates")
	public static void getUsers(@Nonnull final Context context) throws IOException {
		// Check if user is an admin
		final String rawCookie = context.cookie("authToken");
		final String authUsername = ApiController.getJWSUsernameClaim(rawCookie == null ? "" : rawCookie);
		if (!CreateUserRequest.isUserAuthenticated(authUsername)) {
			context.status(401);
			context.result("Unauthorized");
			return;
		}
		context.status(200);
		// Set response payload
		ApiController.jsonContextResponse(new UsersResponse(ApiController.apiResponse, UsersController.getUserList()), context);
	}

	/**
	 * @return a list of all users by username.
	 */
	public static List<String> getUserList() {
		final List<String> userList = new ArrayList<>();
		if (!LocalAuth.isDebugMode()) {
			try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
				final PreparedStatement queryStatement = connection.prepareStatement(QUERY_ALL_USERS);
				final ResultSet resultSet = queryStatement.executeQuery();
				while (resultSet.next()) {
					userList.add(resultSet.getString(1));
				}
				resultSet.close();
				queryStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userList;
	}
}
