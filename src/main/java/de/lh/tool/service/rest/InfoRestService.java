package de.lh.tool.service.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.INFO_PREFIX)
public class InfoRestService {

	@RequestMapping(method = RequestMethod.GET, path = UrlMappings.INFO_HEARTBEAT)
	@ApiOperation(value = "Shows if Application is running")
	public boolean heartbeat() {
		return true;
	}
}
