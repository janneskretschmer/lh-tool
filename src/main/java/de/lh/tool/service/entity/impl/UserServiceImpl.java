package de.lh.tool.service.entity.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
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

import de.lh.tool.config.security.JwtTokenProvider;
import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.MailService;
import de.lh.tool.service.entity.interfaces.PasswordChangeTokenService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.ProjectUserService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.UserCrudService;
import de.lh.tool.util.StringUtil;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl extends BasicEntityCrudServiceImpl<UserRepository, User, UserDto, Long>
		implements UserService, UserDetailsService, UserCrudService {

	@Autowired
	private PasswordChangeTokenService passwordChangeTokenService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectUserService projectUserService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_USER_ID;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findByEmail(username);
	}

	@Override
	@Transactional
	public UserDetails findUserDetailsById(Long id) {
		return getRepository().findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
	}

	@Transactional
	private User findByEmail(String email) throws UsernameNotFoundException {
		return getRepository().findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist"));
	}

	@Override
	protected void checkValidity(@NonNull User user) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_EMAIL, user.getEmail());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_FIRST_NAME, user.getFirstName());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_GENDER, user.getGender());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_LAST_NAME, user.getLastName());
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_USER_EMAIL_ALREADY_IN_USE,
				getRepository().findByEmail(user.getEmail()), user);
	}

	@Override
	protected void postCreate(@NonNull User user) {
		PasswordChangeToken token = passwordChangeTokenService.saveRandomToken(user);
		mailService.sendUserCreatedMail(user, token);
	}

	@Override
	protected void preUpdate(@NonNull User oldUser, @NonNull User newUser) {
		// necessary bc password hash shouldn't get exposed over REST
		// possible alternative: use one mapping without hash for REST-stuff and extend
		// it with one bean definition for password validation and so on
		newUser.setPasswordHash(oldUser.getPasswordHash());
	}

	@Override
	@Transactional
	public UserDto changePassword(Long userId, String token, String oldPassword, String newPassword,
			String confirmPassword) throws DefaultException {
		if (newPassword == null || newPassword.length() < PasswordChangeToken.MIN_PASSWORD_LENGTH) {
			throw ExceptionEnum.EX_PASSWORDS_SHORT_PASSWORD.createDefaultException();
		}
		if (!newPassword.equals(confirmPassword)) {
			throw ExceptionEnum.EX_PASSWORDS_DO_NOT_MATCH.createDefaultException();
		}
		if (userId == null) {
			throw ExceptionEnum.EX_NO_USER_ID.createDefaultException();
		}

		User user = findById(userId).orElseThrow(ExceptionEnum.EX_INVALID_USER_ID::createDefaultException);

		if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_CHANGE_FOREIGN_PASSWORD)) {
			// check might be senseless, because sb. who knows somebody's password could
			// just sign in and change the password...
			Long currentUserId = Optional.ofNullable(getCurrentUser()).map(User::getId).orElse(null);
			if (currentUserId != null
					&& !Optional.ofNullable(user.getId()).map(id -> id.equals(currentUserId)).orElse(false)) {
				throw ExceptionEnum.EX_INVALID_USER_ID.createDefaultException();
			}

			if (oldPassword == null) {
				validateToken(token, user);
			} else {
				try {
					authenticationManager
							.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), oldPassword));
				} catch (AuthenticationException e) {
					throw ExceptionEnum.EX_PASSWORDS_INVALID_PASSWORD.createDefaultException(e);
				}
			}
		}

		user.setPasswordHash(passwordEncoder.encode(newPassword));

		return convertToDto(save(user));
	}

	private void validateToken(String token, User user) throws DefaultException {
		if (token == null) {
			throw ExceptionEnum.EX_PASSWORDS_NO_TOKEN_OR_OLD_PASSWORD.createDefaultException();
		}
		if (user.getPasswordChangeToken() == null
				|| !StringUtil.constantTimeEquals(token, user.getPasswordChangeToken().getToken())) {
			throw ExceptionEnum.EX_PASSWORDS_INVALID_TOKEN.createDefaultException();
		}
		user.getPasswordChangeToken().getUpdated().setLenient(true);
		user.getPasswordChangeToken().getUpdated().add(Calendar.DAY_OF_YEAR,
				PasswordChangeToken.TOKEN_VALIDITY_IN_DAYS);
		if (Calendar.getInstance().after(user.getPasswordChangeToken().getUpdated())) {
			throw ExceptionEnum.EX_PASSWORDS_EXPIRED_TOKEN.createDefaultException();
		}
		passwordChangeTokenService.delete(user.getPasswordChangeToken());
		user.setPasswordChangeToken(null);
	}

	@Override
	public UserDto findCurrentUserDto() throws DefaultException {
		checkFindRight();
		User user = getCurrentUser();
		hasReadPermission(user);
		return convertToDto(user);
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
	public boolean isCurrentUser(User user) {
		return Optional.ofNullable(getCurrentUser()).map(User::getId)
				.map(currentUserId -> currentUserId.equals(Optional.ofNullable(user).map(User::getId).orElse(null)))
				.orElse(Boolean.FALSE);
	}

	@Override
	public List<User> findByProjectIdAndRoleIgnoreCase(Long projectId, String role) {
		return getRepository().findByProjectIdAndRoleAndFreeTextIgnoreCase(projectId, role, null);
	}

	@Override
	@Transactional
	public List<UserDto> findDtosByProjectIdAndRoleIgnoreCase(Long projectId, String role, String freeText)
			throws DefaultException {
		checkFindRight();
		if (projectId != null) {
			projectService.checkReadPermission(projectId);
		}

		List<User> users = getRepository().findByProjectIdAndRoleAndFreeTextIgnoreCase(projectId,
				StringUtils.trimToNull(role), StringUtils.trimToNull(freeText));
		return convertToDtoList(filterFindResult(users));
	}

	@Override
	@Transactional
	public void requestPasswordReset(String email) throws DefaultException {
		try {
			User user = findByEmail(email);
			if (user != null) {
				PasswordChangeToken token = passwordChangeTokenService.saveRandomToken(user);

				mailService.sendPwResetMail(user, token);
			}

		} catch (UsernameNotFoundException ex) {
			// pass
		}
	}

	@Override
	@Transactional
	public JwtAuthenticationDto login(LoginDto loginDto) {
		// admin can login as any user
		if (userRoleService.hasCurrentUserRight(UserRole.ROLE_ADMIN)) {
			log.info(getCurrentUser().getEmail() + " logged himself in as " + loginDto.getEmail());
			User user = findByEmail(loginDto.getEmail());
			String jwt = tokenProvider.generateToken(user);
			return new JwtAuthenticationDto(jwt);
		}

		// default login
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken((User) authentication.getPrincipal());
		return new JwtAuthenticationDto(jwt);
	}

	@Override
	protected void checkDeletable(@NonNull User user) throws DefaultException {
		if (getCurrentUser().equals(user)) {
			throw ExceptionEnum.EX_USER_SUICIDE.createDefaultException();
		}
	}

	@Override
	public boolean hasReadPermission(@NonNull User user) {

		boolean hasPermission = isCurrentUser(user);

		if (userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_CHANGE_FOREIGN)) {

			boolean hasPermissionToGrantAllRoles = hasPermission
					|| userRoleService.hasCurrentUserRightToGrantAllRoles(user);

			hasPermission = hasPermission
					// permission through rights
					|| (userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_CHANGE_FOREIGN)
							&& (hasPermissionToGrantAllRoles));

			hasPermission = hasPermission
					// permission through project
					|| (hasPermissionToGrantAllRoles && Optional
							.ofNullable(user.getId()).map(projectUserService::findByUserId)
							.filter(projects -> !projects.isEmpty()).map(Collection::stream).map(stream -> stream
									.map(ProjectUser::getProject).anyMatch(projectService::hasReadPermission))
							.orElse(false));
		}

		return hasPermission;
	}

	@Override
	public boolean hasWritePermission(@NonNull User user) {
		return (user.getId() == null && userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_POST))
				|| hasReadPermission(user);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.USERS_PREFIX;
	}
}
