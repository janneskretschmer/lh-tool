package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.service.entity.interfaces.BasicEntityService;
import lombok.NonNull;

/**
 * @param <E> Entity class
 * @param <D> Dto class
 */
public interface BasicEntityCrudService<E extends Identifiable<I>, D extends Identifiable<I>, I>
		extends BasicEntityService<E, I> {

	List<D> findDtos() throws DefaultException;

	D findDtoById(@NonNull I id) throws DefaultException;

	D createDto(@NonNull D dto) throws DefaultException;

	D updateDto(@NonNull D dto, @NonNull I id) throws DefaultException;

	void deleteDtoById(@NonNull I id) throws DefaultException;

}
