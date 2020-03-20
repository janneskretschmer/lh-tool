import { apiEndpoints, apiRequest } from '../apiclient';
import { PROJECT_ID_VARIABLE, START_DATE_VARIABLE, END_DATE_VARIABLE } from '../urlmappings';

export function fetchNeedsForCalendar(accessToken, projectId, startDate, endDate, handleFailure) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.assembled.getNeedsForCalendar,
            authToken: accessToken,
            queries: {
                [PROJECT_ID_VARIABLE]: projectId,
                [START_DATE_VARIABLE]: startDate,
                [END_DATE_VARIABLE]: endDate,
            }
        })
            .then(result => result.response.content)
            .catch(handleFailure);
    } else {
        return Promise.resolve([]);
    }
}