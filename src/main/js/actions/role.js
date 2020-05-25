import { apiEndpoints, apiRequest } from "../apiclient";

export function fetchRoles(accessToken) {
    return apiRequest({
        apiEndpoint: apiEndpoints.role.get,
        authToken: accessToken,
    })
        .then(result => result.response.content)
}