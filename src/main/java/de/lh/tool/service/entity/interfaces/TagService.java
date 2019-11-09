package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.TagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Tag;

public interface TagService extends BasicEntityService<Tag, Long> {

	TagDto getTagDtoById(Long id) throws DefaultException;

	TagDto createTagDto(TagDto dto) throws DefaultException;

	TagDto updateTagDto(TagDto dto, Long id) throws DefaultException;

	List<TagDto> getTagDtosByItemId(Long itemId) throws DefaultException;

}
