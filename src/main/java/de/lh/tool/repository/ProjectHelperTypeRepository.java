package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.ProjectHelperType;

public interface ProjectHelperTypeRepository extends CrudRepository<ProjectHelperType, Long> {

	List<ProjectHelperType> findByProject_IdAndHelperType_IdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday);

}
