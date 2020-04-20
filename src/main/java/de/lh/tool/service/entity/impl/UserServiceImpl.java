package de.lh.tool.service.entity.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.MailService;
import de.lh.tool.service.entity.interfaces.PasswordChangeTokenService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.util.StringUtil;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class UserServiceImpl extends BasicMappableEntityServiceImpl<UserRepository, User, UserDto, Long>
		implements UserService, UserDetailsService {

	@Autowired
	private PasswordChangeTokenService passwordChangeTokenService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return loadUserByEmail(username);
	}

	@Override
	@Transactional
	public UserDetails loadUserById(Long id) {
		return getRepository().findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
	}

	@Override
	@Transactional
	public User loadUserByEmail(String email) throws UsernameNotFoundException {
		return getRepository().findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist"));
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
		final boolean emailAlreadyInUse = getRepository().findByEmail(user.getEmail()).isPresent();
		if (emailAlreadyInUse) {
			throw new DefaultException(ExceptionEnum.EX_USER_EMAIL_ALREADY_IN_USE);
		}
		user = save(user);
		if (userRoleService.hasCurrentUserRightToGrantRole(role)) {
			userRoleService.save(new UserRole(null, user, role));
		}
		PasswordChangeToken token = passwordChangeTokenService.saveRandomToken(user);

		if (UserRole.ROLE_LOCAL_COORDINATOR.equals(role)) {
			mailService.sendNewLocalCoordinatorMail(user, token);
		} else if (UserRole.ROLE_PUBLISHER.equals(role)) {
			mailService.sendNewPublisherMail(user, token);
		}

		return user;
	}

	@Override
	@Transactional
	public User updateUser(User user) throws DefaultException {
		if (user.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		User old = findById(user.getId()).orElseThrow(ExceptionEnum.EX_INVALID_USER_ID::createDefaultException);

		checkIfEditIsAllowed(old, true);

		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
		mapper.map(user, old);
		return save(old);
	}

	private void checkIfEditIsAllowed(User user, boolean allowedToEditSelf) throws DefaultException {
		UserPermissionCriteria criteria = Optional.ofNullable(user).map(this::evaluatePermissionsOnOtherUser)
				.orElse(new UserPermissionCriteria());
		if (!(allowedToEditSelf && criteria.isSelf())
				&& !(criteria.isAllowedToChangeFromForeignProjects()
						&& (user.getRoles().size() == 0 || criteria.isAllowedToGrantRoles()))
				&& !(criteria.isSameProject() && criteria.isAllowedToGrantRoles())) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
	}

	private boolean isViewAllowed(User user) {
		UserPermissionCriteria criteria = Optional.ofNullable(user).map(u -> evaluatePermissionsOnOtherUser(u))
				.orElse(new UserPermissionCriteria());
		boolean otherAllowedUser = criteria.isSameProject() || criteria.isAllowedToGetFromForeignProjects();
		return criteria.isSelf() || (criteria.isAllowedToGetOtherUsers() && otherAllowedUser);
	}

	private UserPermissionCriteria evaluatePermissionsOnOtherUser(User user) {
		UserPermissionCriteria criteria = new UserPermissionCriteria();
		criteria.setSelf(getCurrentUser().getId().equals(user.getId()));
		criteria.setAllowedToChangeFromForeignProjects(
				userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_CHANGE_FOREIGN));
		criteria.setAllowedToGetFromForeignProjects(
				userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_GET_FOREIGN));
		criteria.setAllowedToGetOtherUsers(userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_GET_FOREIGN));
		criteria.setAllowedToGrantRoles(user.getRoles().stream().map(UserRole::getRole)
				.allMatch(r -> userRoleService.hasCurrentUserRightToGrantRole(r)));
		criteria.setSameProject(projectService.getOwnProjects().stream().anyMatch(ownProject -> Optional
				.ofNullable(user.getProjects()).map(projects -> projects.contains(ownProject)).orElse(false)));
		return criteria;
	}

	@Data
	private class UserPermissionCriteria {
		private boolean self;
		private boolean allowedToChangeFromForeignProjects;
		private boolean allowedToGetFromForeignProjects;
		private boolean allowedToGetOtherUsers;
		private boolean allowedToGrantRoles;
		private boolean sameProject;
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
			// check might be senseless, because sb. who knows somebody's password could
			// just sign in and change the password...
			if (!Optional.ofNullable(user.getId()).map(id -> id.equals(getCurrentUser().getId())).orElse(false)) {
				throw new DefaultException(ExceptionEnum.EX_INVALID_USER_ID);
			}
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
	public List<User> findByProjectIdAndRoleIgnoreCase(Long projectId, String role) {
		return getRepository().findByProjects_IdAndRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(projectId, role);
	}

	@Override
	@Transactional
	public List<UserDto> findDtosByProjectIdAndRoleIgnoreCase(Long projectId, String role) throws DefaultException {
		if (projectId != null && !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_GET_FOREIGN)
				&& !getCurrentUser().getProjects().stream().anyMatch(project -> projectId.equals(project.getId()))) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		List<User> users = null;
		if (projectId != null && StringUtils.isNotBlank(role)) {
			users = getRepository().findByProjects_IdAndRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(projectId,
					role);
		} else if (projectId != null) {
			users = getRepository().findByProjects_IdOrderByLastNameAscFirstNameAsc(projectId);
		} else if (StringUtils.isNotBlank(role)) {
			users = getRepository().findByRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(role);
		} else {
			users = getRepository().findByOrderByLastNameAscFirstNameAsc();
		}
		if (users != null) {
			return convertToDtoList(users.stream().filter(this::isViewAllowed).collect(Collectors.toList()));
		}
		throw ExceptionEnum.EX_USERS_NOT_FOUND.createDefaultException();
	}

	@Override
	@Transactional
	public UserDto findDtoById(Long id) throws DefaultException {
		User user = findById(id).orElseThrow(
				() -> new DefaultException(userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_GET_FOREIGN)
						? ExceptionEnum.EX_WRONG_ID_PROVIDED
						: ExceptionEnum.EX_FORBIDDEN));
		if (!isViewAllowed(user)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return convertToDto(user);
	}

	@Override
	@Transactional
	public void requestPasswordReset(String email) throws DefaultException {
		try {
			User user = loadUserByEmail(email);
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
			User user = loadUserByEmail(loginDto.getEmail());
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
	@Transactional
	public void deleteUserById(Long id) throws DefaultException {
		User user = findById(id).orElseThrow(() -> ExceptionEnum.EX_INVALID_USER_ID.createDefaultException());
		checkIfEditIsAllowed(user, false);
		getRepository().delete(user);
	}

	public UserDto convertToDto(User user) {
		if (user == null) {
			return null;
		}
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<User, UserDto>() {
			@Override
			protected void configure() {
				using(ctx -> ctx.getSource() != null).map(user.getPasswordHash()).setActive(null);
			}
		});
		return modelMapper.map(user, UserDto.class);
	}
}
