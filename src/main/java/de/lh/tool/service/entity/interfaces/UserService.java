package de.lh.tool.service.entity.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import de.lh.tool.domain.model.User;

public interface UserService extends BasicEntityService<User, Long> {

	UserDetails loadUserById(Long id);

}
