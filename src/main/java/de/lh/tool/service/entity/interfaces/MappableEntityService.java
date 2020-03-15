package de.lh.tool.service.entity.interfaces;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;

import de.lh.tool.domain.exception.DefaultException;

/**
 * @param <E> Entity class
 * @param <D> Dto class
 */
public interface MappableEntityService<E, D, I> extends BasicEntityService<E, I> {

	default E convertToEntity(D dto) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(dto, getEntityClass());
	}

	default D convertToDto(E entity) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(entity, getDtoClass());
	}

	default List<E> convertToEntityList(Iterable<D> dtoList) {
		if (dtoList == null) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(dtoList.spliterator(), false).map(this::convertToEntity)
				.collect(Collectors.toList());
	}

	default List<D> convertToDtoList(Iterable<E> entityList) {
		if (entityList == null) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(entityList.spliterator(), false).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	Class<D> getDtoClass();

	Class<E> getEntityClass();

	List<D> findAllDtos() throws DefaultException;

	D findDtoById(I id) throws DefaultException;

}
