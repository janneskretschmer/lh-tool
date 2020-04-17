package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	List<User> findByProjects_IdOrderByLastNameAscFirstNameAsc(Long projectId);

	List<User> findByRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(String role);

	List<User> findByProjects_IdAndRoles_RoleIgnoreCaseOrderByLastNameAscFirstNameAsc(Long projectId, String role);

}
