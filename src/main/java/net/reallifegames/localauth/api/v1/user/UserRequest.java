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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class UserRequest {

	/**
	 * Sql query for obtaining a users info.
	 */
	private static final String QUERY_USER_INFO = "SELECT `admin`, `active` FROM `users` WHERE `username`=?;";

	/**
	 * The username to check the password for.
	 */
	private final String username;

	/**
	 * Constructor for Jackson json marshalling.
	 *
	 * @param username the requested login username.
	 */
	@JsonCreator
	public UserRequest(@Nonnull @JsonProperty ("username") final String username) {
		this.username = username;
	}

	/**
	 * @return a user response for a user or null if an error.
	 */
	@Nullable
	public UserResponse getUserResponse() {
		UserResponse response = null;
		// Prep sql query
		if (LocalAuth.isDebugMode()) {
			if (this.username.equals("test")) {
				response = new UserResponse(
						ApiController.apiResponse,
						this.username,
						true,
						true
				);
			}
		} else {
			try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
				final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_INFO);
				// Set sql query params
				queryStatement.setString(1, username);
				// Execute update
				final ResultSet resultSet = queryStatement.executeQuery();
				if (resultSet.next()) {
					response = new UserResponse(
							ApiController.apiResponse,
							this.username,
							resultSet.getBoolean("active"),
							resultSet.getBoolean("admin")
					);
				}
				queryStatement.close();
			} catch (SQLException e) {
				ApiController.LOGGER.debug("Api edit user request update error", e);
			}
		}
		return response;
	}
}
