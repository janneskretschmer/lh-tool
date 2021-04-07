import { apiEndpoints, apiRequest } from '../apiclient';

export function fetchItemTags(accessToken) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.itemTag.get,
            authToken: accessToken
        }).then(result => result.response);
    } else {
        return Promise.resolve([]);
    }
}