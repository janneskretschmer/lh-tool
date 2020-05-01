package de.lh.tool.repository;

import de.lh.tool.domain.model.StoreProject;

public interface StoreProjectRepository extends BasicEntityRepository<StoreProject, Long> {
	Iterable<StoreProject> findByStore_Id(Long id);

	void deleteByStore_Id(Long id);
}
