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
package net.reallifegames.localauth;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v1.adminStatus.AdminStatusController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserRequest;
import net.reallifegames.localauth.api.v1.dash.DashController;
import net.reallifegames.localauth.api.v1.editUser.EditUserController;
import net.reallifegames.localauth.api.v1.login.LoginController;
import net.reallifegames.localauth.api.v1.tokenValidity.TokenValidityController;
import net.reallifegames.localauth.api.v1.user.UserController;
import net.reallifegames.localauth.api.v1.users.UsersController;
import net.reallifegames.localauth.index.IndexViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The primary class for this application.
 *
 * @author Tyler Bucher
 */
public class LocalAuth {

	/**
	 * The static logger for the application.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

	/**
	 * The global json factory.
	 */
	public static final JsonFactory jsonFactory = new JsonFactory();

	/**
	 * Object mapper for json marshalling.
	 */
	public static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * The proxy path to host the site of if needed.
	 */
	private static String PROXY_PATH = "";

	/**
	 * The database jdbc url.
	 */
	private static String JDBC_URL = "";

	/**
	 * The front end index page as a string.
	 */
	public static String INDEX_PAGE = "";

	/**
	 * The website domain.
	 */
	public static String DOMAIN = "";

	/**
	 * Config fo sql pool information
	 */
	private static HikariConfig hikariConfig;

	/**
	 * Sql pool.
	 */
	private static HikariDataSource dataSource;

	/**
	 * Puts the application into testing mode. This mode will generally bypass all database calls.
	 */
	private static boolean debugMode = false;

	/**
	 * The sql query for creating a users table.
	 */
	private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS `users` (`username` VARCHAR(25) " +
			"NOT NULL, `password` VARCHAR(60) NOT NULL, `admin` BOOLEAN NOT NULL, `active` BOOLEAN NOT NULL, " +
			"PRIMARY KEY (`username`)) ENGINE = InnoDB;";

	/**
	 * The sql query for creating a dash table.
	 */
	private static final String CREATE_DASH_TABLE = "CREATE TABLE IF NOT EXISTS `dash` ( `id` INT UNSIGNED NOT NULL , `value` " +
			"VARCHAR(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL , UNIQUE `id_unique` (`id`)) " +
			"ENGINE = InnoDB;";

	/**
	 * Main class for the Local Auth application.
	 *
	 * @param args the program arguments to run with.
	 */
	public static void main(@Nonnull final String[] args) {
		LocalAuth.PROXY_PATH = args[1];
		LocalAuth.JDBC_URL = args[2];
		LocalAuth.DOMAIN = args[3];
		LocalAuth.objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		LocalAuth.loadIndexPage();
		LocalAuth.loadConnectionPool();
		// Create users table
		LocalAuth.createTables();
		// Check for first time app launch
		LocalAuth.firstTimeLaunch();
		// Set spark port
		final Javalin javalinApp = Javalin.create();
		// Static files for front end
		javalinApp.config.addStaticFiles("/public");
		// CORS information
		javalinApp.before("*/*", (context)->{
			context.header("Access-Control-Allow-Origin", "*");
			context.header("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS");
			context.header("Access-Control-Allow-Headers", "*");
			context.header("Access-Control-Request-Headers", "*");
			context.header("Access-Control-Allow-Credentials", "true");
		});
		// Api v1 pathing group
		javalinApp.routes(()->ApiBuilder.path(LocalAuth.PROXY_PATH + "/api/v1", ()->{
			// Root api path controller
			ApiBuilder.get("/", ApiController::getApiInformation);
			// Login api controller
			ApiBuilder.post("/login", LoginController::postLoginUser);
			// Check to see if a token is valid api controller
			ApiBuilder.before("/tokenValidity", ApiController::beforeApiAuthentication);
			ApiBuilder.get("/tokenValidity", TokenValidityController::getTokenValidity);
			// Get a users admin status api controller
			ApiBuilder.before("/adminStatus", ApiController::beforeApiAuthentication);
			ApiBuilder.get("/adminStatus", AdminStatusController::getAdminStatus);
			// Create new user api controller
			ApiBuilder.before("/createUser", ApiController::beforeApiAuthentication);
			ApiBuilder.post("/createUser", CreateUserController::postNewUser);
			// Edit user info api controller
			ApiBuilder.before("/editUser", ApiController::beforeApiAuthentication);
			ApiBuilder.patch("/editUser", EditUserController::patchUser);
			// List of users api controller
			ApiBuilder.before("/users", ApiController::beforeApiAuthentication);
			ApiBuilder.get("/users", UsersController::getUsers);
			// User info api controller
			ApiBuilder.before("/user/:username", ApiController::beforeApiAuthentication);
			ApiBuilder.get("/user/:username", UserController::getUser);
			// Dash api controller
			ApiBuilder.before("/dash", ApiController::beforeApiAuthentication);
			ApiBuilder.get("/dash", DashController::getEndpoints);
		}));
		// Spark path for all front end pages
		javalinApp.get(LocalAuth.PROXY_PATH + "/*", IndexViewController::getIndexPage);
		javalinApp.start(Integer.parseInt(args[0]));
	}

	private static void loadConnectionPool() {
		hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(getJdbcUrl());
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
		hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
		hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
		hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
		hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
		hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
		hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
		dataSource = new HikariDataSource(hikariConfig);
	}

	/**
	 * Attempts to create the users table.
	 */
	private static void createTables() {
		try (final Connection connection = LocalAuth.getDataSource().getConnection()) {
			final PreparedStatement queryStatement = connection.prepareStatement(CREATE_USERS_TABLE);
			queryStatement.execute();
			final PreparedStatement queryStatement2 = connection.prepareStatement(CREATE_DASH_TABLE);
			queryStatement2.execute();
			queryStatement.close();
			queryStatement2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add admin account if this is the first time running the application.
	 */
	private static void firstTimeLaunch() {
		if (UsersController.getUserList().isEmpty()) {
			final CreateUserRequest createUserRequest = new CreateUserRequest("admin", "admin");
			createUserRequest.createUser();
		}
	}

	/**
	 * @return the database jdbc url.
	 */
	public static String getJdbcUrl() {
		return JDBC_URL;
	}

	/**
	 * Loads the index page as a static string.
	 */
	private static void loadIndexPage() {
		try {
			// Get file uri
			URI uri = LocalAuth.class.getResource("/private/index.html").toURI();
			// Load index page
			INDEX_PAGE = new String(Files.readAllBytes(Paths.get(uri)), Charset.defaultCharset());
		} catch (IOException | URISyntaxException e) {
			LocalAuth.LOGGER.debug("Error loading index page", e);
		}
	}

	/**
	 * @return the sql connection pool.
	 */
	public static HikariDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @return States if the application is in debug mode.
	 */
	public static boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Puts the application into testing mode. This mode will generally bypass all database calls.
	 *
	 * @param debugMode boolean state to put the application into.
	 */
	public static void setDebugMode(boolean debugMode) {
		LocalAuth.debugMode = debugMode;
	}
}
