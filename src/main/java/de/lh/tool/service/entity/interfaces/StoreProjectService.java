package de.lh.tool.service.entity.interfaces;

import java.util.Collection;

import de.lh.tool.domain.dto.StoreProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.StoreProject;

public interface StoreProjectService extends BasicEntityService<StoreProject, Long> {
	Collection<StoreProjectDto> findDtosByStoreId(Long storeId) throws DefaultException;

	Collection<StoreProjectDto> bulkDeleteAndCreateByStoreId(Long storeId, Collection<StoreProjectDto> dtos)
			throws DefaultException;
}
