package de.lh.tool.service.entity.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import de.lh.tool.service.entity.interfaces.BasicEntityService;
import lombok.Getter;

public abstract class BasicEntityServiceImpl<R extends CrudRepository<E, I>, E, I> implements BasicEntityService<E, I> {
	@Autowired
	@Getter
	private R repository;

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public void delete(E entity) {
		repository.delete(entity);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	@Override
	public void deleteAll(Iterable<? extends E> entities) {
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
	public Iterable<E> findAll() {
		return repository.findAll();
	}

	@Override
	public Iterable<E> findAllById(Iterable<I> ids) {
		return repository.findAllById(ids);
	}

	@Override
	public Optional<E> findById(I id) {
		return repository.findById(id);
	}

	@Override
	public E save(E entity) {
		return repository.save(entity);
	}

	@Override
	public Iterable<E> saveAll(Iterable<E> entities) {
		return repository.saveAll(entities);
	}
}
