package de.lh.tool.rest.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTest {
	private String recipient;
	private String subjectRegex;
	private String contentRegex;
}
