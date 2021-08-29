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
package net.reallifegames.localauth.api.v2.users.patch;

import io.javalin.http.Context;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.Permissions;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.models.UserModel;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

public class UserPatchControllerTest {

    private final Context ctx = Mockito.mock(Context.class);
    private final MongoDbModule dbModule = Mockito.mock(MongoDbModule.class);
    private final SecurityModule securityModule = Mockito.mock(SecurityModule.class);

    @Test
    public void PATCH_patchUser_400() {
        final String email = "test@test.com";
        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(0)));
        Mockito.when(ctx.body()).thenReturn("");
        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(400);
    }

    @Test
    public void PATCH_patchUser_409() {
        final String email = "test@test.com";
        final String password = "123456";
        final boolean active = true;

        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(Permissions.CAN_USER_MOD_USERS.value)));
        Mockito.when(ctx.body()).thenReturn("{\"email\": \"" + email + "\", \"password\":\"" + password + "\", \"active\":" + active + ", \"permissions\": [0], \"nodeMetaData\": \"\"}");
        Mockito.when(dbModule.updateUserModel(email, password, active, null)).thenReturn(false);

        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(409);
    }

    @Test
    public void PATCH_patchUser_200() {
        final String email = "test@test.com";
        final String password = "123456";
        final boolean active = true;

        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(Permissions.CAN_USER_MOD_USERS.value)));
        Mockito.when(ctx.body()).thenReturn("{\"email\": \"" + email + "\", \"password\":\"" + password + "\", \"active\":" + active + ", \"permissions\": [0], \"nodeMetaData\": \"\"}");
        Mockito.when(dbModule.updateUserModel(email, password, active, null)).thenReturn(true);

        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(200);
    }

    @Test
    public void PATCH_patchUser_lite_200() {
        final String email = "test@test.com";
        final String password = "123456";
        final boolean active = true;

        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, new ArrayList<>()));
        Mockito.when(ctx.body()).thenReturn("{\"email\": \"" + email + "\", \"password\":\"" + password + "\", \"active\":" + active + ", \"permissions\": [0], \"nodeMetaData\": \"\"}");
        Mockito.when(dbModule.updateUserModel(email, password, active, null)).thenReturn(true);

        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(200);
    }

    @Test
    public void PATCH_patchUser_metadata_401() {
        final String email = "test@test.com";
        final String password = "123456";
        final boolean active = true;

        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(Permissions.CAN_USER_MOD_USERS.value)));
        Mockito.when(ctx.body()).thenReturn("{\"email\": \"test2@test.com\", \"password\":\"\", \"active\":false, \"permissions\": [], \"nodeMetaData\": \"test\"}");
        Mockito.when(dbModule.updateUserModel(email, password, active, null)).thenReturn(true);
        Mockito.when(dbModule.updateUserNodeMetadata(email, "test")).thenReturn(false);

        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(401);
    }

    @Test
    public void PATCH_patchUser_metadata_409() {
        final String email = "test@test.com";
        final String password = "123456";
        final boolean active = true;

        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(Permissions.CAN_USER_MOD_USERS.value)));
        Mockito.when(ctx.body()).thenReturn("{\"email\": \"" + email + "\", \"password\":\"\", \"active\":false, \"permissions\": [], \"nodeMetaData\": \"test\"}");
        Mockito.when(dbModule.updateUserModel(email, password, active, null)).thenReturn(true);
        Mockito.when(dbModule.updateUserNodeMetadata(email, "test")).thenReturn(false);

        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(409);
    }

    @Test
    public void PATCH_patchUser_metadata_200() {
        final String email = "test@test.com";
        final String password = "123456";
        final boolean active = true;

        Mockito.when(securityModule.getJWSEmailClaim(ctx.cookie("authToken"))).thenReturn(email);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(Permissions.CAN_USER_MOD_USERS.value)));
        Mockito.when(ctx.body()).thenReturn("{\"email\": \"" + email + "\", \"password\":\"\", \"active\":false, \"permissions\": [], \"nodeMetaData\": \"test\"}");
        Mockito.when(dbModule.updateUserModel(email, password, active, null)).thenReturn(true);
        Mockito.when(dbModule.updateUserNodeMetadata(email, "test")).thenReturn(true);

        UserPatchController.patchUser(ctx, dbModule, securityModule);
        Mockito.verify(ctx).status(200);
    }
}