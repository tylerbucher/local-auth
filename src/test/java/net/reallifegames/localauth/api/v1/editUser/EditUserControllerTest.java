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

import io.javalin.http.Context;
import net.reallifegames.localauth.DbModule;
import net.reallifegames.localauth.SecurityDbModule;
import net.reallifegames.localauth.api.v1.ApiController;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

public class EditUserControllerTest {

    private final Context ctx = Mockito.mock(Context.class);
    private final SecurityDbModule securityModule = Mockito.mock(SecurityDbModule.class);
    private final DbModule dbModule = Mockito.mock(DbModule.class);

    @Test
    public void PATCH_patchUser_400_MarshallError() {
        Mockito.when(ctx.body()).thenReturn("");
        try {
            EditUserController.patchUser(ctx, securityModule, dbModule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).status(400);
    }

    @Test
    public void PATCH_patchUser_403_Unauthorized() {
        Mockito.when(ctx.body()).thenReturn("{\"updateUsername\":\"test\", \"admin\": true, \"active\": true}");
        Mockito.when(ctx.cookie("authToken")).thenReturn("");
        Mockito.when(securityModule.isUserAdmin("test", dbModule)).thenReturn(false);
        try {
            EditUserController.patchUser(ctx, securityModule, dbModule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).status(403);
    }

    @Test
    public void PATCH_patchUser_500_Internal() {
        Mockito.when(ctx.body()).thenReturn("{\"updateUsername\":\"test\", \"admin\": true, \"active\": true}");
        Mockito.when(ctx.cookie("authToken")).thenReturn("");
        Mockito.when(securityModule.getJWSUsernameClaim("")).thenReturn("test");
        Mockito.when(securityModule.isUserAdmin("test")).thenReturn(true);
        Mockito.when(dbModule.updateUser(true, true, "test")).thenReturn(false);
        try {
            EditUserController.patchUser(ctx, securityModule, dbModule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).status(500);
    }

    @Test
    public void PATCH_patchUser_200_Success() {
        Mockito.when(ctx.body()).thenReturn("{\"updateUsername\":\"test\", \"admin\": true, \"active\": true}");
        Mockito.when(ctx.cookie("authToken")).thenReturn("");
        Mockito.when(securityModule.getJWSUsernameClaim("")).thenReturn("test");
        Mockito.when(securityModule.isUserAdmin("test")).thenReturn(true);
        Mockito.when(dbModule.updateUser(true, true, "test")).thenReturn(true);
        try {
            EditUserController.patchUser(ctx, securityModule, dbModule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).status(200);
    }
}