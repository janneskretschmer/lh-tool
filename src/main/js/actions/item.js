import { apiEndpoints, apiRequest } from '../apiclient';
import { ID_VARIABLE } from '../urlmappings';
import moment from 'moment';

export function fetchItems(accessToken) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.item.get,
            authToken: accessToken
        }).then(result => result.response.content)
            .catch(e => {
                console.log(e);
                return Promise.resolve([]);
            });
    } else {
        return Promise.resolve([]);
    }
}

export function fetchItem({ accessToken, itemId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.item.getById,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: itemId }
        })
            .then(result => result.response)
            // TODO Proper error message
            .catch(e => {
                console.log(e);
                return Promise.resolve(null);
            });
    } else {
        return Promise.resolve(null);
    }
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

export function fetchItemNotes({ accessToken, itemId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.item.getNotes,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: itemId }
        })
            .then(result => result.response.content.map(note => ({
                ...note,
                timestamp: moment(note.timestamp, 'x'),
            })))
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function createItemNote({ accessToken, itemNote, handleFailure }) {
    return apiRequest({
        apiEndpoint: /*item.id ? apiEndpoints.item.update :*/ apiEndpoints.item.createNotes,
        authToken: accessToken,
        data: itemNote,
        parameters: { [ID_VARIABLE]: itemNote.itemId }
    })
        .then(result => ({
            ...result.response,
            timestamp: moment(result.response.timestamp, 'x'),
        }))
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}

export function fetchItemTags({ accessToken, itemId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.item.getTags,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: itemId }
        })
            .then(result => result.response.content)
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function fetchItemHistory({ accessToken, itemId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.item.getHistory,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: itemId }
        })
            .then(result => result.response.content.map(tmp => ({
                ...tmp,
                timestamp: moment(tmp.timestamp, 'x'),
                data: tmp.data ? JSON.parse(tmp.data) : null,
            })))
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}