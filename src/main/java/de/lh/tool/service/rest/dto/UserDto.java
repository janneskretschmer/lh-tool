package de.lh.tool.service.rest.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDto {
	@Getter
	@Setter
	private Long id;

	@Getter
	@Setter
	private String firstName;

	@Getter
	@Setter
	private String lastName;

	@Getter
	@Setter
	private String gender;

	@Getter
	@Setter
	private String passwordHash;

	@Getter
	@Setter
	private String passwordSalt;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private String telephoneNumber;

	@Getter
	@Setter
	private String mobileNumber;

	@Getter
	@Setter
	private String businessNumber;

	@Getter
	@Setter
	private String profession;

	@Getter
	@Setter
	private String skills;

}
