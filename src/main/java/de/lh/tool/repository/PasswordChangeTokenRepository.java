package de.lh.tool.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.PasswordChangeToken;

@Repository
public interface PasswordChangeTokenRepository extends CrudRepository<PasswordChangeToken, Long> {
	Optional<PasswordChangeToken> findByUser_Id(Long userId);
}
