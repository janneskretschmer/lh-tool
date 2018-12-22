package de.lh.tool.domain.dto;

import lombok.Data;

@Data
public class UserDto {
	private Long id;

	private String firstName;

	private String lastName;

	private String gender;

	private String email;

	private String telephoneNumber;

	private String mobileNumber;

	private String businessNumber;

	private String profession;

	private String skills;

	private Boolean active;

}
