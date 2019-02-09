package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.model.Project;
import de.lh.tool.service.entity.interfaces.ProjectService;

@ExtendWith(MockitoExtension.class)
public class NeedServiceTest {

	@Mock
	private ProjectService projectService;

	// @Mock private UserRoleService userRoleService;

	// @Mock private ProjectUserService projectUserService;

	@InjectMocks
	private NeedServiceImpl needService;

	@BeforeEach
	public void before() {
		MockitoAnnotations.initMocks(needService);
	}

	@Test
	public void testConvertToEntity() {
		Mockito.when(projectService.findById(Mockito.anyLong()))
				.then(i -> Long.valueOf(1l).equals(i.getArgument(0)) ? Optional.of(Project.builder().id(1l).build())
						: Optional.empty());
		assertNull(needService.convertToEntity(NeedDto.builder().projectId(null).build()).getProject());
		assertNull(needService.convertToEntity(NeedDto.builder().projectId(2l).build()).getProject());
		assertEquals(Long.valueOf(1l),
				needService.convertToEntity(NeedDto.builder().projectId(1l).build()).getProject().getId());

	}

}
