package de.lh.tool.service.entity.interfaces.crud;

import java.util.Collection;
import java.util.List;

import de.lh.tool.domain.dto.StoreProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.StoreProject;

public interface StoreProjectCrudService extends BasicEntityCrudService<StoreProject, StoreProjectDto, Long> {
	List<StoreProjectDto> findDtosByStoreId(Long storeId) throws DefaultException;

	List<StoreProjectDto> bulkDeleteAndCreateByStoreId(Long storeId, Collection<StoreProjectDto> dtos)
			throws DefaultException;
}
