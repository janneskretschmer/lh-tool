package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @param <T> type of entity
 * @param <I> type of id
 */
@NoRepositoryBean
public interface BasicEntityRepository<T, I> extends JpaRepository<T, I> {

}
