package de.lh.tool.service.entity.interfaces;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.User;

public interface UserService extends BasicEntityService<User, Long> {

	UserDetails loadUserById(Long id);

	User changePassword(Long userId, String token, String oldPassword, String newPassword, String confirmPassword)
			throws DefaultException;

	List<User> findByProjectIdAndRoleIgnoreCase(Long projectId, String role);

	User createUser(User user) throws DefaultException;

	User getCurrentUser();

	User updateUser(User user) throws DefaultException;

	User loadUserByEmail(String email) throws UsernameNotFoundException;

	void requestPasswordReset(String email) throws DefaultException;

	JwtAuthenticationDto login(LoginDto loginDto);

	void deleteUserById(Long id) throws DefaultException;

	List<UserDto> findDtosByProjectIdAndRoleIgnoreCase(Long projectId, String role) throws DefaultException;

	UserDto findDtoById(Long id) throws DefaultException;

	/**
	 * @throws DefaultException if editing is not allowed
	 */
	void checkIfEditIsAllowed(User user, boolean allowedToEditSelf) throws DefaultException;

}
