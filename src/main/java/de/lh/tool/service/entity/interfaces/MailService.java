package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.NeedUser;
import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;

public interface MailService {

	void sendPwResetMail(User user, PasswordChangeToken passwordChangeToken);

	void sendNeedUserStateChangedMailToUser(NeedUser needUser);

	void sendNeedUserStateChangedMailToCoordinator(NeedUser needUser, User coordinator);

	void sendUserCreatedMail(User user, PasswordChangeToken passwordChangeToken);

}
