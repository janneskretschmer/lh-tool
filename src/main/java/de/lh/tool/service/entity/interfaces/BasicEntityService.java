package de.lh.tool.service.entity.interfaces;

import java.util.Optional;

public interface BasicEntityService<T, I> {

	T save(T entity);

	long count();

	void delete(T entity);

	void deleteAll();

	void deleteAll(Iterable<? extends T> entities);

	void deleteById(I id);

	boolean existsById(I id);

	Iterable<T> findAll();

	Iterable<T> findAllById(Iterable<I> ids);

	Optional<T> findById(I id);

	Iterable<T> saveAll(Iterable<T> entities);

}
