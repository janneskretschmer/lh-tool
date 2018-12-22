package de.lh.tool.domain.dto;

import lombok.Data;

@Data
public class UserCreationDto {
	private String firstName;
	private String lastName;
	private String email;
	private String gender;
}
