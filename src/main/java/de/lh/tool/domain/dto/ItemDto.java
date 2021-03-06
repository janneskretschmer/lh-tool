package de.lh.tool.domain.dto;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.domain.Patchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto implements Patchable, Identifiable<Long> {
	private Long id;
	private Long slotId;
	private String identifier;
	private Boolean hasBarcode;
	private String name;
	private String description;
	private Double quantity;
	private String unit;
	private Boolean outsideQualified;
	private Boolean consumable;
	private Boolean broken;
	private Long technicalCrewId;

	@Override
	public boolean hasNonNullField() {
		// id not included, because it shouldn't change in a patch
		return ObjectUtils.anyNotNull(slotId, identifier, hasBarcode, name, description, quantity, unit,
				outsideQualified, consumable, broken, technicalCrewId);
	}
}
