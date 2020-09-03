import { apiEndpoints, apiRequest } from '../apiclient';
import { DESCRIPTION_VARIABLE, FREE_TEXT_VARIABLE, ID_VARIABLE, NAME_VARIABLE, STORE_ID_VARIABLE } from '../urlmappings';

export function fetchSlotsByStore(accessToken, freeText, name, description, storeId) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.slot.getByStore,
            authToken: accessToken,
            queries: {
                [FREE_TEXT_VARIABLE]: freeText,
                [NAME_VARIABLE]: name,
                [DESCRIPTION_VARIABLE]: description,
                [STORE_ID_VARIABLE]: storeId,
            }
        }).then(result => result.response.content);
    } else {
        return Promise.resolve([]);
    }
}

export function fetchSlot(accessToken, slotId) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.slot.getById,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: slotId }
        })
            .then(result => result.response);
    } else {
        return Promise.resolve(null);
    }
}

export function createSlot(accessToken, slot) {
    return apiRequest({
        apiEndpoint: apiEndpoints.slot.createNew,
        authToken: accessToken,
        data: slot,
    })
        .then(result => result.response);
}

export function updateSlot(accessToken, slot) {
    return apiRequest({
        apiEndpoint: apiEndpoints.slot.update,
        authToken: accessToken,
        data: slot,
        parameters: { [ID_VARIABLE]: slot.id },
    })
        .then(result => result.response);
}

export function deleteSlot(accessToken, slotId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.slot.delete,
        authToken: accessToken,
        parameters: { [ID_VARIABLE]: slotId },
    });
}