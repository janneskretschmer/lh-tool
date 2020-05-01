package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicEntityRepository<T, ID> extends JpaRepository<T, ID> {

}
