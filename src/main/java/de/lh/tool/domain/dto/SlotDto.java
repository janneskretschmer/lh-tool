package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlotDto {
	private Long id;
	private Store store;
	private String name;
	private String description;
	private Float width;
	private Float height;
	private Float depth;
	private Boolean outside;
}
