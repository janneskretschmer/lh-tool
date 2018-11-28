package de.lh.tool.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.bean.DBScript;

@Repository
public interface DBScriptRepository extends CrudRepository<DBScript, Long> {
	public List<DBScript> findAllByOrderByNameAsc();
}
