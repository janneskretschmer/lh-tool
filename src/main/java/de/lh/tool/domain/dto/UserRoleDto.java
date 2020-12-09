package de.lh.tool.domain.dto;

import de.lh.tool.domain.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDto implements Identifiable<Long> {
	private Long id;
	private Long userId;
	private String role;
}
