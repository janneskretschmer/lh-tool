package de.lh.tool.service.entity.impl;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.model.User;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class UserServiceImpl extends BasicEntityServiceImpl<UserRepository, User, Long>
		implements UserService, UserDetailsService {

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
}
