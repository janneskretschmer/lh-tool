import { apiEndpoints, apiRequest, createEntity, deleteEntity, fetchEntity, updateEntity } from '../apiclient';
import { StoreDto } from '../types/generated';

export function fetchOwnStores(accessToken: string): Promise<StoreDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.store.get,
        authToken: accessToken
    });
}

export function fetchStore(accessToken: string, storeId: number): Promise<StoreDto> {
    return fetchEntity<StoreDto>(apiEndpoints.store.getById, accessToken, storeId);
}

export function createStore(accessToken: string, store: StoreDto): Promise<StoreDto> {
    return createEntity<StoreDto>(apiEndpoints.store.createNew, accessToken, store);
}

export function updateStore(accessToken: string, store: StoreDto): Promise<StoreDto> {
    return updateEntity<StoreDto>(apiEndpoints.store.update, accessToken, store);
}

export function deleteStore(accessToken: string, storeId: number): Promise<void> {
    return deleteEntity(apiEndpoints.store.delete, accessToken, storeId);
}