package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.lh.tool.domain.model.Item;

public interface ItemRepository extends BasicEntityRepository<Item, Long> {

	public Optional<Item> findByIdentifier(String identifier);

	@Query("SELECT i FROM Item i WHERE " + "(:freeText IS NULL OR LOWER("
	//
			+ "i.identifier) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "i.name) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "i.description) LIKE CONCAT('%',LOWER(:freeText),'%') OR LOWER("
			+ "i.technicalCrew.name) LIKE CONCAT('%',LOWER(:freeText),'%') OR EXISTS(SELECT id FROM "
			+ "i.itemNotes WHERE note LIKE CONCAT('%',LOWER(:freeText),'%')) OR EXISTS(SELECT id FROM "
			+ "i.tags WHERE name LIKE CONCAT('%',LOWER(:freeText),'%'))" + ") "
			+ "ORDER BY i.name ASC, i.identifier ASC ")
	List<Item> findByFilters(@Param("freeText") String freeText);
}
