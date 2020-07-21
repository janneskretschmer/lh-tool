package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;
import de.lh.tool.repository.ItemItemTagRepository;
import de.lh.tool.service.entity.interfaces.ItemItemTagService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemItemTagServiceImpl
		extends BasicMappableEntityServiceImpl<ItemItemTagRepository, ItemItemTag, ItemItemTagDto, Long>
		implements ItemItemTagService {

	@Override
	@Transactional
	public ItemItemTag createIfNotExists(@NonNull Item item, @NonNull ItemTag itemTag) throws DefaultException {
		ValidationUtil.checkIdsNonNull(item.getId(), itemTag.getId());
		return getRepository().findByItemAndItemTag(item, itemTag)
				.orElseGet(() -> save(ItemItemTag.builder().item(item).itemTag(itemTag).build()));
	}

	@Override
	@Transactional
	public void deleteIfExists(@NonNull Item item, @NonNull ItemTag itemTag) throws DefaultException {
		ValidationUtil.checkIdsNonNull(item.getId(), itemTag.getId());
		getRepository().findByItemAndItemTag(item, itemTag).ifPresent(this::delete);
	}

	@Override
	@Transactional
	public List<ItemItemTag> findByItemTag(ItemTag itemTag) {
		return getRepository().findByItemTag(itemTag);
	}

}
