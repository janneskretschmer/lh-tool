package de.lh.tool.service.entity.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

/**
 * @param <E> Entity class
 * @param <D> Dto class
 */
public interface MappableEntityService<E, D> {

	default E convertToEntity(D dto) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(dto, getEntityClass());
	}

	default D convertToDto(E entity) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(entity, getDtoClass());
	}

	default List<E> convertToEntityList(Collection<D> dtoList) {
		if (dtoList == null) {
			return Collections.emptyList();
		}
		return dtoList.stream().map(this::convertToEntity).collect(Collectors.toList());
	}

	default List<D> convertToDtoList(Collection<E> entityList) {
		if (entityList == null) {
			return Collections.emptyList();
		}
		return entityList.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	Class<D> getDtoClass();

	Class<E> getEntityClass();

}
