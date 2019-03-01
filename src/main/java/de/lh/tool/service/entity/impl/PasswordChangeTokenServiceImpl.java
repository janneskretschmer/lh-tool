package de.lh.tool.service.entity.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.repository.PasswordChangeTokenRepository;
import de.lh.tool.service.entity.interfaces.PasswordChangeTokenService;

@Service
public class PasswordChangeTokenServiceImpl
		extends BasicEntityServiceImpl<PasswordChangeTokenRepository, PasswordChangeToken, Long>
		implements PasswordChangeTokenService {

	@Override
	public PasswordChangeToken saveRandomToken(User user) {
		PasswordChangeToken token = new PasswordChangeToken();
		token.setToken(RandomStringUtils.randomAlphanumeric(PasswordChangeToken.TOKEN_LENGTH));
		token.setUser(user);
		token.setId(getRepository().findByUser_Id(user.getId()).map(PasswordChangeToken::getId).orElse(null));
		return save(token);
	}

}
