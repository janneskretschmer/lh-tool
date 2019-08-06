package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.model.StoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreDto {
	private Long id;
	private StoreType type;
	private String name;
	private String address;

}
