package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.ProjectHelperType;

public interface ProjectHelperTypeRepository extends JpaRepository<ProjectHelperType, Long> {

	List<ProjectHelperType> findByProject_IdAndHelperType_IdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday);

}
