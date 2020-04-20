package de.lh.tool.domain.dto.assembled;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssembledHelperTypeDto {

	private Long id;

	private String name;

	private List<AssembledProjectHelperTypeDto> shifts;

}
