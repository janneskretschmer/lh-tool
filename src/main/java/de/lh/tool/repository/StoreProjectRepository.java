package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.StoreProject;

public interface StoreProjectRepository extends CrudRepository<StoreProject, Long> {
	Iterable<StoreProject> findByStore_Id(Long id);
}
