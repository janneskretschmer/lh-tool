package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.bean.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
