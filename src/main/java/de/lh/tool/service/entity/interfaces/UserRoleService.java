package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;

public interface UserRoleService extends BasicEntityService<UserRole, Long> {

	boolean hasCurrentUserRight(String right);

	boolean hasUserRight(User user, String right);

}
