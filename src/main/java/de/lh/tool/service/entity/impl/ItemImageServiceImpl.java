package de.lh.tool.service.entity.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemImageDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemImage;
import de.lh.tool.repository.ItemImageRepository;
import de.lh.tool.service.entity.interfaces.ItemImageService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.util.ValidationUtil;

@Service
public class ItemImageServiceImpl extends
		BasicMappableEntityServiceImpl<ItemImageRepository, ItemImage, ItemImageDto, Long> implements ItemImageService {

	@Autowired
	private ItemService itemService;

	@Override
	@Transactional
	public ItemImageDto findDtoByItemId(Long itemId) throws DefaultException {
		ValidationUtil.checkIdsNonNull(itemId);
		Item item = itemService.findById(itemId).orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!itemService.isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		ItemImage itemImage = getRepository().findByItem(item).orElse(ItemImage.builder().item(item).build());
		return convertToDto(itemImage);
	}

	@Override
	@Transactional
	public ItemImageDto createDto(Long itemId, ItemImageDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		dto.setItemId(ObjectUtils.defaultIfNull(itemId, dto.getItemId()));
		Item item = itemService.findById(dto.getItemId())
				.orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!itemService.isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		ItemImage itemImage = save(convertToEntity(dto));

		return convertToDto(itemImage);
	}

	@Override
	@Transactional
	public ItemImageDto updateDto(Long itemId, Long id, ItemImageDto dto) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		dto.setItemId(ObjectUtils.defaultIfNull(itemId, dto.getItemId()));
		ValidationUtil.checkIdsNonNull(dto.getId(), dto.getItemId());

		ItemImage itemImage = findById(dto.getId()).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		if (!itemService.isViewAllowed(itemImage.getItem())) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		itemImage.setMediaType(dto.getMediaType());
		itemImage.setImage(dto.getImage());

		return convertToDto(itemImage);
	}

	@Override
	public ItemImage convertToEntity(ItemImageDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ItemImageDto, ItemImage>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((ItemImageDto) c.getSource()).getItemId())
						.flatMap(itemService::findById).orElse(null)).map(source).setItem(null);
			}
		});
		return modelMapper.map(dto, ItemImage.class);
	}

}
