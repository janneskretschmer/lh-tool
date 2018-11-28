package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import de.lh.tool.bean.DBScript;
import de.lh.tool.repository.DBScriptRepository;
import de.lh.tool.service.entity.interfaces.DBScriptService;

@Service
@Transactional
public class DBScriptServiceImpl extends BasicEntityServiceImpl<DBScriptRepository, DBScript, Long>
		implements DBScriptService {

	@Override
	public List<DBScript> findAllOrderedByName() {
		return getRepository().findAllByOrderByNameAsc();
	}

}
