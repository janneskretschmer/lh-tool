package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.TagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Tag;
import de.lh.tool.repository.TagRepository;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.TagService;

@Service
public class TagServiceImpl extends BasicMappableEntityServiceImpl<TagRepository, Tag, TagDto, Long>
		implements TagService {

	@Autowired
	private ItemService itemService;

	@Override
	@Transactional
	public List<TagDto> getTagDtosByItemId(Long itemId) throws DefaultException {
		return convertToDtoList(itemService.findById(itemId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID)).getTags());
	}

	@Override
	@Transactional
	public TagDto getTagDtoById(Long id) throws DefaultException {
		// Tag tag = findById(id).orElseThrow(() -> new
		// DefaultException(ExceptionEnum.EX_INVALID_ID));

		return convertToDto(null);
	}

	@Override
	@Transactional
	public TagDto createTagDto(TagDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Tag tag = save(convertToEntity(dto));
		return convertToDto(tag);
	}

	@Override
	@Transactional
	public TagDto updateTagDto(TagDto dto, Long id) throws DefaultException {
//		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
//		if (dto.getId() == null) {
//			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
//		}
//		Tag tag = save(convertToEntity(dto));
//		return convertToDto(tag);
		return null;
	}

}
