package de.lh.tool.service.entity.impl;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.StoreDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Store;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.StoreRepository;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.StoreService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.util.DateUtil;
import lombok.NonNull;

@Service
public class StoreServiceImpl extends BasicMappableEntityServiceImpl<StoreRepository, Store, StoreDto, Long>
		implements StoreService {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private ProjectService projectService;

	@Override
	@Transactional
	public List<StoreDto> getStoreDtos() {
		return convertToDtoList(getOwnStores());
	}

	@Override
	@Transactional
	public StoreDto getStoreDtoById(Long id) throws DefaultException {
		Store store = findStoreByIdIfAllowed(id);
		return convertToDto(store);
	}

	private @NonNull Store findStoreByIdIfAllowed(Long id) throws DefaultException {
		Store store = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		if (!isViewAllowed(store)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return store;
	}

	@Override
	@Transactional
	public boolean isViewAllowed(Store store) {
		return userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT)
				|| (store != null && store.getStoreProjects().stream()
						.anyMatch(sp -> DateUtil.isDateWithinRange(LocalDate.now(), sp.getStart(), sp.getEnd())
								&& projectService.isOwnProject(sp.getProject())));
	}

	@Override
	@Transactional
	public StoreDto createStoreDto(StoreDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		Store store = save(convertToEntity(dto));
		return convertToDto(store);
	}

	@Override
	@Transactional
	public StoreDto updateStoreDto(StoreDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw ExceptionEnum.EX_NO_ID_PROVIDED.createDefaultException();
		}
		findStoreByIdIfAllowed(dto.getId());
		Store store = save(convertToEntity(dto));
		return convertToDto(store);
	}

	@Override
	@Transactional
	public List<Store> getOwnStores() {
		return userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT) ? findAll()
				: getRepository().findByCurrentProjectMembership(userService.getCurrentUser().getId());
	}

	@Override
	@Transactional
	public void deleteStoreById(Long id) throws DefaultException {
		Store store = findStoreByIdIfAllowed(id);
		if (store.getSlots().stream().anyMatch(slot -> !slot.getItems().isEmpty())) {
			throw ExceptionEnum.EX_STORE_NOT_EMPTY.createDefaultException();
		}
		delete(store);
	}

}
