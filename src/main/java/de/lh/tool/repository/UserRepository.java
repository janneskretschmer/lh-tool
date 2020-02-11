package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Iterable<User> findByProjects_IdOrderByLastNameAscFirstNameAsc(Long projectId);

	Iterable<User> findByRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(String role);

	List<User> findByProjects_IdAndRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(Long projectId, String role);

}
