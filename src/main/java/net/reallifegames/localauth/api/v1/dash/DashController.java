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
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base dash api controller, which dispatches information about dash endpoints in this application. This is a secure api
 * endpoint.
 *
 * @author Tyler Bucher
 */
public class DashController {

	/**
	 * Default dash response.
	 */
	private static final String GET_DASH_ITEMS_SQL = "SELECT * FROM `dash` ORDER BY `id` ASC;";

	/**
	 * Gets the list of dash endpoints.
	 */
	public static void getEndpoints(@Nonnull final Context context) throws IOException {
		// Set response type and status code
		context.status(200);
		// Set response payload
		ApiController.jsonContextResponse(new DashResponse(ApiController.apiResponse, getDashItems()), context);
	}

	/**
	 * @return the list of dash items.
	 */
	private static List<String> getDashItems() {
		final List<String> strings = new ArrayList<>();
		if (!LocalAuth.isDebugMode()) {
			try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
				final PreparedStatement preparedStatement = connection.prepareStatement(GET_DASH_ITEMS_SQL);
				final ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					strings.add(resultSet.getString("value"));
				}
				resultSet.close();
				preparedStatement.close();
			} catch (SQLException e) {
				LocalAuth.LOGGER.error("Dash sql error", e);
			}
		}
		return strings;
	}
}
