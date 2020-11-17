package de.lh.tool.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.UserRole;

@Repository
public interface UserRoleRepository extends BasicEntityRepository<UserRole, Long> {
	List<UserRole> findByUserId(Long userId);
}
