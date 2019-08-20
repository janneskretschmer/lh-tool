package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.StoreProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.StoreProject;
import de.lh.tool.repository.StoreProjectRepository;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.StoreProjectService;
import de.lh.tool.service.entity.interfaces.StoreService;

@Service
public class StoreProjectServiceImpl
		extends BasicMappableEntityServiceImpl<StoreProjectRepository, StoreProject, StoreProjectDto, Long>
		implements StoreProjectService {

	@Autowired
	private StoreService storeService;
	@Autowired
	private ProjectService projectService;

	@Override
	public Collection<StoreProjectDto> findDtosByStoreId(Long storeId) throws DefaultException {
		return convertToDtoList(getRepository().findByStore_Id(
				Optional.ofNullable(storeId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED))));
	}

	@Override
	public StoreProject convertToEntity(StoreProjectDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<StoreProjectDto, StoreProject>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((StoreProjectDto) c.getSource()).getStoreId())
						.map(storeService::findById).orElse(null)).map(source).setStore(null);
				using(c -> Optional.ofNullable(((StoreProjectDto) c.getSource()).getProjectId())
						.map(projectService::findById).orElse(null)).map(source).setProject(null);
			}
		});
		return modelMapper.map(dto, StoreProject.class);
	}

}
