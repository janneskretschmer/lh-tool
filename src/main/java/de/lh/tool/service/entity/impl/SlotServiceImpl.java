package de.lh.tool.service.entity.impl;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.model.Slot;
import de.lh.tool.repository.SlotRepository;
import de.lh.tool.service.entity.interfaces.SlotService;

public class SlotServiceImpl extends BasicMappableEntityServiceImpl<SlotRepository, Slot, SlotDto, Long>
		implements SlotService {

}
