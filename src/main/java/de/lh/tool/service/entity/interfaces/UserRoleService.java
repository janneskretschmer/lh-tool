package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;

public interface UserRoleService extends BasicEntityService<UserRole, Long> {

	boolean hasCurrentUserRight(String right);

	boolean hasUserRight(User user, String right);

	boolean hasCurrentUserRightToGrantRole(String role);

	/**
	 * @throws DefaultException(EX_FORBIDDEN) if user doesn't have right
	 */
	void checkCurrentUserRight(String right) throws DefaultException;

}
