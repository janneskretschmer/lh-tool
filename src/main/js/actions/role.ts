import { apiEndpoints, apiRequest } from '../apiclient';
import { RoleDto } from '../types/generated';

export function fetchRoles(accessToken: string): Promise<RoleDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.role.get,
        authToken: accessToken,
    });
}