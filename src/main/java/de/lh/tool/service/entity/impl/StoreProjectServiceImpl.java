package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.StoreProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Store;
import de.lh.tool.domain.model.StoreProject;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.StoreProjectRepository;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.StoreProjectService;
import de.lh.tool.service.entity.interfaces.StoreService;
import de.lh.tool.service.entity.interfaces.crud.StoreProjectCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class StoreProjectServiceImpl
		extends BasicEntityCrudServiceImpl<StoreProjectRepository, StoreProject, StoreProjectDto, Long>
		implements StoreProjectService, StoreProjectCrudService {

	@Autowired
	private StoreService storeService;
	@Autowired
	private ProjectService projectService;

	// FUTURE get rid fo this
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public List<StoreProjectDto> findDtosByStoreId(@NonNull Long storeId) throws DefaultException {
		checkFindRight();
		Store store = storeService.findByIdOrThrowInvalidIdException(storeId);
		storeService.checkReadPermission(store);
		return convertToDtoList(filterFindResult(getRepository().findByStore(store)));
	}

	@Override
	protected void checkValidity(@NonNull StoreProject storeProject) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_END_DATE, storeProject.getEnd());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_PROJECT_ID, storeProject.getProject());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_START_DATE, storeProject.getStart());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_STORE_ID, storeProject.getStore());
	}

	// FUTURE: use standard methods from super class (new rights are required)
	@Override
	@Transactional
	public List<StoreProjectDto> bulkDeleteAndCreateByStoreId(@NonNull Long storeId,
			@NonNull Collection<StoreProjectDto> dtos) throws DefaultException {
		if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_PUT)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		Store store = storeService.findByIdOrThrowInvalidIdException(storeId);
		storeService.checkWritePermission(store);

		getRepository().deleteByStore(store);
		entityManager.flush();

		List<StoreProject> result = dtos.stream().map(this::convertToEntity).filter(this::hasWritePermission)
				.map(getRepository()::save).collect(Collectors.toList());

		return convertToDtoList(result);
	}

	@Override
	public boolean hasReadPermission(@NonNull StoreProject entity) {
		return projectService.hasReadPermission(entity.getProject())
				&& storeService.hasReadPermission(entity.getStore());
	}

	@Override
	public boolean hasWritePermission(@NonNull StoreProject entity) {
		return projectService.hasReadPermission(entity.getProject())
				&& storeService.hasWritePermission(entity.getStore());
	}

	@Override
	public String getRightPrefix() {
		return UserRole.STORES_PREFIX;
	}

}
