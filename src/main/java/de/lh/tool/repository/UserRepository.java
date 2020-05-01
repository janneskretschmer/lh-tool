package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.User;

@Repository
public interface UserRepository extends BasicEntityRepository<User, Long> {

	Optional<User> findByEmail(String email);

	List<User> findByOrderByLastNameAscFirstNameAsc();

	List<User> findByProjects_IdOrderByLastNameAscFirstNameAsc(Long projectId);

	List<User> findByRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(String role);

	List<User> findByProjects_IdAndRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(Long projectId, String role);

}
