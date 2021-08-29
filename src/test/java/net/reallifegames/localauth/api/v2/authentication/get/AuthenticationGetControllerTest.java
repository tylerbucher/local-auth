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
package net.reallifegames.localauth.api.v2.authentication.get;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.api.v1.ApiControllerTest;
import net.reallifegames.localauth.models.UserModel;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

public class AuthenticationGetControllerTest {

    private final Context ctx = Mockito.mock(Context.class);
    private final SecurityModule securityModule = Mockito.mock(SecurityModule.class);
    private final MongoDbModule dbModule = Mockito.mock(MongoDbModule.class);

    @Test
    public void GET_beforeApiAuthentication_Unauthorized() {
        Mockito.when(ctx.cookie("authToken")).thenReturn("");
        Mockito.when(securityModule.getJWSEmailClaim("")).thenReturn("");
        Mockito.when(securityModule.isJWSTokenValid("")).thenReturn(false);
        Mockito.when(dbModule.getUserModelByEmail("")).thenReturn(new UserModel("", "", true, false, Collections.emptyList()));
        try {
            AuthenticationGetController.getAuthentication(ctx, dbModule, securityModule);
        } catch (UnauthorizedResponse e) {
            ApiControllerTest.mockitoJsonStatus(ctx, 401);
        }
    }

    @Test
    public void GET_beforeApiAuthentication_Authorized() {
        final String email = "test@test.com";
        Mockito.when(ctx.cookie("authToken")).thenReturn("");
        Mockito.when(securityModule.getJWSEmailClaim("")).thenReturn(email);
        Mockito.when(securityModule.isJWSTokenValid("")).thenReturn(true);
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, "", true, false, Arrays.asList(0)));
        AuthenticationGetController.getAuthentication(ctx, dbModule, securityModule);

        ApiControllerTest.mockitoJsonStatusNot(ctx, 401);
    }

}