import { apiEndpoints, apiRequest, createEntity, deleteEntity, fetchEntity, updateEntity } from '../apiclient';
import { SlotDto } from '../types/generated';
import { DESCRIPTION_VARIABLE, FREE_TEXT_VARIABLE, ID_VARIABLE, NAME_VARIABLE, STORE_ID_VARIABLE } from '../urlmappings';

export function fetchSlotsByFreeTextAndNameAndDescriptionAndStore(accessToken: string, freeText?: string, name?: string, description?: string, storeId?: number): Promise<SlotDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.slot.getByStore,
        authToken: accessToken,
        queries: {
            [FREE_TEXT_VARIABLE]: freeText,
            [NAME_VARIABLE]: name,
            [DESCRIPTION_VARIABLE]: description,
            [STORE_ID_VARIABLE]: storeId?.toString(),
        }
    });
}

export function fetchSlot(accessToken: string, slotId: number): Promise<SlotDto> {
    return fetchEntity<SlotDto>(apiEndpoints.slot.getById, accessToken, slotId);
}

export function createSlot(accessToken: string, slot: SlotDto): Promise<SlotDto> {
    return createEntity<SlotDto>(apiEndpoints.slot.createNew, accessToken, slot);
}

export function updateSlot(accessToken: string, slot: SlotDto): Promise<SlotDto> {
    return updateEntity<SlotDto>(apiEndpoints.slot.update, accessToken, slot);
}

export function deleteSlot(accessToken: string, slotId: number): Promise<void> {
    return deleteEntity(apiEndpoints.slot.delete, accessToken, slotId);
}