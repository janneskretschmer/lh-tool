package de.lh.tool.service.entity.interfaces;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.User;

public interface UserService extends BasicEntityService<User, Long> {

	UserDetails loadUserById(Long id);

	User changePassword(Long userId, String token, String oldPassword, String newPassword, String confirmPassword)
			throws DefaultException;

	Iterable<User> findByProjectId(Long projectId);

	Iterable<User> findByRoleIgnoreCase(String role);

	List<User> findByProjectIdAndRoleIgnoreCase(Long projectId, String role);

	User createUser(User user, String role) throws DefaultException;

	User getCurrentUser();

	User updateUser(User user) throws DefaultException;

	User loadUserByEmail(String email) throws UsernameNotFoundException;

	void requestPasswordReset(String email) throws DefaultException;

	JwtAuthenticationDto login(LoginDto loginDto);

}
