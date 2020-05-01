package de.lh.tool.repository;

import java.time.LocalDate;
import java.util.Optional;

import de.lh.tool.domain.model.Need;

public interface NeedRepository extends BasicEntityRepository<Need, Long> {
	public Optional<Need> findByProjectHelperType_IdAndDate(Long id, LocalDate date);

}
