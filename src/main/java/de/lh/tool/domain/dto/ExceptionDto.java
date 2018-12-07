package de.lh.tool.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionDto {
	private String key;
	private String message;
	private int httpCode;
}
