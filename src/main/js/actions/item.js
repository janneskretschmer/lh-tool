import { apiEndpoints, apiRequest } from '../apiclient';
import { ID_VARIABLE, ITEM_ID_VARIABLE, NOTE_ID_VARIABLE } from '../urlmappings';
import moment from 'moment';
import { result } from 'lodash';

export function fetchItems(accessToken) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.item.get,
            authToken: accessToken
        }).then(result => result.response.content);
    } else {
        return Promise.resolve([]);
    }
}

export function fetchItem(accessToken, itemId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getById,
        authToken: accessToken,
        parameters: { [ID_VARIABLE]: itemId }
    })
        .then(result => result.response);
}

export function createOrUpdateItem({ accessToken, item, handleFailure }) {
    return apiRequest({
        apiEndpoint: item.id ? apiEndpoints.item.update : apiEndpoints.item.createNew,
        authToken: accessToken,
        data: item,
        parameters: item.id ? { [ID_VARIABLE]: item.id } : {},
    })
        .then(result => result.response)
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}

export function updateItem(accessToken, item) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.update,
        authToken: accessToken,
        data: item,
        parameters: { [ID_VARIABLE]: item.id },
    }).then(result => result.response);
}

export function updateItemBrokenState(accessToken, itemId, broken) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.patch,
        authToken: accessToken,
        data: { broken },
        parameters: { [ID_VARIABLE]: itemId },
    })
        .then(result => result.response.broken);
}

export function updateItemSlot(accessToken, itemId, slotId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.patch,
        authToken: accessToken,
        data: { slotId },
        parameters: { [ID_VARIABLE]: itemId },
    })
        .then(result => result.response.slotId);
}



export function fetchItemNotes(accessToken, itemId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getNotes,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId }
    })
        .then(result => result.response.content.map(note => ({
            ...note,
            timestamp: moment(note.timestamp, moment.ISO_8601).utc(true),
        })));
}

export function fetchItemNotesUser(accessToken, { itemId, id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getNotesUser,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId,
            [NOTE_ID_VARIABLE]: id,
        },
    })
        .then(result => result.response);
}

export function createItemNote(accessToken, itemNote) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.createNotes,
        authToken: accessToken,
        data: itemNote,
        parameters: { [ITEM_ID_VARIABLE]: itemNote.itemId }
    })
        .then(result => ({
            ...result.response,
            timestamp: moment(result.response.timestamp, moment.ISO_8601).utc(true),
        }));
}

export function deleteItemNote(accessToken, { itemId, id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.deleteNotes,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId,
            [NOTE_ID_VARIABLE]: id,
        },
    });
}

export function fetchItemTagsByItem(accessToken, itemId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getTags,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId }
    })
        .then(result => result.response.content);
}

export function createItemTag(accessToken, itemId, itemTag) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.createTag,
        authToken: accessToken,
        data: itemTag,
        parameters: { [ITEM_ID_VARIABLE]: itemId }
    })
        .then(result => result.response);
}

export function deleteItemTag(accessToken, itemId, itemTagId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.deleteTag,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId, [ID_VARIABLE]: itemTagId }
    });
}

export function fetchItemHistory(accessToken, itemId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getHistory,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId }
    })
        .then(result => result.response.content.map(tmp => ({
            ...tmp,
            timestamp: moment(tmp.timestamp, moment.ISO_8601).utc(true),
            data: tmp.data ? JSON.parse(tmp.data) : null,
        })));
}


export function fetchItemHistoryUser(accessToken, { itemId, id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getHistoryUser,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId,
            [ID_VARIABLE]: id,
        },
    })
        .then(result => result.response);
}