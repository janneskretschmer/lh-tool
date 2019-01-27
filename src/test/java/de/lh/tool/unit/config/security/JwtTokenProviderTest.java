package de.lh.tool.unit.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import de.lh.tool.config.security.JwtTokenProvider;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;

public class JwtTokenProviderTest {
	private JwtTokenProvider provider;

	@BeforeEach
	public void before() {
		provider = new JwtTokenProvider();
		provider.setJwtSecret("secret");
		provider.setJwtExpirationInMs(1000000);
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
	}

	@Test
	public void testValidateTokenExpired() {
		provider.setJwtExpirationInMs(0);
		assertFalse(provider.validateToken(getTestToken()));
	}

	@Test
	public void testValidateTokenMalformed() {
		assertFalse(provider.validateToken(getTestToken().substring(10)));
	}

	public String getTestToken() {
		return provider.generateToken(new Authentication() {

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
				// immutable
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public Object getPrincipal() {
				return User.builder().id(123l).roles(
						Arrays.asList(new UserRole(null, null, "ROLE_TEST1"), new UserRole(null, null, "ROLE_TEST2")))
						.build();
			}

			@Override
			public Object getDetails() {
				return null;
			}

			@Override
			public Object getCredentials() {
				return null;
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
		});
	}

}
