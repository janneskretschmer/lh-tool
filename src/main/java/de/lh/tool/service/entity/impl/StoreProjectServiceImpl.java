package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public Collection<StoreProjectDto> findDtosByStoreId(Long storeId) throws DefaultException {
		return convertToDtoList(getRepository().findByStore_Id(
				Optional.ofNullable(storeId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED))));
	}

	@Override
	@Transactional
	public Collection<StoreProjectDto> bulkDeleteAndCreateByStoreId(Long storeId, Collection<StoreProjectDto> dtos)
			throws DefaultException {
		getRepository().deleteByStore_Id(
				Optional.ofNullable(storeId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED)));
		entityManager.flush();

		List<StoreProject> result = Optional.ofNullable(dtos).map(Collection::stream).map(
				stream -> stream.map(this::convertToEntity).map(getRepository()::save).collect(Collectors.toList()))
				.orElse(List.of());

		return convertToDtoList(result);
	}

	@Override
	public StoreProject convertToEntity(StoreProjectDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<StoreProjectDto, StoreProject>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((StoreProjectDto) c.getSource()).getStoreId())
						.flatMap(storeService::findById).orElse(null)).map(source).setStore(null);
				using(c -> Optional.ofNullable(((StoreProjectDto) c.getSource()).getProjectId())
						.flatMap(projectService::findById).orElse(null)).map(source).setProject(null);
			}
		});
		return modelMapper.map(dto, StoreProject.class);
	}

}
