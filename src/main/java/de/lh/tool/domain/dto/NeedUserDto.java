package de.lh.tool.domain.dto;

import de.lh.tool.domain.model.NeedUserState;
import lombok.Data;

@Data
public class NeedUserDto {

	private Long id;
	private Long needId;
	private Long userId;
	private NeedUserState state;
}
