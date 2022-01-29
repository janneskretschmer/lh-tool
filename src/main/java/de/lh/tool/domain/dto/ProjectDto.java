package de.lh.tool.domain.dto;

import java.time.LocalDate;

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
public class ProjectDto implements Identifiable<Long> {

	private Long id;

	private String name;

	private LocalDate startDate;

	private LocalDate endDate;

}
