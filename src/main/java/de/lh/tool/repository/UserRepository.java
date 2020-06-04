package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.User;

@Repository
public interface UserRepository extends BasicEntityRepository<User, Long> {

	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE 1=1 "
			+ "AND (:projectId IS NULL OR :projectId IN (SELECT pu.project.id FROM ProjectUser pu WHERE pu.user.id = u.id)) "
			+ "AND (:role IS NULL OR UPPER(:role) IN (SELECT ur.role FROM UserRole ur WHERE ur.user.id = u.id)) "
			+ "AND (:freeText IS NULL OR LOWER("
			//
			+ "u.firstName) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.lastName) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.email) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.telephoneNumber) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.mobileNumber) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.businessNumber) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.profession) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "u.skills) LIKE CONCAT('%',LOWER(:freeText),'%')) " + "ORDER BY u.lastName ASC, u.firstName ASC ")
	List<User> findByProjectIdAndRoleAndFreeTextIgnoreCase(@Param("projectId") Long projectId,
			@Param("role") String role, @Param("freeText") String freeText);

}
