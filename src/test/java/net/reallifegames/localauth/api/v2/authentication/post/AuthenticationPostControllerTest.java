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
package net.reallifegames.localauth.api.v2.authentication.post;

import io.javalin.http.Context;
import net.reallifegames.localauth.Config;
import net.reallifegames.localauth.MongoDbModule;
import net.reallifegames.localauth.SecurityModule;
import net.reallifegames.localauth.models.UserModel;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.ArrayList;

public class AuthenticationPostControllerTest {

    private final Context ctx = Mockito.mock(Context.class);
    private final SecurityModule securityModule = Mockito.mock(SecurityModule.class);
    private final MongoDbModule dbModule = Mockito.mock(MongoDbModule.class);
    private final Config config = Mockito.mock(Config.class);

    @Test
    public void POST_postAuthentication_400() {
        Mockito.when(ctx.body()).thenReturn("");
        AuthenticationPostController.postAuthentication(ctx, dbModule, securityModule, config);
        Mockito.verify(ctx).status(400);
    }

    @Test
    public void POST_postAuthentication_406_nullUser() {
        final String email = "test@test.com";
        Mockito.when(ctx.body()).thenReturn("{\"email\":\"" + email + "\", \"password\":\"123456\", \"rememberMe\": false}");
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(null);
        AuthenticationPostController.postAuthentication(ctx, dbModule, securityModule, config);
        Mockito.verify(ctx).status(406);
    }

    @Test
    public void POST_postAuthentication_406() {
        final String email = "test@test.com";
        final String pass = "123";
        final String passHash = BCrypt.hashpw(pass, BCrypt.gensalt());
        Mockito.when(ctx.body()).thenReturn("{\"email\":\"" + email + "\", \"password\":\"123456\", \"rememberMe\": false}");
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, passHash, false, false, new ArrayList<>()));
        AuthenticationPostController.postAuthentication(ctx, dbModule, securityModule, config);
        Mockito.verify(ctx).status(406);
    }

    @Test
    public void POST_postAuthentication_409() {
        final String email = "test@test.com";
        final String pass = "123456";
        final String passHash = BCrypt.hashpw(pass, BCrypt.gensalt());
        Mockito.when(ctx.body()).thenReturn("{\"email\":\"" + email + "\", \"password\":\"123456\", \"rememberMe\": false}");
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, passHash, false, false, new ArrayList<>()));
        AuthenticationPostController.postAuthentication(ctx, dbModule, securityModule, config);
        Mockito.verify(ctx).status(409);
    }

    @Test
    public void POST_postAuthentication_200() {
        final String email = "test@test.com";
        final String pass = "123456";
        final String passHash = BCrypt.hashpw(pass, BCrypt.gensalt());
        Mockito.when(ctx.body()).thenReturn("{\"email\":\"" + email + "\", \"password\":\"" + pass + "\", \"rememberMe\": false}");
        Mockito.when(dbModule.getUserModelByEmail(email)).thenReturn(new UserModel(email, passHash, true, false, new ArrayList<>()));
        Mockito.when(config.getJwtExpireTime()).thenReturn(604800000L);
        Mockito.when(config.getDomain()).thenReturn("localhost");
        Mockito.when(securityModule.getJWSToken(email, new Date(System.currentTimeMillis() + config.getJwtExpireTime()))).thenReturn("");

        AuthenticationPostController.postAuthentication(ctx, dbModule, securityModule, config);
        Mockito.verify(ctx).status(200);
    }
}