package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.PasswordChangeTokenService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordChangeTokenService passwordChangeTokenService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	public void before() {
		MockitoAnnotations.initMocks(userService);
	}

	@Test
	public void testCreateUserNoEmail() {
		try {
			userService.createUser(new User());
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_USER_NO_EMAIL, e.getException());
		}
	}

	@Test
	public void testCreateUserNoFirstName() {
		try {
			userService.createUser(User.builder().email("test@te.st").build());
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_USER_NO_FIRST_NAME, e.getException());
		}
	}

	@Test
	public void testCreateUserNoLastName() {
		try {
			userService.createUser(User.builder().email("test@te.st").firstName("Tes").build());
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_USER_NO_LAST_NAME, e.getException());
		}
	}

	@Test
	public void testCreateUserNoGender() {
		try {
			userService.createUser(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").build());
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_USER_NO_GENDER, e.getException());
		}
	}

	@Test
	public void testCreateUser() throws DefaultException {
		Mockito.when(userRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
		User user = userService.createUser(
				User.builder().email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build());
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
		Mockito.verify(passwordChangeTokenService, Mockito.times(1)).saveRandomToken(Mockito.eq(user));
	}

	@Test
	public void testChangePasswordNull() {
		try {
			userService.changePassword(null, null, null, null, null);
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_SHORT_PASSWORD, e.getException());
		}
	}

	@Test
	public void testChangePasswordShort() {
		try {
			userService.changePassword(null, null, null, "abcde", null);
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_SHORT_PASSWORD, e.getException());
		}
	}

	@Test
	public void testChangePasswordDifferent() {
		try {
			userService.changePassword(null, null, null, "abcdef", null);
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_DO_NOT_MATCH, e.getException());
		}
	}

	@Test
	public void testChangePasswordDifferent2() {
		try {
			userService.changePassword(null, null, null, "abcdef", "abcdeF");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_DO_NOT_MATCH, e.getException());
		}
	}

	@Test
	public void testChangePasswordNoUser() {
		try {
			userService.changePassword(null, null, null, "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_NO_USER_ID, e.getException());
		}
	}

	@Test
	public void testChangePasswordFalseUser() {
		Mockito.when(userRepository.findById(Mockito.eq(0l))).thenReturn(Optional.empty());
		try {
			userService.changePassword(0l, null, null, "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_USER_ID, e.getException());
		}
	}

	@Test
	public void testChangePasswordNoToken() {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional
				.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build()));
		try {
			userService.changePassword(1l, null, null, "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD, e.getException());
		}
	}

	@Test
	public void testChangePasswordUserHasNoToken() {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional
				.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build()));
		try {
			userService.changePassword(1l, "abc", null, "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN, e.getException());
		}
	}

	@Test
	public void testChangePasswordInvalidToken() {
		Mockito.when(userRepository.findById(Mockito.eq(1l)))
				.thenReturn(Optional.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter")
						.gender(Gender.MALE)
						.passwordChangeToken(
								PasswordChangeToken.builder().token("abcdef").updated(Calendar.getInstance()).build())
						.build()));
		try {
			userService.changePassword(1l, "abc", null, "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN, e.getException());
		}
	}

	@Test
	public void testChangePasswordExpiredToken() {
		Calendar updated = Calendar.getInstance();
		updated.add(Calendar.DAY_OF_YEAR, -PasswordChangeToken.TOKEN_VALIDITY_IN_DAYS - 1);
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().email("test@te.st")
				.firstName("Tes").lastName("Ter").gender(Gender.MALE)
				.passwordChangeToken(PasswordChangeToken.builder().token("abcdef").updated(updated).build()).build()));
		try {
			userService.changePassword(1l, "abcdef", null, "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_EXPIRED_TOKEN, e.getException());
		}
	}

	@Test
	public void testChangePasswordValidToken() throws DefaultException {
		PasswordChangeToken token = PasswordChangeToken.builder().token("abcdef").updated(Calendar.getInstance())
				.build();
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().email("test@te.st")
				.firstName("Tes").lastName("Ter").gender(Gender.MALE).passwordChangeToken(token).build()));
		Mockito.when(userRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
		Mockito.when(passwordEncoder.encode(Mockito.anyString())).then(i -> i.<String>getArgument(0).toUpperCase());
		User user = userService.changePassword(1l, "abcdef", null, "abcdef", "abcdef");
		Mockito.verify(passwordChangeTokenService, Mockito.times(1)).delete(Mockito.eq(token));
		assertNull(user.getPasswordChangeToken());
		assertEquals("ABCDEF", user.getPassword());
	}

	@Test
	public void testChangePasswordFalsePassword() {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().email("test@te.st")
				.firstName("Tes").lastName("Ter").gender(Gender.MALE).passwordHash("abc").build()));
		Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new AuthenticationException(null) {
			private static final long serialVersionUID = -5217148258702539075L;
		});
		try {
			userService.changePassword(1l, null, "abcdef", "abcdef", "abcdef");
			fail("exception expected");
		} catch (DefaultException e) {
			assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_PASSWORD, e.getException());
		}
	}

	@Test
	public void testChangePasswordOldPassword() throws DefaultException {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().email("test@te.st")
				.firstName("Tes").lastName("Ter").gender(Gender.MALE).passwordHash("abc").build()));
		Mockito.when(userRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
		Mockito.when(passwordEncoder.encode(Mockito.anyString())).then(i -> i.<String>getArgument(0).toUpperCase());
		Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
		User user = userService.changePassword(1l, null, "abc", "abcdef", "abcdef");
		assertEquals("ABCDEF", user.getPassword());
	}

}
