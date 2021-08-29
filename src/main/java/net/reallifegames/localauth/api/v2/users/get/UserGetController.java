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
package net.reallifegames.localauth.api.v2.users.get;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.Permissions;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.models.UserModel;

import javax.annotation.Nonnull;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Attempts to return the list of users in this application.
 *
 * @author Tyler Bucher
 */
public class UserGetController {

    /**
     * Default list of permissions for this endpoint.
     */
    private static final List<Integer> PERMISSIONS = Arrays.asList(
            Permissions.IS_USER_SUPER_ADMIN.value,
            Permissions.IS_USER_ADMIN.value,
            Permissions.CAN_USER_MOD_USERS.value,
            Permissions.CAN_USER_DELETE_USERS.value
    );

    /**
     * Returns a user based on the post data or a list of users.
     *
     * @param context the REST request context to modify.
     */
    public static void getUsers(@Nonnull final Context context) throws Exception {
        getUsers(context, LocalAuth.getDbModule(), LocalAuth.getSecurityModule());
    }

    /**
     * Returns a user based on the post data or a list of users.
     *
     * @param context        the REST request context to modify.
     * @param dbModule       the module instance to use.
     * @param securityModule the module instance to use.
     */
    public static void getUsers(@Nonnull final Context context,
                                @Nonnull final MongoDbModule dbModule,
                                @Nonnull final SecurityModule securityModule) throws Exception {
        final UserModel authUserModel = ApiController.beforeApiAuthentication(context, dbModule, securityModule, new ArrayList<>());
        final String[] path = context.path().split("/");
        if (authUserModel.hasPermission(PERMISSIONS) && path.length == 5) {
            final String pathVal = URLDecoder.decode(path[4], StandardCharsets.UTF_8);
            if(pathVal.equals("*")) {
                ApiController.jsonContextResponse(new UserGetRequest(convertUserModelList(dbModule.getAllUserModels())), context);
            } else {
                final UserModel userModel = dbModule.getUserModelByEmail(pathVal);
                if (userModel != null) {
                    ApiController.jsonContextResponse(new UserGetRequest(convertUserModelList(userModel)), context);
                } else {
                    context.status(400);
                    context.result("Bad Request");
                }
            }
        } else {
            ApiController.jsonContextResponse(new UserGetRequest(convertUserModelList(authUserModel)), context);
        }
    }

    /**
     * @param userModels the list to be converted.
     * @return the new converted SafeUserModel list.
     */
    private static List<SafeUserModel> convertUserModelList(@Nonnull final List<UserModel> userModels) {
        final List<SafeUserModel> safeUserModels = new ArrayList<>();
        userModels.forEach(userModel->safeUserModels.add(SafeUserModel.fromUserModel(userModel)));
        return safeUserModels;
    }

    /**
     * @param userModel the UserModel to be converted.
     * @return the new converted SafeUserModel.
     */
    @Nonnull
    private static List<SafeUserModel> convertUserModelList(@Nonnull final UserModel userModel) {
        return Collections.singletonList(SafeUserModel.fromUserModel(userModel));
    }
}
