package de.lh.tool.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.Need;

public interface NeedRepository extends JpaRepository<Need, Long> {
	public Optional<Need> findByProjectHelperType_IdAndDate(Long id, LocalDate date);

}
