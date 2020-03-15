import { apiEndpoints, apiRequest } from "../apiclient";
import { WEEKDAY_VARIABLE, PROJECT_ID_VARIABLE } from "../urlmappings";

export function fetchHelperTypes(accessToken, projectId, weekday, handleFailure) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.helperType.get,
            authToken: accessToken,
            queries: {
                [PROJECT_ID_VARIABLE]: projectId,
                [WEEKDAY_VARIABLE]: weekday,
            }
        })
            .then(result => result.response.content)
            .catch(handleFailure);
    } else {
        return Promise.resolve([]);
    }
}