import { apiEndpoints, apiRequest } from '../apiclient';
import { HelperTypeDto } from '../types/generated';
import { PROJECT_ID_VARIABLE, WEEKDAY_VARIABLE } from '../urlmappings';

export function fetchHelperTypes(accessToken: string, projectId?: number, weekday?: number): Promise<HelperTypeDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.helperType.get,
        authToken: accessToken,
        queries: {
            [PROJECT_ID_VARIABLE]: projectId?.toString(),
            [WEEKDAY_VARIABLE]: weekday?.toString(),
        }
    });
}