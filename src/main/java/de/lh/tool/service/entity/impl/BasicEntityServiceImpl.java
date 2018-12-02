package de.lh.tool.service.entity.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import de.lh.tool.service.entity.interfaces.BasicEntityService;

public abstract class BasicEntityServiceImpl<R extends CrudRepository<T, I>, T, I> implements BasicEntityService<T, I> {
	@Autowired
	private R repository;

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public void delete(T entity) {
		repository.delete(entity);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		repository.deleteAll(entities);
	}

	@Override
	public void deleteById(I id) {
		repository.deleteById(id);
	}

	@Override
	public boolean existsById(I id) {
		return repository.existsById(id);
	}

	@Override
	public Iterable<T> findAll() {
		return repository.findAll();
	}

	@Override
	public Iterable<T> findAllById(Iterable<I> ids) {
		return repository.findAllById(ids);
	}

	@Override
	public Optional<T> findById(I id) {
		return repository.findById(id);
	}

	@Override
	public T save(T entity) {
		return repository.save(entity);
	}

	@Override
	public Iterable<T> saveAll(Iterable<T> entities) {
		return repository.saveAll(entities);
	}
}
