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
package net.reallifegames.localauth.api.v1.createUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A create user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class CreateUserRequest {

	/**
	 * Sql query for checking if a user exists.
	 */
	private static final String QUERY_USER_EXISTS = "SELECT `username` FROM `users` WHERE `username`=?;";

	/**
	 * Sql query for creating a new user.
	 */
	private static final String CREATE_USER = "INSERT INTO `users`(`username`, `password`, `admin`, `active`) VALUES (?, ?, ?, ?);";

	/**
	 * Sql query for checking if a user is an admin.
	 */
	private static final String QUERY_USER_ADMIN = "SELECT `admin` FROM `users` WHERE `username`=?;";

	/**
	 * Requested username for new user.
	 */
	private final String username;

	/**
	 * Requested password for new user.
	 */
	private final String Password;

	/**
	 * Constructor for Jackson json marshalling.
	 *
	 * @param username requested username for new user.
	 * @param password requested password for new user.
	 */
	@JsonCreator
	public CreateUserRequest(@Nonnull @JsonProperty ("username") final String username,
	                         @Nonnull @JsonProperty ("password") final String password) {
		this.username = username;
		Password = password;
	}

	/**
	 * @return true if a user exists in the database false otherwise.
	 */
	boolean userExists() {
		return CreateUserRequest.userExists(this.username);
	}

	/**
	 * @return false if the username or password length is zero.
	 */
	boolean isDataValid() {
		return !(this.username.length() == 0 || this.Password.length() == 0);
	}

	/**
	 * Checks to see if the user is an admin.
	 *
	 * @param authUsername the attempted admins username.
	 * @return true if the user is an admin false otherwise.
	 */
	public static boolean isUserAuthenticated(@Nonnull final String authUsername) {
		boolean isAdmin = false;
		// Query if user is an admin
		if (LocalAuth.isDebugMode()) {
			isAdmin = authUsername.equals("test");
		} else {
			try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
				final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_ADMIN);
				queryStatement.setString(1, authUsername);
				// Process results
				final ResultSet result = queryStatement.executeQuery();
				if (result.next()) {
					isAdmin = result.getBoolean("admin");
				}
				result.close();
				queryStatement.close();
			} catch (SQLException e) {
				ApiController.LOGGER.debug("Create user authenticate error", e);
			}
		}
		return isAdmin;
	}

	/**
	 * Checks to see if a user already exists.
	 *
	 * @param username the username to check.
	 * @return true if the user exists false otherwise.
	 */
	public static boolean userExists(@Nonnull final String username) {
		boolean returnResult = true;
		// Query for user information
		if (LocalAuth.isDebugMode()) {
			returnResult = username.equals("test");
		} else {
			try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
				final PreparedStatement queryStatement = connection.prepareStatement(QUERY_USER_EXISTS);
				queryStatement.setString(1, username);
				// Parse result information
				final ResultSet result = queryStatement.executeQuery();
				returnResult = result.next();
				result.close();
				queryStatement.close();
			} catch (SQLException e) {
				ApiController.LOGGER.debug("Create user request check if user exists error", e);
			}
		}
		return returnResult;
	}

	/**
	 * Attempts to create a new user.
	 *
	 * @return true if the user was created false otherwise.
	 */
	public boolean createUser() {
		if(LocalAuth.isDebugMode()) {
			return !this.username.equals("test500");
		}
		// Hash the users password
		final String userPassword = BCrypt.hashpw(this.Password, BCrypt.gensalt());
		// Prep user INSERT query
		try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
			final PreparedStatement queryStatement = connection.prepareStatement(CREATE_USER);
			// Set query data
			queryStatement.setString(1, this.username);
			queryStatement.setString(2, userPassword);
			queryStatement.setBoolean(3, false);
			queryStatement.setBoolean(4, false);
			// Execute insert statement
			queryStatement.executeUpdate();
			queryStatement.close();
		} catch (SQLException e) {
			ApiController.LOGGER.debug("Create user request create user error", e);
			return false;
		}
		return true;
	}
}
