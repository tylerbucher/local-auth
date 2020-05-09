package net.reallifegames.localauth.api.v1.user;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

public class UserControllerTest {

	@BeforeClass
	public static void setUp() {
		LocalAuth.setDebugMode(true);
	}

	@Test
	public void GET_getUser_401_Unauthorized() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.pathParam(":username")).thenReturn("");
		Mockito.when(ctx.cookie("authToken")).thenReturn("");
		try {
			UserController.getUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(401);
	}

	@Test
	public void GET_getUser_500_InvalidRequest() {
		final Context ctx = Mockito.mock(Context.class);
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		Mockito.when(ctx.pathParam(":username")).thenReturn("test500");
		try {
			UserController.getUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(500);
	}

	@Test
	public void GET_getUser_200_Success() {
		final Context ctx = Mockito.mock(Context.class);
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		Mockito.when(ctx.pathParam(":username")).thenReturn("test");
		try {
			UserController.getUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(200);
	}
}