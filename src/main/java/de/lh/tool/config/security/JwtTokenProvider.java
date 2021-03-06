package de.lh.tool.config.security;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import de.lh.tool.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@PropertySource(value = { "classpath:credentials.properties" })
public class JwtTokenProvider {

	@Value("${app.jwtSecret}")
	@Setter
	private String jwtSecret;

	@Value("${app.jwtExpirationInMs}")
	@Setter
	private int jwtExpirationInMs;

	public String generateToken(User user) {
		if (user == null) {
			return null;
		}

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(Long.toString(user.getId()))
				.claim("permissions",
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date()).setExpiration(expiryDate).signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token", ex);
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token", ex);
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token", ex);
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.", ex);
		}
		return false;
	}
}