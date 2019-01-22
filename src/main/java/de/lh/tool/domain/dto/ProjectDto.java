package de.lh.tool.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ProjectDto {

	private Long id;

	private String name;

	private Date startDate;

	private Date endDate;

}
