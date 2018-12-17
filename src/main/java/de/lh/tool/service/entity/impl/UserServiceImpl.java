package de.lh.tool.service.entity.impl;

import java.util.Calendar;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.PasswordChangeTokenService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class UserServiceImpl extends BasicEntityServiceImpl<UserRepository, User, Long>
		implements UserService, UserDetailsService {

	@Autowired
	private PasswordChangeTokenService passwordChangeTokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User orElseThrow = getRepository().findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not " + username + " does not exist"));
		orElseThrow.getAuthorities();
		return orElseThrow;
	}

	@Override
	@Transactional
	public UserDetails loadUserById(Long id) {
		return getRepository().findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
	}

	@Override
	@Transactional
	public User createUser(User user) throws DefaultException {
		if (user.getEmail() == null) {
			throw new DefaultException(ExceptionEnum.EX_USER_NO_EMAIL);
		}
		if (user.getFirstName() == null) {
			throw new DefaultException(ExceptionEnum.EX_USER_NO_FIRST_NAME);
		}
		if (user.getLastName() == null) {
			throw new DefaultException(ExceptionEnum.EX_USER_NO_LAST_NAME);
		}
		if (user.getGender() == null) {
			throw new DefaultException(ExceptionEnum.EX_USER_NO_GENDER);
		}
		user = save(user);
		passwordChangeTokenService.saveRandomToken(user);
		return user;
	}

	@Override
	@Transactional
	public User changePassword(Long userId, String token, String oldPassword, String newPassword,
			String confirmPassword) throws DefaultException {
		if (newPassword == null || newPassword.length() < PasswordChangeToken.MIN_PASSWORD_LENGTH) {
			throw new DefaultException(ExceptionEnum.EX_PASSWORDS_SHORT_PASSWORD);
		}
		if (!newPassword.equals(confirmPassword)) {
			throw new DefaultException(ExceptionEnum.EX_PASSWORDS_DO_NOT_MATCH);
		}
		if (userId == null) {
			throw new DefaultException(ExceptionEnum.EX_PASSWORDS_NO_USER_ID);
		}

		User user = findById(userId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_PASSWORDS_INVALID_USER_ID));

		if (oldPassword == null) {
			if (token == null) {
				throw new DefaultException(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD);
			}
			if (user.getPasswordChangeToken() == null || !token.equals(user.getPasswordChangeToken().getToken())) {
				throw new DefaultException(ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN);
			}
			user.getPasswordChangeToken().getUpdated().setLenient(true);
			user.getPasswordChangeToken().getUpdated().add(Calendar.DAY_OF_YEAR,
					PasswordChangeToken.TOKEN_VALIDITY_IN_DAYS);
			if (Calendar.getInstance().after(user.getPasswordChangeToken().getUpdated())) {
				throw new DefaultException(ExceptionEnum.EX_PASSWORDS_EXPIRED_TOKEN);
			}
			passwordChangeTokenService.delete(user.getPasswordChangeToken());
			user.setPasswordChangeToken(null);
		} else {
			try {
				authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), oldPassword));
			} catch (AuthenticationException e) {
				throw new DefaultException(ExceptionEnum.EX_PASSWORDS_INVALID_PASSWORD, e);
			}
		}

		user.setPasswordHash(passwordEncoder.encode(newPassword));

		return save(user);
	}
}
