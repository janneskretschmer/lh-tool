package de.lh.tool.service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.service.entity.interfaces.MailService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.INFO_PREFIX)
public class InfoRestService {

	@Autowired
	private MailService mailService;

	@RequestMapping(method = RequestMethod.GET, path = UrlMappings.INFO_HEARTBEAT)
	@ApiOperation(value = "Shows if Application is running")
	public boolean heartbeat() {
		mailService.test();
		return true;
	}
}
