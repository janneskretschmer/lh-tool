package de.lh.tool.domain.dto;

import lombok.Data;

@Data
public class JwtAuthenticationDto {
	private String accessToken;
	private String tokenType = "Bearer";

	public JwtAuthenticationDto(String accessToken) {
		this.accessToken = accessToken;
	}
}
