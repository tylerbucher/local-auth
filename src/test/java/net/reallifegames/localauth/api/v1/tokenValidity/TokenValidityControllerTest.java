package net.reallifegames.localauth.api.v1.tokenValidity;

import io.javalin.http.Context;
import org.junit.Test;
import org.mockito.Mockito;

public class TokenValidityControllerTest {

	@Test
	public void GET_getTokenValidity_200_TokenIsValid() {
		final Context ctx = Mockito.mock(Context.class);
		TokenValidityController.getTokenValidity(ctx);
		Mockito.verify(ctx).status(200);
	}
}