package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectHelperTypeDto implements Identifiable<Long> {

	private Long id;

	private Long projectId;

	private Long helperTypeId;

	private Integer weekday;

	private String startTime;

	private String endTime;

}
