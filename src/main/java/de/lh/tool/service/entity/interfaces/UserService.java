package de.lh.tool.service.entity.interfaces;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import de.lh.tool.domain.model.User;

public interface UserService extends BasicEntityService<User, Long> {

	UserDetails findUserDetailsById(Long id);

	boolean isCurrentUser(User user);

	User getCurrentUser();

	List<User> findByProjectIdAndRoleIgnoreCase(Long projectId, String role);

}
