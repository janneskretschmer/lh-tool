package de.lh.tool.domain.dto;

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserDto implements Identifiable<Long> {

	private Long id;
	private Long projectId;
	private Long userId;
}
