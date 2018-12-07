package de.lh.tool.service.entity.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import de.lh.tool.bean.model.User;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class UserServiceImpl extends BasicEntityServiceImpl<UserRepository, User, Long> implements UserService {

	@Override
	public List<User> findByEmail(String email) {
		return getRepository().findByEmail(email);
	}
}
