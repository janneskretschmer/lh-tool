package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.StoreDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Store;

public interface StoreService extends BasicEntityService<Store, Long> {

	List<StoreDto> getStoreDtos();

	StoreDto getStoreDtoById(Long id) throws DefaultException;

	StoreDto createStoreDto(StoreDto dto) throws DefaultException;

	StoreDto updateNeedDto(StoreDto dto, Long id) throws DefaultException;

}
