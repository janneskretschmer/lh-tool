package de.lh.tool.service.entity.interfaces;

import java.util.Optional;

public interface BasicEntityService<T, ID> {

	T save(T entity);

	long count();

	void delete(T entity);

	void deleteAll();

	void deleteAll(Iterable<? extends T> entities);

	void deleteById(ID id);

	boolean existsById(ID id);

	Iterable<T> findAll();

	Iterable<T> findAllById(Iterable<ID> ids);

	Optional<T> findById(ID id);

	Iterable<T> saveAll(Iterable<T> entities);

}
