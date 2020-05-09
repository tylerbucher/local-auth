package net.reallifegames.localauth.api.v1.login;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class LoginControllerTest {

	@BeforeClass
	public static void setUp() {
		LocalAuth.setDebugMode(true);
	}

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
		Mockito.when(ctx.body()).thenReturn("{\"username\":\"\", \"password\": \"\"}");
		try {
			LoginController.postLoginUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(409);
	}

	@Test
	public void POST_postLoginUser_200_LoggedIn() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"username\":\"test\", \"password\": \"test\"}");
		try {
			LoginController.postLoginUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).contentType("application/json");
		Mockito.verify(ctx).status(200);
	}
}