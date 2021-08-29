/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 - Present, Tyler Bucher
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
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.staticfiles.Location;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v2.authentication.get.AuthenticationGetController;
import net.reallifegames.localauth.api.v2.authentication.post.AuthenticationPostController;
import net.reallifegames.localauth.api.v2.invites.delete.InviteDeleteController;
import net.reallifegames.localauth.api.v2.invites.get.InviteGetController;
import net.reallifegames.localauth.api.v2.invites.patch.InvitePatchController;
import net.reallifegames.localauth.api.v2.invites.post.InvitePostController;
import net.reallifegames.localauth.api.v2.nodes.delete.NodeDeleteController;
import net.reallifegames.localauth.api.v2.nodes.get.NodeGetController;
import net.reallifegames.localauth.api.v2.nodes.patch.NodePatchController;
import net.reallifegames.localauth.api.v2.nodes.patch.NodePatchRequest;
import net.reallifegames.localauth.api.v2.nodes.post.NodePostController;
import net.reallifegames.localauth.api.v2.permissions.get.PermissionsGetController;
import net.reallifegames.localauth.api.v2.users.delete.UserDeleteController;
import net.reallifegames.localauth.api.v2.users.get.UserGetController;
import net.reallifegames.localauth.api.v2.users.patch.UserPatchController;
import net.reallifegames.localauth.api.v2.users.post.UserPostController;
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
     * Static db module reference.
     */
    private static MongoDbModule DB_MODULE;

    /**
     * Static db module reference.
     */
    private static SecurityModule SECURITY_MODULE;

    /**
     * Global application configuration
     */
    private static Config CONFIG;

    /**
     * Main class for the Local Auth application.
     *
     * @param args the program arguments to run with.
     */
    public static void main(@Nonnull final String[] args) {
        CONFIG = new Config();
        LocalAuth.DB_MODULE = MongoDbModule.getInstance();
        LocalAuth.SECURITY_MODULE = SecurityModule.getInstance();
        final Javalin javalinApp = Javalin.create();
        // CORS information
        javalinApp.before("*/*", (context)->{
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Request-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
        });
        // Api v1 pathing group
        javalinApp.routes(()->ApiBuilder.path("/api/v2", ()->{
            // Authentication
            ApiBuilder.get("/", ApiController::getApiInformation);
            ApiBuilder.get("/authentication", AuthenticationGetController::getAuthentication);
            ApiBuilder.post("/authentication", AuthenticationPostController::postAuthentication);
            // Invites
            ApiBuilder.get("/invites", InviteGetController::getInvites);
            ApiBuilder.post("/invites", InvitePostController::postInvite);
            ApiBuilder.patch("/invites", InvitePatchController::patchInvite);
            ApiBuilder.delete("/invites/*", InviteDeleteController::deleteInvite);
            // Users
            ApiBuilder.get("/users/*", UserGetController::getUsers);
            ApiBuilder.post("/users", UserPostController::postUser);
            ApiBuilder.patch("/users", UserPatchController::patchUser);
            ApiBuilder.delete("/users/*", UserDeleteController::deleteUser);
            // Nodes
            ApiBuilder.get("/nodes", NodeGetController::getNodes);
            ApiBuilder.post("/nodes", NodePostController::postNode);
            ApiBuilder.patch("/nodes", NodePatchController::patchNode);
            ApiBuilder.delete("/nodes/*", NodeDeleteController::deleteNode);
            // Permissions
            ApiBuilder.get("/permissions", PermissionsGetController::getPermissions);
        }));

        javalinApp.start(8080);

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            DB_MODULE.close();
        }));
    }

    /**
     * @return the current database module instance.
     */
    public static MongoDbModule getDbModule() {
        return DB_MODULE;
    }

    /**
     * @return the current security module instance.
     */
    public static SecurityModule getSecurityModule() {
        return SECURITY_MODULE;
    }

    public static Config getConfig() {
        return CONFIG;
    }
}
