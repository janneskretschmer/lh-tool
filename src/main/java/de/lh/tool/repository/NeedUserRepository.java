package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.NeedUser;
import de.lh.tool.domain.model.User;

@Repository
public interface NeedUserRepository extends JpaRepository<NeedUser, Long> {
	Optional<NeedUser> findByNeedAndUser(Need need, User user);

	List<NeedUser> findByNeedOrderByUser_LastNameAscUser_FirstNameAsc(Need need);
}
