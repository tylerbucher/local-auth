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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Jws;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserRequest;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nonnull;
import java.sql.*;

/**
 * A login request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class LoginRequest {

	/**
	 * The username to check the password for.
	 */
	private final String username;

	/**
	 * The password to verify.
	 */
	private final String password;

	/**
	 * The SQL query for user information.
	 */
	private static final String QUERY_USER_PASSWORD = "SELECT `password`, `active` FROM `users` WHERE `username`=?;";

	/**
	 * Constructor for Jackson json marshalling.
	 *
	 * @param username the requested login username.
	 * @param password the requested login password.
	 */
	@JsonCreator
	public LoginRequest(@Nonnull @JsonProperty ("username") final String username,
	                    @Nonnull @JsonProperty ("password") final String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * @return true if a user exists in the database false otherwise.
	 */
	boolean userExists() {
		return LocalAuth.isDebugMode() || CreateUserRequest.userExists(this.username);
	}

	/**
	 * @return true if a user is active and their password matches.
	 */
	boolean userAuthenticated() {
		// Set initial variables
		String hashPassword = "";
		boolean isActive = false;
		if (LocalAuth.isDebugMode()) {
			hashPassword = BCrypt.hashpw("test", BCrypt.gensalt());
			isActive = true;
		} else {
			try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
				// Query database
				final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_PASSWORD);
				queryStatement.setString(1, username);
				final ResultSet result = queryStatement.executeQuery();
				// Obtain results
				if (result.next()) {
					hashPassword = result.getString("password");
					isActive = result.getBoolean("active");
				}
				result.close();
				queryStatement.close();
			} catch (SQLException e) {
				ApiController.LOGGER.debug("Login request authenticate check", e);
			}
		}
		// Check if user is active and their password matches
		return isActive && hashPassword.length() != 0 && BCrypt.checkpw(this.password, hashPassword);
	}

	/**
	 * @return a newly created {@link Jws} token for the user.
	 */
	String generateAuthToken() {
		return ApiController.getJWSToken(this.username, new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
	}
}
