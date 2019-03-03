package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;

public interface MailService {

	void sendPwResetMail(User user, PasswordChangeToken passwordChangeToken);

	void sendNewLocalCoordinatorMail(User user, PasswordChangeToken passwordChangeToken);

	void sendNewPublisherMail(User user, PasswordChangeToken passwordChangeToken);

}
