package de.lh.tool.service.entity.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.transaction.Transactional;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.repository.BasicEntityRepository;
import de.lh.tool.service.entity.interfaces.MappableEntityService;

public abstract class BasicMappableEntityServiceImpl<R extends BasicEntityRepository<E, I>, E, D, I>
		extends BasicEntityServiceImpl<R, E, I> implements MappableEntityService<E, D, I> {

	@SuppressWarnings("unchecked")
	@Override
	public Class<E> getEntityClass() {
		return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<D> getDtoClass() {
		return (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	}

	@Override
	@Transactional
	public List<D> findAllDtos() throws DefaultException {
		return convertToDtoList(findAll());
	}

	@Override
	@Transactional
	public D findDtoById(I id) throws DefaultException {
		return convertToDto(findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID)));
	}

}
