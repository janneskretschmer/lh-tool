import { apiEndpoints, apiRequest } from '../apiclient';
import { ID_VARIABLE } from '../urlmappings';

export function fetchTechnicalCrews(accessToken) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.technicalCrew.get,
            authToken: accessToken
        }).then(result => result.response.content)
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function fetchTechnicalCrew({ accessToken, technicalCrewId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.technicalCrew.getById,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: technicalCrewId }
        })
            .then(result => result.response)
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve(null);
    }
}