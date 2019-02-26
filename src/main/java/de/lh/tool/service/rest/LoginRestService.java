package de.lh.tool.service.rest;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.config.security.JwtTokenProvider;
import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.PasswordResetDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.service.entity.interfaces.UserService;

@RestController
@RequestMapping(UrlMappings.LOGIN_PREFIX)
public class LoginRestService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UserService userService;

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	public Resource<JwtAuthenticationDto> authenticateUser(@RequestBody LoginDto loginDto) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		return new Resource<>(new JwtAuthenticationDto(jwt));
	}
	
	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.LOGIN_PASSWORD_RESET)
	public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetDto passwordResetDto)	throws DefaultException {
		String email = passwordResetDto.getEmail();
		// TODO Proper error message
		Objects.requireNonNull(email);
		userService.requestPasswordReset(email);
		return ResponseEntity.noContent().build();
	}

}
