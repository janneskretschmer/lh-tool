package de.lh.tool.service.entity.interfaces;

import java.util.Optional;

public interface BasicEntityService<E, I> {

	E save(E entity);

	long count();

	void delete(E entity);

	void deleteAll();

	void deleteAll(Iterable<? extends E> entities);

	void deleteById(I id);

	boolean existsById(I id);

	Iterable<E> findAll();

	Iterable<E> findAllById(Iterable<I> ids);

	Optional<E> findById(I id);

	Iterable<E> saveAll(Iterable<E> entities);

}
