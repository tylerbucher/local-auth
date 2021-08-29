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
package net.reallifegames.localauth;

import io.jsonwebtoken.security.Keys;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

public class SecurityModuleTest {

    public final String SECRET_KEY_STRING = "c1a6d28b8e2f2df1b0c73cc7f0fe7dfeb079eeb67e6781c83ae540e1cb357237";
    public static final byte[] SECRET_KEY = new byte[]{-63, -90, -46, -117, -114, 47, 45, -15, -80, -57, 60, -57, -16, -2, 125, -2, -80, 121, -18, -74, 126, 103, -127, -56, 58, -27, 64, -31, -53, 53, 114, 55};
    private final SecurityModule securityModule = new SecurityModule();
    private final Config config = Mockito.mock(Config.class);

    @Test
    public void base16ToInt_Correct() {
        for (int i = 0; i < 256; i++) {
            final String hexSet = String.format("%02X", i);
            final int b1 = SecurityModule.base16CharToInt(hexSet.charAt(0));
            final int b2 = SecurityModule.base16CharToInt(hexSet.charAt(1));
            Assert.assertEquals(i, ((long) b1 << 4) + b2);
        }
    }

    @Test
    public void hexStringToBytes_Correct() {
        Assert.assertArrayEquals(
                new byte[]{93, -73, 64, -125, -59, 120, -103, -31, 38, 17, 10, 108, 9, -69, 79, 28},
                SecurityModule.hexToBytes("5db74083c57899e126110a6c09bb4f1c")
        );
    }

    @Test (expected = StringIndexOutOfBoundsException.class)
    public void hexStringToBytes_Malformed() {
        SecurityModule.hexToBytes("5db74083c57899e126110a6c09bb4f1");
    }

    @Test (expected = NumberFormatException.class)
    public void hexStringToBytes_NonHexString() {
        SecurityModule.hexToBytes("This is a non hex string");
    }

    @Test
    public void isJWSTokenValid_Success() {
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.JIRU31zeEcRPWJM08TCFkiVCEEqGH7OCp--9ZuTrgmc";
        Mockito.when(config.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));
        Assert.assertTrue(securityModule.isJWSTokenValid(config, token));
    }

    @Test
    public void isJWSTokenValid_Expired() {
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjAsImVtYWlsIjoidGVzdEB0ZXN0LmNvbSJ9.qC6gj9HDFKrRGw0_xixzU6uH32QKmM9TkQSLgsyPO1g";
        Mockito.when(config.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));
        Assert.assertFalse(securityModule.isJWSTokenValid(config, token));
    }

    @Test
    public void isJWSTokenValid_Invalid() {
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjAsImVtYWlsIjoidGVzdEB0ZXN0LmNvbSJ9.3UdQlnY5KruTOqzJmCf5OpAoF2-RfcdZJ6n-FCT0TSQ";
        Mockito.when(config.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));
        Assert.assertFalse(securityModule.isJWSTokenValid(config, token));
    }

    @Test
    public void createToken_Success() {
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjAsImVtYWlsIjoidGVzdEB0ZXN0LmNvbSJ9.qC6gj9HDFKrRGw0_xixzU6uH32QKmM9TkQSLgsyPO1g";
        Mockito.when(config.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));
        Assert.assertEquals(token, securityModule.getJWSToken(config, "test@test.com", new Date(0)));
    }

    @Test
    public void getEmailFromToken_Success() {
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.JIRU31zeEcRPWJM08TCFkiVCEEqGH7OCp--9ZuTrgmc";
        Mockito.when(config.getSecretKey()).thenReturn(Keys.hmacShaKeyFor(SECRET_KEY));
        Assert.assertEquals("test@test.com", securityModule.getJWSEmailClaim(config, token));
    }
}