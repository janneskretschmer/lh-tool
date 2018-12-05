package de.lh.tool.service.entity.impl;

import de.lh.tool.bean.model.User;
import de.lh.tool.repository.UserRepository;
import de.lh.tool.service.entity.interfaces.UserService;

public class UserServiceImpl extends BasicEntityServiceImpl<UserRepository, User, Long> implements UserService {

}
