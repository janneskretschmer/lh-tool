package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtAuthenticationDto {
	private String accessToken;
	private String tokenType = "Bearer";

	public JwtAuthenticationDto(String accessToken) {
		this.accessToken = accessToken;
	}
}
