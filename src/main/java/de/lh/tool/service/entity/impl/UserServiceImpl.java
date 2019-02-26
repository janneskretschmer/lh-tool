package de.lh.tool.service.entity.impl;

import java.util.Calendar;

import javax.transaction.Transactional;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.PasswordChangeTokenService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.util.StringUtil;

@Service
public class UserServiceImpl extends BasicEntityServiceImpl<UserRepository, User, Long>
		implements UserService, UserDetailsService {

	@Autowired
	private PasswordChangeTokenService passwordChangeTokenService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return getRepository().findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not " + username + " does not exist"));
	}

	@Override
	@Transactional
	public UserDetails loadUserById(Long id) {
		return getRepository().findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
	}

	@Override
	@Transactional
	public User createUser(User user, String role) throws DefaultException {
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
		if (userRoleService.hasCurrentUserRightToGrantRole(role)) {
			userRoleService.save(new UserRole(null, user, role));
		}
		passwordChangeTokenService.saveRandomToken(user);
		return user;
	}

	@Override
	@Transactional
	public User updateUser(User user) throws DefaultException {
		if (user.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		User old = findById(user.getId()).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_USER_ID));
		if (getCurrentUser().getId() != user.getId()
				&& !((userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_CHANGE_FOREIGN))
						&& old.getRoles().stream()
								.anyMatch(r -> userRoleService.hasCurrentUserRightToGrantRole(r.getRole())))) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
		mapper.map(user, old);
		return save(old);
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

		User user = findById(userId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_USER_ID));

		if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_CHANGE_FOREIGN_PASSWORD)) {
			if (oldPassword == null) {
				validateToken(token, user);
			} else {
				try {
					authenticationManager
							.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), oldPassword));
				} catch (AuthenticationException e) {
					throw new DefaultException(ExceptionEnum.EX_PASSWORDS_INVALID_PASSWORD, e);
				}
			}
		}

		user.setPasswordHash(passwordEncoder.encode(newPassword));

		return save(user);
	}

	private void validateToken(String token, User user) throws DefaultException {
		if (token == null) {
			throw new DefaultException(ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD);
		}
		if (user.getPasswordChangeToken() == null
				|| !StringUtil.constantTimeEquals(token, user.getPasswordChangeToken().getToken())) {
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
	}

	@Override
	@Transactional
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			return getRepository().findByEmail(currentUserName).orElseThrow(
					() -> new UsernameNotFoundException("User not " + currentUserName + " does not exist"));
		}
		return null;
	}

	@Override
	public Iterable<User> findByProjectId(Long projectId) {
		return getRepository().findByProjects_Id(projectId);
	}

	@Override
	public Iterable<User> findByRoleIgnoreCase(String role) {
		return getRepository().findByRoles_RoleIgnoreCase(role);
	}

	@Override
	public Iterable<User> findByProjectIdAndRoleIgnoreCase(Long projectId, String role) {
		return getRepository().findByProjects_IdAndRoles_RoleIgnoreCase(projectId, role);
	}
}
