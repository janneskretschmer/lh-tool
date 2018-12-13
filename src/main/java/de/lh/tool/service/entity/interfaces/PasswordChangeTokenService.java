package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;

public interface PasswordChangeTokenService extends BasicEntityService<PasswordChangeToken, Long> {

	PasswordChangeToken saveRandomToken(User user);
}
