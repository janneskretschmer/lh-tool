package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.bean.DBScript;

public interface DBScriptService extends BasicEntityService<DBScript, Long> {

	List<DBScript> findAllOrderedByName();

	DBScript save(DBScript dbscript);

}
