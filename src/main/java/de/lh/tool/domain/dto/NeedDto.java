package de.lh.tool.domain.dto;

import java.util.Date;

import de.lh.tool.domain.model.HelperType;
import lombok.Data;

@Data
public class NeedDto {

	private Long id;

	private Long projectId;

	private Date date;

	private Integer quantity;

	private HelperType helperType;

}
