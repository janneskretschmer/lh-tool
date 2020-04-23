package de.lh.tool.service.rest;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.config.security.SessionConstants;
import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.PasswordResetDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.service.entity.interfaces.UserService;
import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping(UrlMappings.LOGIN_PREFIX)
@CommonsLog
public class LoginRestService {

	@Autowired
	private UserService userService;

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	public Resource<JwtAuthenticationDto> authenticateUser(@RequestBody LoginDto loginDto, HttpServletRequest request) {
		JwtAuthenticationDto loginResult = userService.login(loginDto);
		if (loginResult != null && loginResult.getAccessToken() != null) {
			request.getSession().setAttribute(SessionConstants.ACCESSTOKEN, loginResult.getAccessToken());
		}
		return new Resource<>(loginResult);
	}
	
	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.LOGIN_CLEAR)
	public ResponseEntity<Void> clearSession(HttpServletRequest request) {
		request.getSession().removeAttribute(SessionConstants.ACCESSTOKEN);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.LOGIN_PASSWORD_RESET)
	public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetDto passwordResetDto)
			throws DefaultException {
		String email = passwordResetDto.getEmail();
		// TODO Proper error message
		Objects.requireNonNull(email);
		userService.requestPasswordReset(email);
		return ResponseEntity.noContent().build();
	}

}
