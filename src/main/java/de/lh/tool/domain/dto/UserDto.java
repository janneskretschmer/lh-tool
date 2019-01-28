package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
