package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.UserRoleDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import lombok.NonNull;

public interface UserRoleCrudService extends BasicEntityCrudService<UserRole, UserRoleDto, Long> {

	List<UserRoleDto> findDtosByUserId(@NonNull Long userId) throws DefaultException;

}
