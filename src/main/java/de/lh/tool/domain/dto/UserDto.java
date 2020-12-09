package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Identifiable<Long> {
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

}
