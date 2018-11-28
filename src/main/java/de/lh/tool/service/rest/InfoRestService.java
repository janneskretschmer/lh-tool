package de.lh.tool.service.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLMappings.INFO_PREFIX)
public class InfoRestService {
	
	@RequestMapping(URLMappings.INFO_HEARTBEAT)
	public boolean heartbeat() {
		return true;
	}
}
