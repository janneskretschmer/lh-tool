package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.bean.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
