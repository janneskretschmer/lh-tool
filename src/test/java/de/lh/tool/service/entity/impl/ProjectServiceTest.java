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

import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.User;
import de.lh.tool.service.entity.interfaces.ProjectUserService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private UserRoleService userRoleService;

	@Mock
	private ProjectUserService projectUserService;

	@InjectMocks
	private ProjectServiceImpl projectService;

	@BeforeEach
	public void before() {
		MockitoAnnotations.initMocks(projectService);
	}

	@Test
	public void testIsOwnProject() {
		assertFalse(projectService.isOwnProject(null));
		assertFalse(projectService.isOwnProject(new Project()));
		Mockito.when(userService.getCurrentUser()).thenReturn(User.builder().email("email").build());
		assertFalse(projectService.isOwnProject(new Project()));
		assertFalse(projectService
				.isOwnProject(Project.builder().users(List.of(User.builder().email("other").build())).build()));
		assertTrue(projectService
				.isOwnProject(Project.builder().users(List.of(User.builder().email("email").build())).build()));

	}

}
