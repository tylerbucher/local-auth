package net.reallifegames.localauth.api.v1.editUser;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import net.reallifegames.localauth.api.v1.ApiController;
import net.reallifegames.localauth.api.v1.createUser.CreateUserController;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

public class EditUserControllerTest {

	@BeforeClass
	public static void setUp() {
		LocalAuth.setDebugMode(true);
	}

	@Test
	public void PATCH_patchUser_400_MarshallError() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("");
		try {
			EditUserController.patchUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(400);
	}

	@Test
	public void PATCH_patchUser_401_Unauthorized() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"updateUsername\":\"test\", \"admin\": true, \"active\": true}");
		Mockito.when(ctx.cookie("authToken")).thenReturn("");
		try {
			EditUserController.patchUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(401);
	}

	@Test
	public void PATCH_patchUser_200_Success() {
		final Context ctx = Mockito.mock(Context.class);
		Mockito.when(ctx.body()).thenReturn("{\"updateUsername\":\"test\", \"admin\": true, \"active\": true}");
		final String token = ApiController.getJWSToken("test", new Date(System.currentTimeMillis() + ApiController.DEFAULT_EXPIRE_TIME_EXT));
		Mockito.when(ctx.cookie("authToken")).thenReturn(token);
		try {
			EditUserController.patchUser(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(200);
	}
}