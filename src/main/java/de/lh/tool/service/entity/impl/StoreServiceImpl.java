package de.lh.tool.service.entity.impl;

import java.time.LocalDate;

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
import de.lh.tool.service.entity.interfaces.crud.StoreCrudService;
import de.lh.tool.util.DateUtil;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class StoreServiceImpl extends BasicEntityCrudServiceImpl<StoreRepository, Store, StoreDto, Long>
		implements StoreService, StoreCrudService {

	@Autowired
	private ProjectService projectService;

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_STORE_ID;
	}

	@Override
	protected void checkValidity(@NonNull Store store) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, store.getName());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_TYPE, store.getType());
	}

	@Override
	protected void checkDeletable(@NonNull Store store) throws DefaultException {
		if (store.getSlots().stream().anyMatch(slot -> !slot.getItems().isEmpty())) {
			throw ExceptionEnum.EX_STORE_NOT_EMPTY.createDefaultException();
		}
	}

	@Override
	public boolean hasReadPermission(@NonNull Store store) {
		boolean hasPermission = userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT);
		hasPermission = hasPermission || (store.getStoreProjects().stream()
				.anyMatch(storeProject -> projectService.hasReadPermission(storeProject.getProject()) && DateUtil
						.isDateWithinRange(LocalDate.now(), storeProject.getStart(), storeProject.getEnd())));
		return hasPermission;
	}

	@Override
	public boolean hasWritePermission(@NonNull Store store) {
		return hasReadPermission(store);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.STORES_PREFIX;
	}

}
