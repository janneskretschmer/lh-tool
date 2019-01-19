package de.lh.tool.domain.dto;

import de.lh.tool.domain.model.HelperType;
import lombok.Data;

@Data
public class NeedDto {

	private Long id;

	private Long projectId;

	private Long date;

	private Integer quantity;

	private HelperType helperType;

}
