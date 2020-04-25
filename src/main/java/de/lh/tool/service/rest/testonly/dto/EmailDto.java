package de.lh.tool.service.rest.testonly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
	private String recipient;
	private String subject;
	private String content;
}
