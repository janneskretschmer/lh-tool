package de.lh.tool.service.entity.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemImageDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemImage;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemImageRepository;
import de.lh.tool.service.entity.interfaces.ItemImageService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.crud.ItemImageCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemImageServiceImpl extends BasicEntityCrudServiceImpl<ItemImageRepository, ItemImage, ItemImageDto, Long>
		implements ItemImageService, ItemImageCrudService {

	@Autowired
	private ItemService itemService;

	@Override
	@Transactional
	public ItemImageDto findDtoByItemId(@NonNull Long itemId) throws DefaultException {
		Item item = itemService.findByIdOrThrowInvalidIdException(itemId);
		ItemImage itemImage = getRepository().findByItem(item).orElse(ItemImage.builder().item(item).build());
		checkFindPermission(itemImage);
		return convertToDto(itemImage);
	}

	@Override
	@Transactional
	public ItemImageDto createDto(@NonNull ItemImageDto dto, @NonNull Long itemId) throws DefaultException {
		dto.setItemId(itemId);
		return createDto(dto);
	}

	@Override
	@Transactional
	public ItemImageDto updateDto(@NonNull ItemImageDto dto, @NonNull Long id, @NonNull Long itemId)
			throws DefaultException {
		dto.setId(id);
		dto.setItemId(itemId);

		return updateDto(dto, id);
	}

	@Override
	protected void checkValidity(@NonNull ItemImage itemImage) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemImage.getItem());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_IMAGE, itemImage.getImage());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_MEDIA_TYPE, itemImage.getMediaType());
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEMS_PREFIX;
	}

	@Override
	public boolean hasReadPermission(@NonNull ItemImage entity) {
		return itemService.hasReadPermission(entity.getItem());
	}

	@Override
	public boolean hasWritePermission(@NonNull ItemImage entity) {
		return hasReadPermission(entity);
	}

}
