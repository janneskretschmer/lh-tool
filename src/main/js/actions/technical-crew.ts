import { apiEndpoints, apiRequest } from '../apiclient';
import { TechnicalCrewDto } from '../types/generated';
import { ID_VARIABLE } from '../urlmappings';

export function fetchTechnicalCrews(accessToken: string): Promise<TechnicalCrewDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.technicalCrew.get,
        authToken: accessToken
    });
}