package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.TechnicalCrew;

public interface TechnicalCrewRepository extends JpaRepository<TechnicalCrew, Long> {

}
