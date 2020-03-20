package de.lh.tool.domain.dto.assembled;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.dto.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssembledNeedUserDto {

	private Long id;
	private Long needId;
	private Long userId;
	private UserDto user;
}
