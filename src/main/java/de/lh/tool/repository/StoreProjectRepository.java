package de.lh.tool.repository;

import java.util.List;

import de.lh.tool.domain.model.Store;
import de.lh.tool.domain.model.StoreProject;

public interface StoreProjectRepository extends BasicEntityRepository<StoreProject, Long> {
	List<StoreProject> findByStore(Store store);

	void deleteByStore(Store store);
}
