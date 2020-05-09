package net.reallifegames.localauth.api.v1.dash;

import io.javalin.http.Context;
import net.reallifegames.localauth.LocalAuth;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class DashControllerTest {

	@BeforeClass
	public static void setUp() {
		LocalAuth.setDebugMode(true);
	}

	@Test
	public void GET_getEndpoints_200_Success() {
		final Context ctx = Mockito.mock(Context.class);
		try {
			DashController.getEndpoints(ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(ctx).status(200);
	}
}