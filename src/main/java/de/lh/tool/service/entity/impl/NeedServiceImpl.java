package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.NeedRepository;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.UserRoleService;

@Service
public class NeedServiceImpl extends BasicMappableEntityServiceImpl<NeedRepository, Need, NeedDto, Long>
		implements NeedService {
	@Autowired
	UserRoleService userRoleService;

	@Autowired
	ProjectService projectService;

	@Override
	@Transactional
	public List<NeedDto> getNeedDtos() {
		return convertToDtoList(
				userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_GET_FOREIGN) ? (Collection<Need>) findAll()
						: StreamSupport.stream(findAll().spliterator(), false)
								.filter(n -> projectService.isOwnProject(n.getProject())).collect(Collectors.toList()));
	}

	@Override
	@Transactional
	public NeedDto getNeedDtoById(Long id) throws DefaultException {
		Need need = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (projectService.isOwnProject(need.getProject())
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_GET_FOREIGN)) {
			return convertToDto(need);
		}
		throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
	}

	@Override
	@Transactional
	public NeedDto saveNeedDto(NeedDto needDto) throws DefaultException {
		if (needDto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Need need = convertToEntity(needDto);
		if (!projectService.isOwnProject(need.getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		need = save(need);
		return convertToDto(need);
	}

	@Override
	@Transactional
	public void deleteOwn(Long id) throws DefaultException {
		Need need = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (!projectService.isOwnProject(need.getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		delete(need);
	}

	@Override
	public Need convertToEntity(NeedDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<NeedDto, Need>() {
			@Override
			protected void configure() {
				using(c -> ((NeedDto) c.getSource()).getProjectId() != null
						? projectService.findById(((NeedDto) c.getSource()).getProjectId()).orElse(null)
						: null).map(source).setProject(null);
			}
		});
		return modelMapper.map(dto, Need.class);
	}
}
