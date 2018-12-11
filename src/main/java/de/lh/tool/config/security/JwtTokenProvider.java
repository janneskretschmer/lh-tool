package de.lh.tool.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import de.lh.tool.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
@PropertySource(value = { "classpath:credentials.properties" })
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expirationInMs}")
	private int jwtExpirationInMs;

	public String generateToken(Authentication authentication) {

		User user = (User) authentication.getPrincipal();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(Long.toString(user.getId())).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
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