package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemItemTagRepository;
import de.lh.tool.service.entity.interfaces.ItemItemTagService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.crud.ItemItemTagCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemItemTagServiceImpl
		extends BasicEntityCrudServiceImpl<ItemItemTagRepository, ItemItemTag, ItemItemTagDto, Long>
		implements ItemItemTagService, ItemItemTagCrudService {

	@Autowired
	private ItemService itemService;

	@Override
	protected void checkValidity(@NonNull ItemItemTag itemItemTag) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemItemTag.getItem());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_TAG_ID, itemItemTag.getItemTag());
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_ITEM_ITEM_TAG_ALREADY_EXISTS,
				getRepository().findByItemAndItemTag(itemItemTag.getItem(), itemItemTag.getItemTag()), itemItemTag);
	}

	// FUTURE: use deleteDtoById in REST-service
	@Override
	@Transactional
	public void deleteIfExists(@NonNull Item item, @NonNull ItemTag itemTag) throws DefaultException {
		Optional<Long> optId = getRepository().findByItemAndItemTag(item, itemTag).map(ItemItemTag::getId);
		if (optId.isPresent()) {
			deleteDtoById(optId.get());
		}
	}

	@Override
	@Transactional
	public List<ItemItemTag> findByItemTag(ItemTag itemTag) {
		return getRepository().findByItemTag(itemTag);
	}

	@Override
	@Transactional
	public List<ItemItemTag> findByItem(Item item) {
		return getRepository().findByItem(item);
	}

	@Override
	public boolean hasReadPermission(@NonNull ItemItemTag itemItemTag) {
		return itemService.hasReadPermission(itemItemTag.getItem());
	}

	@Override
	public boolean hasWritePermission(@NonNull ItemItemTag itemItemTag) {
		return hasReadPermission(itemItemTag);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEMS_ITEM_TAGS_PREFIX;
	}

}
