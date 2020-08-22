package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.lh.tool.domain.model.Slot;

public interface SlotRepository extends BasicEntityRepository<Slot, Long> {

	@Query("SELECT s FROM Slot s WHERE " + "(:storeId IS NULL OR s.store.id = :storeId) "
			+ "AND (:name IS NULL OR LOWER(s.name) LIKE CONCAT('%',LOWER(:name),'%')) "
			+ "AND (:description IS NULL OR LOWER(s.description) LIKE CONCAT('%',LOWER(:description),'%')) "
			+ "AND (:freeText IS NULL OR LOWER("
			//
			+ "s.name) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "s.description) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "s.width) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "s.height) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "s.depth) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "s.store.name) LIKE CONCAT('%',LOWER(:freeText),'%')) " + "ORDER BY s.name ASC, s.store.name ASC ")
	List<Slot> findByNameAndDescriptionAndStoreIdAndFreeTextIgnoreCase(@Param("freeText") String freeText,
			@Param("name") String name, @Param("description") String description, @Param("storeId") Long storeId);

}
