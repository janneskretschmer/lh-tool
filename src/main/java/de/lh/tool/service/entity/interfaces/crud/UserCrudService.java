package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.JwtAuthenticationDto;
import de.lh.tool.domain.dto.LoginDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.User;

public interface UserCrudService extends BasicEntityCrudService<User, UserDto, Long> {

	UserDto changePassword(Long userId, String token, String oldPassword, String newPassword, String confirmPassword)
			throws DefaultException;

	void requestPasswordReset(String email) throws DefaultException;

	JwtAuthenticationDto login(LoginDto loginDto);

	List<UserDto> findDtosByProjectIdAndRoleIgnoreCase(Long projectId, String role, String freeText)
			throws DefaultException;

	UserDto findCurrentUserDto() throws DefaultException;

}
