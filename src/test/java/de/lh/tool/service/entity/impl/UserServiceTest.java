package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.User.Gender;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.UserRoleService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private UserRoleService userRoleService;

	@Mock
	private AuthenticationManager authenticationManager;

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
		UserDetails user = userService.findById(1l).get();
		Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.eq(1l));
		assertEquals("test@te.st", user.getUsername());
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
		assertEquals(ExceptionEnum.EX_NO_USER_ID, exception.getException());
	}

	@Test
	public void testChangePasswordFalseUser() {
		Mockito.when(userRepository.findById(Mockito.eq(0l))).thenReturn(Optional.empty());
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(0l, null, null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_INVALID_USER_ID, exception.getException());
	}

	@Test
	public void testChangePasswordNoToken() {
		mockCurrentUser();

		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().id(1l)
				.email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build()));
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, null, null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD, exception.getException());
	}

	@Test
	public void testChangePasswordUserHasNoToken() {
		mockCurrentUser();

		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().id(1l)
				.email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).build()));
		Mockito.when(userRoleService.hasCurrentUserRight(Mockito.eq(UserRole.RIGHT_USERS_CHANGE_FOREIGN_PASSWORD)))
				.thenReturn(false);
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, "abc", null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN, exception.getException());
	}

	@Test
	public void testChangePasswordInvalidToken() {
		mockCurrentUser();

		Mockito.when(userRepository.findById(Mockito.eq(1l)))
				.thenReturn(Optional.of(User.builder().id(1l).email("test@te.st").firstName("Tes").lastName("Ter")
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
		mockCurrentUser();

		Calendar updated = Calendar.getInstance();
		updated.add(Calendar.DAY_OF_YEAR, -PasswordChangeToken.TOKEN_VALIDITY_IN_DAYS - 1);
		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().id(1l)
				.email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE)
				.passwordChangeToken(PasswordChangeToken.builder().token("abcdef").updated(updated).build()).build()));
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, "abcdef", null, "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_EXPIRED_TOKEN, exception.getException());
	}

	@Test
	public void testChangePasswordFalsePassword() {
		mockCurrentUser();

		Mockito.when(userRepository.findById(Mockito.eq(1l))).thenReturn(Optional.of(User.builder().id(1l)
				.email("test@te.st").firstName("Tes").lastName("Ter").gender(Gender.MALE).passwordHash("abc").build()));
		Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new AuthenticationException(null) {
			private static final long serialVersionUID = -5217148258702539075L;
		});
		DefaultException exception = assertThrows(DefaultException.class,
				() -> userService.changePassword(1l, null, "abcdef", "abcdef", "abcdef"));
		assertEquals(ExceptionEnum.EX_PASSWORDS_INVALID_PASSWORD, exception.getException());
	}

	private void mockCurrentUser() {
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("test@te.st");
		Mockito.when(userRepository.findByEmail(Mockito.eq("test@te.st")))
				.thenReturn(Optional.of(User.builder().id(1l).build()));
	}

}
