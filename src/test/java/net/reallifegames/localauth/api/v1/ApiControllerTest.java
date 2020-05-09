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
package net.reallifegames.localauth.api.v1;

import io.javalin.http.Context;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

public class ApiControllerTest {

	@Test
	public void GET_beforeApiAuthentication_Unauthorized() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.cookie("authToken")).thenReturn("");
		ApiController.beforeApiAuthentication(ctx);

		Mockito.verify(ctx).contentType("application/json");
		Mockito.verify(ctx).status(401);
	}

	@Test
	public void GET_beforeApiAuthentication_Authorized() {
		final Context ctx = Mockito.mock(Context.class);
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		ApiController.beforeApiAuthentication(ctx);

		Mockito.verify(ctx).contentType("application/json");
		Mockito.verify(ctx, Mockito.never()).status(401);
	}

	@Test
	public void GET_beforeApiAuthentication_Unauthorized_TokenExpire() {
		final Context ctx = Mockito.mock(Context.class);
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() - 1));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		ApiController.beforeApiAuthentication(ctx);

		Mockito.verify(ctx).contentType("application/json");
		Mockito.verify(ctx).status(401);
	}

	@Test
	public void GET_getApiInformation_200_ApiResponse() {
		final Context ctx = Mockito.mock(Context.class);
		try {
			ApiController.getApiInformation(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).contentType("application/json");
		Mockito.verify(ctx).status(200);
	}
}