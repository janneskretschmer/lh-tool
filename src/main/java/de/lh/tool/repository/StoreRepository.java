package de.lh.tool.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.Store;

public interface StoreRepository extends CrudRepository<Store, Long> {

	@Query(value = "SELECT * FROM store s WHERE s.id IN (SELECT store_id FROM store_project sp WHERE sp.project_id IN (SELECT pu.project_id FROM project_user pu WHERE user_id = ?1) AND CURDATE() BETWEEN start AND end)", nativeQuery = true)
	public Collection<Store> findByCurrentProjectMembership(Long userId);
}