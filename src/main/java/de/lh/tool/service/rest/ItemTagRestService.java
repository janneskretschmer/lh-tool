package de.lh.tool.service.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.ItemTagCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.ITEM_TAG_PREFIX)
public class ItemTagRestService {

	@Autowired
	private ItemTagCrudService itemTagService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of item tags")
	@Secured(UserRole.RIGHT_ITEM_TAGS_GET)
	public List<ItemTagDto> getItemTags() throws DefaultException {

		return itemTagService.findDtos();
	}

}
