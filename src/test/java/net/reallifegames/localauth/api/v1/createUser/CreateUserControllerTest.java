package net.reallifegames.localauth.api.v1.createUser;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

public class CreateUserControllerTest {

	@BeforeClass
	public static void setUp() {
		LocalAuth.setDebugMode(true);
	}

	@Test
	public void POST_postNewUser_400_MarshallError() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("");
		try {
			CreateUserController.postNewUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(400);
	}

	@Test
	public void POST_postNewUser_401_Unauthorized() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"username\":\"test\", \"password\": \"test\"}");
		Mockito.when(ctx.cookie("authToken")).thenReturn("");
		try {
			CreateUserController.postNewUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(401);
	}

	@Test
	public void POST_postNewUser_409_DataConflict() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"username\":\"test\", \"password\": \"test\"}");
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		try {
			CreateUserController.postNewUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(409);
	}

	@Test
	public void POST_postNewUser_500_DataConflict() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"username\":\"test500\", \"password\": \"test\"}");
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		try {
			CreateUserController.postNewUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(500);
	}

	@Test
	public void POST_postNewUser_200_Success() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"username\":\"test200\", \"password\": \"test\"}");
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		try {
			CreateUserController.postNewUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(200);
	}
}