package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.lh.tool.domain.model.Store;

public interface StoreRepository extends BasicEntityRepository<Store, Long> {

	@Query(value = "SELECT * FROM store s WHERE s.id IN (SELECT store_id FROM store_project sp WHERE sp.project_id IN (SELECT pu.project_id FROM project_user pu WHERE user_id = :userId) AND CURDATE() BETWEEN start AND end)", nativeQuery = true)
	public List<Store> findByCurrentProjectMembership(@Param("userId") Long userId);
}
