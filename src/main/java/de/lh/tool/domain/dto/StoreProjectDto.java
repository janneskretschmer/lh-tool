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
public class StoreProjectDto implements Identifiable<Long> {

	private Long id;

	private Long storeId;

	private Long projectId;

	private LocalDate start;

	private LocalDate end;

}
