package de.lh.tool.repository;

import java.util.List;

import de.lh.tool.domain.model.ProjectHelperType;

public interface ProjectHelperTypeRepository extends BasicEntityRepository<ProjectHelperType, Long> {

	List<ProjectHelperType> findByProject_IdAndHelperType_IdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday);

}
