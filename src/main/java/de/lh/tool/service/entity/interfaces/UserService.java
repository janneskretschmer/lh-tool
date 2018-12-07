package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.bean.model.User;

public interface UserService extends BasicEntityService<User, Long> {

	List<User> findByEmail(String email);

}
