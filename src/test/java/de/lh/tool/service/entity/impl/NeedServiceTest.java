package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.Date;
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
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectHelperType;
import de.lh.tool.service.entity.interfaces.ProjectHelperTypeService;

@ExtendWith(MockitoExtension.class)
public class NeedServiceTest {

	@Mock
	private ProjectHelperTypeService projectHelperTypeService;

	@InjectMocks
	private NeedServiceImpl needService;

	@BeforeEach
	public void before() {
		MockitoAnnotations.initMocks(needService);
	}

	@Test
	public void testConvertToEntity() {
		Mockito.when(projectHelperTypeService.findById(Mockito.anyLong()))
				.then(i -> Long.valueOf(1l).equals(i.getArgument(0))
						? Optional.of(ProjectHelperType.builder().id(1l).project(new Project())
								.helperType(new HelperType()).weekday(1).build())
						: Optional.empty());
		assertNull(needService.convertToEntity(NeedDto.builder().projectHelperTypeId(null).build())
				.getProjectHelperType());
		assertNull(
				needService.convertToEntity(NeedDto.builder().projectHelperTypeId(2l).build()).getProjectHelperType());
		assertEquals(Long.valueOf(1l), needService.convertToEntity(NeedDto.builder().projectHelperTypeId(1l).build())
				.getProjectHelperType().getId());

		assertNull(needService.convertToEntity(NeedDto.builder().date(null).build()).getDate());
		assertEquals("2020-02-02",
				needService
						.convertToEntity(NeedDto.builder().date(Date.from(Instant.ofEpochSecond(1580601600))).build())
						.getDate().toString());
	}

}
