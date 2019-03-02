package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.Need;
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

	@Test
	public void testCreateNeedIndex() {
		Date today = new Date();

		Map<Date, Map<HelperType, Need>> index = needService.createNeedIndex(List.of(
				Need.builder().date(today).helperType(HelperType.CONSTRUCTION_WORKER).quantity(1).build(),
				Need.builder().date(today).helperType(HelperType.KITCHEN_HELPER).quantity(2).build(), Need.builder()
						.date(DateUtils.addDays(today, 1)).helperType(HelperType.KITCHEN_HELPER).quantity(3).build()));
		assertEquals(Integer.valueOf(1), index.get(today).get(HelperType.CONSTRUCTION_WORKER).getQuantity());
		assertEquals(Integer.valueOf(2), index.get(today).get(HelperType.KITCHEN_HELPER).getQuantity());
		assertEquals(Integer.valueOf(3),
				index.get(DateUtils.addDays(today, 1)).get(HelperType.KITCHEN_HELPER).getQuantity());

	}

}
