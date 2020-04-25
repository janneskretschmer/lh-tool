package de.lh.tool.service.rest.testonly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailWrapperDto {
	private List<EmailDto> emails;
}
