package de.lh.tool.repository;

import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.UserRole;

@Repository
public interface UserRoleRepository extends BasicEntityRepository<UserRole, Long> {
}
