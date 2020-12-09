package de.lh.tool.service.entity.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.repository.BasicEntityRepository;
import de.lh.tool.service.entity.interfaces.crud.BasicEntityCrudService;
import lombok.NonNull;

public abstract class BasicEntityCrudServiceImpl<R extends BasicEntityRepository<E, I>, E extends Identifiable<I>, D extends Identifiable<I>, I>
		extends BasicEntityServiceImpl<R, E, I> implements BasicEntityCrudService<E, D, I> {
	@Autowired
	protected ModelMapper modelMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	private void addMappings() {
		AbstractConverter<I, E> converter = new AbstractConverter<I, E>() {
			@Override
			public E convert(I id) {
				return findById(id).orElseThrow(getInvalidIdException()::createDefaultRuntimeException);
			}
		};
		modelMapper.addConverter(converter, getIdClass(), getEntityClass());

	}

	// CRUD

	@Override
	@Transactional
	public List<D> findDtos() throws DefaultException {
		checkFindRight();
		return convertToDtoList(filterFindResult(findAll()));
	}

	@Override
	@Transactional
	public D findDtoById(@NonNull I id) throws DefaultException {
		E entity = findByIdOrThrowInvalidIdException(id);
		checkFindPermission(entity);
		return convertToDto(entity);
	}

	protected List<E> filterFindResult(@NonNull List<E> entityList) {
		return entityList.stream().filter(this::hasReadPermission).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public D createDto(@NonNull D dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ILLEGAL_ID.createDefaultException();
		}
		E entity = convertToEntity(dto);
		checkCreatePermission(entity);
		checkValidity(entity);

		entity = save(entity);
		postCreate(entity);
		return convertToDto(entity);
	}

	/**
	 * override for actions after entity got saved, e.g. logging, notifications or
	 * save dependent entities
	 * 
	 * @param entity
	 */
	protected void postCreate(@NonNull E entity) {
		// default: no action
	}

	@Override
	@Transactional
	public D updateDto(@NonNull D dto, @NonNull I id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw ExceptionEnum.EX_NO_ID.createDefaultException();
		}

		E old = findByIdOrThrowInvalidIdException(dto.getId());
		checkUpdatePermission(old);
		// otherwise it would get updated as well and detecting changes would be
		// impossible
		entityManager.detach(old);

		E entity = convertToEntity(dto);
		checkUpdatePermission(entity);
		checkValidity(entity);

		preUpdate(old, entity);
		entity = save(entity);
		postUpdate(old, entity);
		return convertToDto(entity);
	}

	/**
	 * override for actions before entity gets updated
	 * 
	 * @param oldEntity
	 * @param newEntity
	 */
	protected void preUpdate(@NonNull E oldEntity, @NonNull E newEntity) {
		// default: no action
	}

	/**
	 * override for action after entity got updated, e.g. logging or notifications
	 * 
	 * @param oldEntity
	 * @param newEntity
	 */
	protected void postUpdate(@NonNull E oldEntity, @NonNull E newEntity) {
		// default: no action
	}

	@Override
	@Transactional
	public void deleteDtoById(@NonNull I id) throws DefaultException {
		E entity = findByIdOrThrowInvalidIdException(id);
		checkDeletePermission(entity);
		checkDeletable(entity);
		delete(entity);
		postDelete(entity);
	}

	/**
	 * override for action after entity got deleted, e.g. logging or notifications
	 * 
	 * @param deletedEntity
	 */
	protected void postDelete(@NonNull E deletedEntity) {
		// default: no action
	}

	/**
	 * override for duplicate checks, null-checks of obligatory fields, etc.
	 * 
	 * @param entity
	 * @throws DefaultException
	 */
	protected void checkValidity(@NonNull E entity) throws DefaultException {
		// default: no action
	}

	protected void checkDeletable(@NonNull E entity) throws DefaultException {
		// default: no action
	}

	// conversion

	@SuppressWarnings("unchecked")
	public Class<E> getEntityClass() {
		return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@SuppressWarnings("unchecked")
	public Class<D> getDtoClass() {
		return (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	}

	@SuppressWarnings("unchecked")
	public Class<I> getIdClass() {
		return (Class<I>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
	}

	public E convertToEntity(D dto) {
		return modelMapper.map(dto, getEntityClass());
	}

	public D convertToDto(E entity) {
		return modelMapper.map(entity, getDtoClass());
	}

	public List<E> convertToEntityList(Iterable<D> dtoList) {
		if (dtoList == null) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(dtoList.spliterator(), false).map(this::convertToEntity)
				.collect(Collectors.toList());
	}

	public List<D> convertToDtoList(Iterable<E> entityList) {
		if (entityList == null) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(entityList.spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

}
