package de.lh.tool.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDto {
	private String firstName;
	private String lastName;
	private String email;
	private String gender;
	private String telephoneNumber;
	private String mobileNumber;
	private String businessNumber;
	private String role;
}
