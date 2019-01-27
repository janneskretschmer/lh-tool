package de.lh.tool.unit.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.impl.UserServiceImpl;
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
	public void testLoadByUsername() {
		Mockito.when(userRepository.findByEmail(Mockito.eq("test@te.st")))
				.thenReturn(Optional.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").build()));
		UserDetails user = userService.loadUserByUsername("test@te.st");
		Mockito.verify(userRepository, Mockito.times(1)).findByEmail(Mockito.eq("test@te.st"));
		assertEquals("test@te.st", user.getUsername());
	}

	@Test
	public void testLoadByUsernameNull() {
		Mockito.when(userRepository.findByEmail(Mockito.isNull())).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(null));
		Mockito.verify(userRepository, Mockito.times(1)).findByEmail(Mockito.isNull());
	}

	@Test
	public void testLoadByUsernameFalseUsername() {
		Mockito.when(userRepository.findByEmail(Mockito.eq("test@te.s"))).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("test@te.s"));
		Mockito.verify(userRepository, Mockito.times(1)).findByEmail(Mockito.eq("test@te.s"));
	}

	@Test
	public void testLoadById() {
		Mockito.when(userRepository.findById(Mockito.eq(1l)))
				.thenReturn(Optional.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").build()));
		UserDetails user = userService.loadUserById(1l);
		Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.eq(1l));
		assertEquals("test@te.st", user.getUsername());
	}

	@Test
	public void testLoadByIdNull() {
		Mockito.when(userRepository.findById(Mockito.isNull())).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserById(null));
		Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.isNull());
	}

	@Test
	public void testLoadByIdFalseId() {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserById(1l));
		Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.eq(1l));
	}

	@Test
	public void testCreateUserNoEmail() {
		DefaultException exception = assertThrows(DefaultException.class, () -> userService.createUser(new User()));
		assertEquals(ExceptionEnum.EX_USER_NO_EMAIL, exception.getException());
	}

	@Test
	public void testCreateUserNoFirstName() {
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.createUser(User.builder().email("test@te.st").build()));
		assertEquals(ExceptionEnum.EX_USER_NO_FIRST_NAME, exception.getException());
	}

	@Test
	public void testCreateUserNoLastName() {
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.createUser(User.builder().email("test@te.st").firstName("Tes").build()));
		assertEquals(ExceptionEnum.EX_USER_NO_LAST_NAME, exception.getException());
	}

	@Test
	public void testCreateUserNoGender() {
		DefaultException exception = assertThrows(DefaultException.class, () -> userService
				.createUser(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").build()));
		assertEquals(ExceptionEnum.EX_USER_NO_GENDER, exception.getException());
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
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(null, null, null, null, null));
		assertEquals(ExceptionEnum.EX_PASSWORDS_SHORT_PASSWORD, exception.getException());
	}

	@Test
	public void testChangePasswordShort() {
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(null, null, null, "abcde", null));
		assertEquals(ExceptionEnum.EX_PASSWORDS_SHORT_PASSWORD, exception.getException());
	}

	@Test
	public void testChangePasswordDifferent() {
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(null, null, null, "abcdef", null));
		assertEquals(ExceptionEnum.EX_PASSWORDS_DO_NOT_MATCH, exception.getException());
	}

	@Test
	public void testChangePasswordDifferent2() {
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(null, null, null, "abcdef", "abcdeF"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_DO_NOT_MATCH, exception.getException());
	}

	@Test
	public void testChangePasswordNoUser() {
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(null, null, null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_NO_USER_ID, exception.getException());
	}

	@Test
	public void testChangePasswordFalseUser() {
		Mockito.when(userRepository.findById(Mockito.eq(0l))).thenReturn(Optional.empty());
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(0l, null, null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_USER_ID, exception.getException());
	}

	@Test
	public void testChangePasswordNoToken() {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional
				.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build()));
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, null, null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD, exception.getException());
	}

	@Test
	public void testChangePasswordUserHasNoToken() {
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional
				.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build()));
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, "abc", null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN, exception.getException());
	}

	@Test
	public void testChangePasswordInvalidToken() {
		Mockito.when(userRepository.findById(Mockito.eq(1l)))
				.thenReturn(Optional.of(User.builder().email("test@te.st").firstName("Tes").lastName("Ter")
						.gender(Gender.MALE)
						.passwordChangeToken(
								PasswordChangeToken.builder().token("abcdef").updated(Calendar.getInstance()).build())
						.build()));
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, "abc", null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN, exception.getException());
	}

	@Test
	public void testChangePasswordExpiredToken() {
		Calendar updated = Calendar.getInstance();
		updated.add(Calendar.DAY_OF_YEAR, -PasswordChangeToken.TOKEN_VALIDITY_IN_DAYS - 1);
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().email("test@te.st")
				.firstName("Tes").lastName("Ter").gender(Gender.MALE)
				.passwordChangeToken(PasswordChangeToken.builder().token("abcdef").updated(updated).build()).build()));
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, "abcdef", null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_EXPIRED_TOKEN, exception.getException());
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
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, null, "abcdef", "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_PASSWORD, exception.getException());
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
