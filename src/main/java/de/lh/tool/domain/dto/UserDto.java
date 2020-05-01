package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
