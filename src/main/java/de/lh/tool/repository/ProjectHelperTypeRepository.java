package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.lh.tool.domain.model.ProjectHelperType;

public interface ProjectHelperTypeRepository extends BasicEntityRepository<ProjectHelperType, Long> {

	@Query("SELECT pht FROM ProjectHelperType pht WHERE pht.project.id=:projectId AND (:helperTypeId IS NULL OR pht.helperType.id=:helperTypeId) AND (:weekday IS NULL OR pht.weekday=:weekday)")
	List<ProjectHelperType> findByProjectIdAndNullableHelperTypeIdAndNullableWeekday(@Param("projectId") Long projectId,
			@Param("helperTypeId") Long helperTypeId, @Param("weekday") Integer weekday);
}
