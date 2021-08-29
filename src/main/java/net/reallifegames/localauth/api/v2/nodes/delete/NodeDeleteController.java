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
package net.reallifegames.localauth.api.v2.nodes.delete;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.Permissions;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v2.invites.delete.InviteDeleteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Attempts to delete a node based on the id.
 *
 * @author Tyler Bucher
 */
public class NodeDeleteController {

    /**
     * The static logger for this version of the api.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(NodeDeleteController.class);

    /**
     * Default list of permissions for this endpoint.
     */
    private static final List<Integer> PERMISSIONS = Arrays.asList(
            Permissions.IS_USER_ADMIN.value,
            Permissions.IS_USER_SUPER_ADMIN.value,
            Permissions.CAN_USER_DELETE_NODE.value
    );

    /**
     * Attempts to delete a node based on the post data.
     *
     * @param context the REST request context to modify.
     */
    public static void deleteNode(@Nonnull final Context context) {
        deleteNode(context, LocalAuth.getDbModule(), LocalAuth.getSecurityModule());
    }

    /**
     * Attempts to delete a node based on the post data.
     *
     * @param context        the REST request context to modify.
     * @param dbModule       the module instance to use.
     * @param securityModule the module instance to use.
     */
    public static void deleteNode(@Nonnull final Context context,
                                  @Nonnull final MongoDbModule dbModule,
                                  @Nonnull final SecurityModule securityModule) {
        ApiController.beforeApiAuthentication(context, dbModule, securityModule, PERMISSIONS);
        final String[] path = context.path().split("/");
        if (path.length != 5) {
            context.status(400);
            context.result("Bad Request");
            return;
        }
        final NodeDeleteRequest postRequest = new NodeDeleteRequest(URLDecoder.decode(path[4], StandardCharsets.UTF_8));
        if (!postRequest.deleteNode(dbModule)) {
            context.status(409);
            context.result("Conflict");
        } else {
            context.status(200);
            context.result("Success");
        }
    }
}
