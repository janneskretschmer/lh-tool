package de.lh.tool.domain.dto;

import lombok.Data;

@Data
public class PasswordChangeDto {
	private Long userId;
	private String token;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
}
