import { apiEndpoints, apiRequest } from '../apiclient';
import { ID_VARIABLE } from '../urlmappings';
import moment from 'moment';

export function fetchOwnStores(accessToken) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.store.get,
            authToken: accessToken
        }).then(result => result.response.content)
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function fetchStore({ accessToken, storeId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.store.getById,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: storeId }
        })
            .then(result => result.response)
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve(null);
    }
}

export function fetchStoreProjects({ accessToken, storeId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.store.getProjects,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: storeId }
        })
            .then(result => result.response.content.map(storeProject => ({
                ...storeProject,
                start: moment(storeProject.start, 'x'),
                end: moment(storeProject.end, 'x'),
            })))
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function deleteAndCreateStoreProjects({ accessToken, storeId, storeProjects }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.store.setProjects,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: storeId },
            data: storeProjects,
        })
            .then(result => result.response.content.map(storeProject => ({
                ...storeProject,
                start: moment(storeProject.start, 'x'),
                end: moment(storeProject.end, 'x'),
            })))
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function createOrUpdateStore({ accessToken, store, handleFailure }) {
    return apiRequest({
        apiEndpoint: store.id ? apiEndpoints.store.update : apiEndpoints.store.createNew,
        authToken: accessToken,
        data: store,
        parameters: store.id ? { [ID_VARIABLE]: store.id } : {},
    })
        .then(result => result.response)
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}