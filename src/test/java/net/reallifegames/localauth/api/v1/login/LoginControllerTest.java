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
package net.reallifegames.localauth.api.v1.login;

import io.javalin.http.Context;
import net.reallifegames.localauth.DbModule;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class LoginControllerTest {

    @Test
    public void POST_postLoginUser_400_InvalidRequest() {
        final Context ctx = Mockito.mock(Context.class);
        Mockito.when(ctx.body()).thenReturn("");
        try {
            LoginController.postLoginUser(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).status(400);
    }

    @Test
    public void POST_postLoginUser_409_LoginError() {
        final Context ctx = Mockito.mock(Context.class);
        final DbModule dbModule = Mockito.mock(DbModule.class);
        Mockito.when(ctx.body()).thenReturn("{\"username\":\"\", \"password\": \"\"}");
        Mockito.when(dbModule.userExists("")).thenReturn(false);
        Mockito.when(dbModule.isLoginInfoValid("", "")).thenReturn(false);
        try {
            LoginController.postLoginUser(ctx, dbModule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).status(409);
    }

    @Test
    public void POST_postLoginUser_200_LoggedIn() {
        final Context ctx = Mockito.mock(Context.class);
        final DbModule dbModule = Mockito.mock(DbModule.class);
        Mockito.when(ctx.body()).thenReturn("{\"username\":\"test\", \"password\": \"test\"}");
        Mockito.when(dbModule.userExists("test")).thenReturn(true);
        Mockito.when(dbModule.isLoginInfoValid("test", "test")).thenReturn(true);
        try {
            LoginController.postLoginUser(ctx, dbModule);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito.verify(ctx).contentType("application/json");
        Mockito.verify(ctx).status(200);
    }
}