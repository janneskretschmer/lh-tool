import { apiRequest, apiEndpoints } from '../apiclient';

export function fetchCurrentUser({ accessToken }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.current,
        authToken: accessToken,
    })
        .then(result => result.response)
        .catch(err => null);
}
