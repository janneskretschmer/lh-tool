package de.lh.tool.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeDto {
	private Long userId;
	private String token;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
}
