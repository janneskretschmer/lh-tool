package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.UserRole;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

}
