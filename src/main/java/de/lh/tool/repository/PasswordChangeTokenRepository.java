package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.PasswordChangeToken;

@Repository
public interface PasswordChangeTokenRepository extends CrudRepository<PasswordChangeToken, Long> {

}
