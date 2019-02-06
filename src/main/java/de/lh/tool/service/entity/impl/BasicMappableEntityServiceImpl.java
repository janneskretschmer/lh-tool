package de.lh.tool.service.entity.impl;

import java.lang.reflect.ParameterizedType;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.service.entity.interfaces.MappableEntityService;

public abstract class BasicMappableEntityServiceImpl<R extends CrudRepository<E, I>, E, D, I>
		extends BasicEntityServiceImpl<R, E, I> implements MappableEntityService<E, D> {

	@Override
	public Class<E> getEntityClass() {
		return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@Override
	public Class<D> getDtoClass() {
		return (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	}

}
