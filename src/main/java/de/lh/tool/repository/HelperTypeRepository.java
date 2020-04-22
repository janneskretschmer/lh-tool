package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.lh.tool.domain.model.HelperType;

public interface HelperTypeRepository extends JpaRepository<HelperType, Long> {
	@Query("SELECT DISTINCT ph.helperType FROM ProjectHelperType ph WHERE ph.project.id=:projectId AND ph.weekday=:weekday")
	List<HelperType> findByProjectIdAndWeekday(@Param("projectId") Long projectId, @Param("weekday") Integer weekday);

	Boolean existsByName(String name);
}
