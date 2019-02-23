package de.lh.tool.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Iterable<User> findByProjects_Id(Long projectId);

	Iterable<User> findByRoles_RoleIgnoreCase(String role);

	Iterable<User> findByProjects_IdAndRoles_RoleIgnoreCase(Long projectId, String role);

}
