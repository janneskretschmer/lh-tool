package de.lh.tool.service.entity.interfaces;

public interface UrlService {

	String getPasswordChangeUrl(Long userId, String token);

}
