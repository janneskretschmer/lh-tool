package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import lombok.NonNull;

public interface UserRoleService extends BasicEntityService<UserRole, Long> {

	boolean hasCurrentUserRight(String right);

	void checkCurrentUserRight(String right) throws DefaultException;

	boolean hasUserRight(User user, String right);

	boolean hasCurrentUserRightToGrantRole(String role);

	boolean hasCurrentUserRightToGrantAllRoles(@NonNull User user);

}
