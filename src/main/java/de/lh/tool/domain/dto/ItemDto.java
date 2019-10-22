package de.lh.tool.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto {
	private Long id;
	private Long slotId;
	private String identifier;
	private Boolean hasBarcode;
	private String name;
	private String description;
	private Double quantity;
	private String unit;
	private Float width;
	private Float height;
	private Float depth;
	private Boolean outsideQualified;
	private Boolean consumable;
	private Boolean broken;
	private String picture_url;
}
