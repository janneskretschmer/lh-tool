package de.lh.tool.domain.dto.assembled;

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
public class AssembledProjectHelperTypeDto {

	private Long id;

	private Long projectId;

	private Long helperTypeId;

	private Integer weekday;

	private String startTime;

	private String endTime;

	private AssembledNeedDto need;

}
