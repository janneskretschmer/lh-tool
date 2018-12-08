package de.lh.tool.service.entity.impl;

import org.springframework.stereotype.Service;

import de.lh.tool.domain.model.User;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class UserServiceImpl extends BasicEntityServiceImpl<UserRepository, User, Long> implements UserService {

}
