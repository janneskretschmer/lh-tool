import { apiEndpoints, apiRequest } from '../apiclient';
import { ID_VARIABLE, SLOT_STORE_VARIABLE } from '../urlmappings';
import moment from 'moment';

export function fetchSlotsByStore(accessToken, storeId) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.slot.getByStore,
            authToken: accessToken,
            queries: {
                [SLOT_STORE_VARIABLE]: storeId,
            }
        }).then(result => result.response.content);
    } else {
        return Promise.resolve([]);
    }
}

export function fetchSlot({ accessToken, slotId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.slot.getById,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: slotId }
        })
            .then(result => result.response)
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve(null);
    }
}

export function createOrUpdateSlot({ accessToken, slot, handleFailure }) {
    return apiRequest({
        apiEndpoint: slot.id ? apiEndpoints.slot.update : apiEndpoints.slot.createNew,
        authToken: accessToken,
        data: slot,
        parameters: slot.id ? { [ID_VARIABLE]: slot.id } : {},
    })
        .then(result => result.response)
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}