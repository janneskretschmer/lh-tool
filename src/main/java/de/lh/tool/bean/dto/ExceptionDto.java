package de.lh.tool.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionDto {
	private String key;
	private String message;
	private int httpCode;
}
