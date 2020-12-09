package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.BasicEntityRepository;
import de.lh.tool.service.entity.interfaces.BasicEntityService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import lombok.Getter;
import lombok.NonNull;

public abstract class BasicEntityServiceImpl<R extends BasicEntityRepository<E, I>, E extends Identifiable<I>, I>
		implements BasicEntityService<E, I> {
	@Autowired
	@Getter
	private R repository;

	@Autowired
	protected UserRoleService userRoleService;

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public void delete(E entity) {
		repository.delete(entity);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	@Override
	public void deleteAll(Iterable<? extends E> entities) {
		repository.deleteAll(entities);
	}

	@Override
	public void deleteById(I id) {
		repository.deleteById(id);
	}

	@Override
	public boolean existsById(I id) {
		return repository.existsById(id);
	}

	@Override
	public List<E> findAll() {
		return repository.findAll();
	}

	@Override
	public List<E> findAllById(Iterable<I> ids) {
		return repository.findAllById(ids);
	}

	@Override
	public Optional<E> findById(I id) {
		return repository.findById(id);
	}

	@Override
	public E save(E entity) {
		return repository.save(entity);
	}

	@Override
	public List<E> saveAll(Iterable<E> entities) {
		return repository.saveAll(entities);
	}

	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_ID;
	}

	@Override
	@Transactional
	public E findByIdOrThrowInvalidIdException(I id) throws DefaultException {
		return findById(id).orElseThrow(getInvalidIdException()::createDefaultException);
	}

	// R/W permissions

	@Override
	public abstract boolean hasReadPermission(@NonNull E entity);

	@Override
	@Transactional
	public boolean hasReadPermission(@NonNull I id) throws DefaultException {
		return hasReadPermission(findByIdOrThrowInvalidIdException(id));
	}

	@Override
	@Transactional
	public void checkReadPermission(@NonNull E entity) throws DefaultException {
		if (!hasReadPermission(entity)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	@Transactional
	public void checkReadPermission(@NonNull I id) throws DefaultException {
		if (!hasReadPermission(id)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	public abstract boolean hasWritePermission(@NonNull E entity);

	@Override
	@Transactional
	public boolean hasWritePermission(@NonNull I id) throws DefaultException {
		return hasWritePermission(findByIdOrThrowInvalidIdException(id));
	}

	@Override
	@Transactional
	public void checkWritePermission(@NonNull E entity) throws DefaultException {
		if (!hasWritePermission(entity)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	@Transactional
	public void checkWritePermission(@NonNull I id) throws DefaultException {
		if (!hasWritePermission(id)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	// Find permission

	protected abstract String getRightPrefix();

	@Override
	@Transactional
	public boolean hasFindPermission(@NonNull E entity) {
		return hasFindRight() && hasReadPermission(entity);
	}

	@Override
	@Transactional
	public boolean hasFindRight() {
		return userRoleService.hasCurrentUserRight(getRightPrefix() + UserRole.GET_SUFFIX);
	}

	@Override
	@Transactional
	public void checkFindRight() throws DefaultException {
		if (!hasFindRight()) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	@Transactional
	public boolean hasFindPermission(@NonNull I id) throws DefaultException {
		return hasFindPermission(findByIdOrThrowInvalidIdException(id));
	}

	@Override
	@Transactional
	public void checkFindPermission(@NonNull E entity) throws DefaultException {
		if (!hasFindPermission(entity)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	@Transactional
	public void checkFindPermission(@NonNull I id) throws DefaultException {
		if (!hasFindPermission(id)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	// Create permission

	@Override
	@Transactional
	public boolean hasCreatePermission(@NonNull E entity) {
		return userRoleService.hasCurrentUserRight(getRightPrefix() + UserRole.POST_SUFFIX)
				&& hasWritePermission(entity);
	}

	@Override
	@Transactional
	public void checkCreatePermission(@NonNull E entity) throws DefaultException {
		if (!hasCreatePermission(entity)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	// Update permission

	@Override
	@Transactional
	public boolean hasUpdatePermission(@NonNull E entity) {
		return userRoleService.hasCurrentUserRight(getRightPrefix() + UserRole.PUT_SUFFIX)
				&& hasWritePermission(entity);
	}

	@Override
	@Transactional
	public void checkUpdatePermission(@NonNull E entity) throws DefaultException {
		if (!hasUpdatePermission(entity)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	// Delete permission

	@Override
	@Transactional
	public boolean hasDeletePermission(@NonNull E entity) {
		return userRoleService.hasCurrentUserRight(getRightPrefix() + UserRole.DELETE_SUFFIX)
				&& hasWritePermission(entity);
	}

	@Override
	@Transactional
	public void checkDeletePermission(@NonNull E entity) throws DefaultException {
		if (!hasDeletePermission(entity)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}
}
