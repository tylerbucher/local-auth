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