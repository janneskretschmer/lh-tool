package de.lh.tool.service.entity.impl;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.lh.tool.service.entity.interfaces.UrlService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UrlServiceImpl implements UrlService {

	@Value("${app.base}")
	private String baseUrl;

	@Override
	public String getPasswordChangeUrl(Long userId, String token) {
		try {
			URIBuilder uriBuilder = new URIBuilder(baseUrl + "/web/changepw");
			uriBuilder.addParameter("uid", userId.toString());
			uriBuilder.addParameter("token", token);
			return uriBuilder.toString();
		} catch (URISyntaxException e) {
			log.catching(e);
		}
		throw new IllegalArgumentException();
	}
}
