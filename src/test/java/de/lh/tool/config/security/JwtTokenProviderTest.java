package de.lh.tool.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

public class JwtTokenProviderTest {
	private JwtTokenProvider provider;

	private Log log;

	@BeforeEach
	public void before() throws NoSuchFieldException, SecurityException, Exception {
		provider = new JwtTokenProvider();
		provider.setJwtSecret("secret");
		provider.setJwtExpirationInMs(1000000);

		log = Mockito.mock(Log.class);
		setFinalStatic(provider.getClass().getDeclaredField("log"), log);
	}

	@Test
	public void testGenerateToken() {
		String[] splitToken = getTestToken().split("\\.");
		assertEquals("{\"alg\":\"HS512\"}", new String(Base64.getDecoder().decode(splitToken[0])));
		assertTrue(new String(Base64.getDecoder().decode(splitToken[1]))
				.startsWith("{\"sub\":\"123\",\"permissions\":[\"ROLE_TEST1\",\"ROLE_TEST2\"],"));
	}

	@Test
	public void testGetUserIdFromJwt() {
		assertEquals(Long.valueOf(123), provider.getUserIdFromJWT(getTestToken()));
	}

	@Test
	public void testValidateToken() {
		assertTrue(provider.validateToken(getTestToken()));
	}

	@Test
	public void testValidateTokenEmpty() {
		assertFalse(provider.validateToken(null));
		Mockito.verify(log, Mockito.times(1)).error(Mockito.any(), Mockito.any(IllegalArgumentException.class));
	}

	@Test
	public void testValidateTokenExpired() {
		provider.setJwtExpirationInMs(0);
		assertFalse(provider.validateToken(getTestToken()));
		Mockito.verify(log, Mockito.times(1)).error(Mockito.any(), Mockito.any(ExpiredJwtException.class));
	}

	@Test
	public void testValidateTokenMalformed() {
		assertFalse(provider.validateToken(getTestToken().substring(10)));
		Mockito.verify(log, Mockito.times(1)).error(Mockito.any(), Mockito.any(MalformedJwtException.class));
	}

	public String getTestToken() {
		return provider.generateToken(User.builder().id(123l)
				.roles(Arrays.asList(new UserRole(null, null, "ROLE_TEST1"), new UserRole(null, null, "ROLE_TEST2")))
				.build());
	}

	static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, newValue);
	}

}
