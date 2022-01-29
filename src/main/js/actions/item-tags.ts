import { apiEndpoints, apiRequest } from '../apiclient';
import { ItemTagDto } from '../types/generated';

export function fetchItemTags(accessToken: string): Promise<ItemTagDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.itemTag.get,
        authToken: accessToken
    });
}