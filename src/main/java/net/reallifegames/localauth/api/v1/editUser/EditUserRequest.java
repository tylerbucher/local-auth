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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A edit user request represented as a jackson marshallable object.
 *
 * @author Tyler Bucher
 */
public class EditUserRequest {

	/**
	 * Sql update string for updating a users status.
	 */
	private static final String PATCH_USER_STATUS = "UPDATE `users` SET `admin`=?, `active`=? WHERE `username`=?";

	/**
	 * Username for user to update.
	 */
	public final String updateUsername;

	/**
	 * Requested admin status for user.
	 */
	public final boolean admin;

	/**
	 * Requested account status.
	 */
	public final boolean active;

	/**
	 * Constructor for Jackson json marshalling.
	 *
	 * @param updateUsername username for user to update.
	 * @param admin          requested admin status for user.
	 * @param active         requested account status.
	 */
	@JsonCreator
	public EditUserRequest(@Nonnull @JsonProperty ("updateUsername") final String updateUsername,
	                       @JsonProperty ("admin") final boolean admin,
	                       @JsonProperty ("active") final boolean active) {
		this.updateUsername = updateUsername;
		this.admin = admin;
		this.active = active;
	}

	/**
	 * Attempts to update a users status.
	 */
	public void updateUser() {
		if (LocalAuth.isDebugMode()) {
			return;
		}
		// Prep sql query
		try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
			final PreparedStatement queryStatement = connection.prepareStatement(PATCH_USER_STATUS);
			// Set sql query params
			queryStatement.setBoolean(1, admin);
			queryStatement.setBoolean(2, active);
			queryStatement.setString(3, updateUsername);
			// Execute update
			queryStatement.executeUpdate();
			queryStatement.close();
		} catch (SQLException e) {
			ApiController.LOGGER.debug("Api edit user request update error", e);
		}
	}
}
