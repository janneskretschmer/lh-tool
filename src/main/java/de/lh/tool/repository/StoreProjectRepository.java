package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.StoreProject;

public interface StoreProjectRepository extends JpaRepository<StoreProject, Long> {
	Iterable<StoreProject> findByStore_Id(Long id);

	void deleteByStore_Id(Long id);
}
