package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;

public interface MailService {

	void sendNewLocalCoordinatorMail(User user);

	void sendNewPublisherMail(User user);

	void sendPwResetMail(User user, PasswordChangeToken passwordChangeToken);

}
