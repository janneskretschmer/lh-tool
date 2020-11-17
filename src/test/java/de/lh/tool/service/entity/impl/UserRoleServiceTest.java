package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.UserService;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserRoleServiceImpl userRoleService;

	@BeforeEach
	public void before() {
		MockitoAnnotations.initMocks(userRoleService);
	}

	@Test
	public void testHasCurrentUserRight() {
		assertFalse(userRoleService.hasCurrentUserRight(UserRole.ROLE_ADMIN));
		Mockito.when(userService.getCurrentUser()).thenReturn(new User());
		assertFalse(userRoleService.hasCurrentUserRight(UserRole.ROLE_ADMIN));
		Mockito.when(userService.getCurrentUser())
				.thenReturn(User.builder().roles(List.of(new UserRole("ROLE_ADMIN"))).build());
		assertFalse(userRoleService.hasCurrentUserRight("RIGHT"));
		assertFalse(userRoleService.hasCurrentUserRight(UserRole.ROLE_CONSTRUCTION_SERVANT));
		assertTrue(userRoleService.hasCurrentUserRight(UserRole.ROLE_ADMIN));
		assertTrue(userRoleService.hasCurrentUserRight(UserRole.RIGHT_USERS_POST));
	}
}
