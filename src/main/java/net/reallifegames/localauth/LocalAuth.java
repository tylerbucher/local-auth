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
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.staticfiles.Location;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

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
     * The database jdbc type.
     */
    private static String JDBC_TYPE = "";

    /**
     * The database jdbc url.
     */
    private static String JDBC_URL = "";

    /**
     * The website domain.
     */
    public static String DOMAIN = "";

    /**
     * Static db module reference.
     */
    private static DbModule DB_MODULE;

    /**
     * Main class for the Local Auth application.
     *
     * @param args the program arguments to run with.
     */
    public static void main(@Nonnull final String[] args) {
        LocalAuth.JDBC_TYPE = args[0];
        LocalAuth.JDBC_URL = args[1];
        LocalAuth.DOMAIN = args[2];
        LocalAuth.objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            LocalAuth.DB_MODULE = LocalAuth.findDbModule(LocalAuth.JDBC_TYPE);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error loading sq lite driver.", e);
        }
        // Create users table
        LocalAuth.getDbModule().createTables();
        // Check for first time app launch
        LocalAuth.firstTimeLaunch(LocalAuth.getDbModule());

        // Set spark port
        final Javalin javalinApp = Javalin.create(config->{
            config.addStaticFiles(System.getProperty("user.dir") + "/public", Location.EXTERNAL);
            config.addSinglePageRoot("/", System.getProperty("user.dir") + "/public/" + "index.html", Location.EXTERNAL);
        });

        // CORS information
        javalinApp.before("*/*", (context)->{
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Request-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
        });
        // Api v1 pathing group
        javalinApp.routes(()->ApiBuilder.path("/api/v1", ()->{
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

        javalinApp.start(8080);
    }

    private static DbModule findDbModule(@Nonnull final String key) throws ClassNotFoundException {
        switch (key) {
            case "mysql":
                return MySqlModule.getInstance();
            default:
                Class.forName("org.sqlite.JDBC");
                return SqLiteModule.getInstance();
        }
    }

    /**
     * Add admin account if this is the first time running the application.
     */
    private static void firstTimeLaunch(@Nonnull final DbModule dbModule) {
        if (dbModule.getUserList().isEmpty()) {
            dbModule.createUser("admin", "admin", true, true);
        }
    }

    /**
     * @return the current database module instance.
     */
    public static DbModule getDbModule() {
        return DB_MODULE;
    }

    /**
     * @return the current security module instance.
     */
    public static SecurityModule getSecurityModule() {
        return SecurityDbModule.getInstance();
    }

    /**
     * @return the database jdbc url.
     */
    public static String getJdbcUrl() {
        return JDBC_URL;
    }
}
