package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.RoleDto;

public interface RoleService {

	List<RoleDto> getGrantableRoleDtos();

}
