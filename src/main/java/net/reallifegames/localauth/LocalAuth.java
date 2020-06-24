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
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v1.adminStatus.AdminStatusController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserController;
import net.reallifegames.localauth.api.v1.dash.DashController;
import net.reallifegames.localauth.api.v1.editUser.EditUserController;
import net.reallifegames.localauth.api.v1.login.LoginController;
import net.reallifegames.localauth.api.v1.tokenValidity.TokenValidityController;
import net.reallifegames.localauth.api.v1.user.UserController;
import net.reallifegames.localauth.api.v1.users.UsersController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

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
     * The JWT secretKey auto initialize.
     */
    private static boolean SECRET_KEY_AUTO = true;

    /**
     * The JWT secretKey.
     */
    private static SecretKey SECRET_KEY;

    /**
     * The amount of time in the future the token will expire.
     */
    public static long JWT_EXPIRE_TIME = 604800000L;

    /**
     * Static db module reference.
     */
    private static DbModule DB_MODULE;

    /**
     * Static db module reference.
     */
    private static SecurityModule SECURITY_MODULE;

    /**
     * Static list of hex characters.
     */
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes();

    /**
     * Main class for the Local Auth application.
     *
     * @param args the program arguments to run with.
     */
    public static void main(@Nonnull final String[] args) {
        LocalAuth.JDBC_TYPE = System.getenv("JDBC_TYPE");
        LocalAuth.JDBC_URL = System.getenv("JDBC_URL");
        LocalAuth.DOMAIN = System.getenv("DOMAIN");
        final String autoEnvString = System.getenv("SECRET_KEY_AUTO");
        if (autoEnvString != null) {
            LocalAuth.SECRET_KEY_AUTO = Boolean.parseBoolean(autoEnvString);
        }
        final String secretKeyString = System.getenv("SECRET_KEY");
        if(LocalAuth.SECRET_KEY_AUTO  || secretKeyString == null) {
            LocalAuth.SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            LocalAuth.SECRET_KEY = Keys.hmacShaKeyFor(LocalAuth.hexToBytes(secretKeyString));
        }
        final String jwtEnvString = System.getenv("JWT_EXPIRE_TIME");
        if (jwtEnvString != null) {
            LocalAuth.JWT_EXPIRE_TIME = Long.parseLong(jwtEnvString);
        }
        LocalAuth.objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            LocalAuth.DB_MODULE = LocalAuth.findDbModule(LocalAuth.JDBC_TYPE);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error loading sq lite driver.", e);
        }
        // Create tables
        LocalAuth.DB_MODULE.createTables();
        // Check for first time app launch
        LocalAuth.firstTimeLaunch(LocalAuth.getDbModule());
        // Setup security modules
        LocalAuth.SECURITY_MODULE = SecurityDbModule.getInstance();
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

    /**
     * @param hexString the string to convert.
     * @return the byte array for a hex string.
     */
    private static byte[] hexToBytes(@Nonnull final String hexString) {
        final int length = hexString.length();
        final byte[] byteArray = new byte[length / 2];
        for (int i = 0, j = 0; i < length; i += 2, j++) {
            byteArray[i / 2] = (byte) ((charToBase16(hexString.charAt(i)) << 4) + charToBase16(hexString.charAt(i + 1)));
        }
        return byteArray;
    }

    /**
     * Quick converts a valid base 16 character to an int.
     *
     * @param c the character to convert.
     * @return the character as a base 16 number.
     */
    private static int charToBase16(final char c) {
        return c < 58 ? c - 48 : c - 55;
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
        return SECURITY_MODULE;
    }

    /**
     * @return the database jdbc url.
     */
    public static String getJdbcUrl() {
        return JDBC_URL;
    }

    public static SecretKey getSecretKey() {
        return SECRET_KEY;
    }

    public static long getJwtExpireTime() {
        return JWT_EXPIRE_TIME;
    }
}
