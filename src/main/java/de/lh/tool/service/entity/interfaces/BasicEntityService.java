package de.lh.tool.service.entity.interfaces;

import java.util.List;
import java.util.Optional;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.exception.DefaultException;
import lombok.NonNull;

public interface BasicEntityService<E extends Identifiable<I>, I> {

	E save(E entity);

	long count();

	void delete(E entity);

	void deleteAll();

	void deleteAll(Iterable<? extends E> entities);

	void deleteById(I id);

	boolean existsById(I id);

	List<E> findAll();

	List<E> findAllById(Iterable<I> ids);

	Optional<E> findById(I id);

	List<E> saveAll(Iterable<E> entities);

	E findByIdOrThrowInvalidIdException(I id) throws DefaultException;

	boolean hasReadPermission(@NonNull E entity);

	boolean hasReadPermission(@NonNull I id) throws DefaultException;

	void checkReadPermission(@NonNull E entity) throws DefaultException;

	void checkReadPermission(@NonNull I id) throws DefaultException;

	boolean hasWritePermission(@NonNull E entity);

	boolean hasWritePermission(@NonNull I id) throws DefaultException;

	void checkWritePermission(@NonNull E entity) throws DefaultException;

	void checkWritePermission(@NonNull I id) throws DefaultException;

	boolean hasFindPermission(@NonNull E entity);

	boolean hasFindRight();

	void checkFindRight() throws DefaultException;

	boolean hasFindPermission(@NonNull I id) throws DefaultException;

	void checkFindPermission(@NonNull E entity) throws DefaultException;

	void checkFindPermission(@NonNull I id) throws DefaultException;

	boolean hasCreatePermission(@NonNull E entity);

	void checkCreatePermission(@NonNull E entity) throws DefaultException;

	boolean hasUpdatePermission(@NonNull E entity);

	void checkUpdatePermission(@NonNull E entity) throws DefaultException;

	boolean hasDeletePermission(@NonNull E entity);

	void checkDeletePermission(@NonNull E entity) throws DefaultException;

}
