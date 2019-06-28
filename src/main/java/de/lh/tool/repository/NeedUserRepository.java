package de.lh.tool.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.NeedUser;
import de.lh.tool.domain.model.User;

@Repository
public interface NeedUserRepository extends CrudRepository<NeedUser, Long> {
	Optional<NeedUser> findByNeedAndUser(Need need, User user);
	
	Iterable<NeedUser> findByNeedOrderByUser_LastNameAscUser_FirstNameAsc(Need need);
}
