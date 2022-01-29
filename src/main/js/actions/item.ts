import { apiEndpoints, apiRequest, apiRequestRaw, createEntity, deleteEntity, fetchEntity, updateEntity } from '../apiclient';
import { ItemDto, ItemHistoryDto, ItemImageDto, ItemItemDto, ItemNoteDto, ItemTagDto, UserDto } from '../types/generated';
import { FREE_TEXT_VARIABLE, ID_VARIABLE, ITEM_ID_VARIABLE, NOTE_ID_VARIABLE } from '../urlmappings';

export function fetchItems(accessToken: string, freeText?: string): Promise<ItemDto[]> {
    if (accessToken) {
        return apiRequest<ItemDto[]>({
            apiEndpoint: apiEndpoints.item.get,
            authToken: accessToken,
            queries: {
                [FREE_TEXT_VARIABLE]: freeText,
            },
        });
    } else {
        return Promise.resolve([]);
    }
}

export function fetchItem(accessToken: string, itemId: number): Promise<ItemDto> {
    return fetchEntity(apiEndpoints.item.getById, accessToken, itemId);
}

export function createItem(accessToken: string, item: ItemDto): Promise<ItemDto> {
    return createEntity(apiEndpoints.item.createNew, accessToken, item);
}

export function updateItem(accessToken: string, item: ItemDto): Promise<ItemDto> {
    return updateEntity(apiEndpoints.item.update, accessToken, item);
}

export function deleteItem(accessToken: string, itemId: number): Promise<void> {
    return deleteEntity(apiEndpoints.item.delete, accessToken, itemId);
}

export function updateItemBrokenState(accessToken: string, itemId: number, broken: boolean): Promise<boolean> {
    return updateEntity(apiEndpoints.item.patch, accessToken, { id: itemId, broken }).then(item => item.broken);
}

export function updateItemSlot(accessToken: string, itemId: number, slotId: number): Promise<number> {
    return updateEntity(apiEndpoints.item.patch, accessToken, { id: itemId, slotId }).then(item => item.slotId);
}

export function updateItemQuantity(accessToken: string, itemId: number, quantity: number): Promise<number> {
    return updateEntity(apiEndpoints.item.patch, accessToken, { id: itemId, quantity }).then(item => item.quantity);
}



export function fetchItemNotes(accessToken: string, itemId: number): Promise<ItemNoteDto[]> {
    return apiRequest<ItemNoteDto[]>({
        apiEndpoint: apiEndpoints.item.getNotes,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId.toString() }
    })
        .then(notes => notes.map(note => ({
            ...note,
            timestamp: new Date(note.timestamp),
        })));
}

export function fetchItemNotesUser(accessToken: string, { itemId, id }: ItemNoteDto): Promise<UserDto> {
    return apiRequest<UserDto>({
        apiEndpoint: apiEndpoints.item.getNotesUser,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId.toString(),
            [NOTE_ID_VARIABLE]: id.toString(),
        },
    });
}

export function createItemNote(accessToken: string, itemNote: ItemNoteDto): Promise<ItemNoteDto> {
    return apiRequest<ItemNoteDto>({
        apiEndpoint: apiEndpoints.item.createNotes,
        authToken: accessToken,
        data: itemNote,
        parameters: { [ITEM_ID_VARIABLE]: itemNote.itemId.toString() }
    })
        .then(note => ({
            ...note,
            timestamp: new Date(note.timestamp),
        }));
}

export function deleteItemNote(accessToken: string, { itemId, id }: ItemNoteDto): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.item.deleteNotes,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId.toString(),
            [NOTE_ID_VARIABLE]: id.toString(),
        },
    }).then(result => undefined);
}

export function fetchItemTagsByItem(accessToken: string, itemId: number): Promise<ItemTagDto[]> {
    return apiRequest<ItemTagDto[]>({
        apiEndpoint: apiEndpoints.item.getTags,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId.toString() }
    });
}

export function createItemTag(accessToken: string, itemId: number, itemTag: ItemTagDto): Promise<ItemTagDto> {
    return apiRequest<ItemTagDto>({
        apiEndpoint: apiEndpoints.item.createTag,
        authToken: accessToken,
        data: itemTag,
        parameters: { [ITEM_ID_VARIABLE]: itemId.toString() }
    });
}

export function deleteItemTag(accessToken: string, itemId: number, itemTagId: number): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.item.deleteTag,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId.toString(), [ID_VARIABLE]: itemTagId.toString() }
    }).then(result => undefined);
}

export function fetchItemHistory(accessToken: string, itemId: number): Promise<ItemHistoryDto[]> {
    return apiRequest<ItemHistoryDto[]>({
        apiEndpoint: apiEndpoints.item.getHistory,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId.toString() }
    })
        .then(history => history.map(tmp => ({
            ...tmp,
            timestamp: new Date(tmp.timestamp),
            data: tmp.data ? JSON.parse(tmp.data) : null,
        })));
}


export function fetchItemHistoryUser(accessToken: string, { itemId, id }: ItemHistoryDto): Promise<UserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.item.getHistoryUser,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId.toString(),
            [ID_VARIABLE]: id.toString(),
        },
    });
}


export function fetchItemImage(accessToken: string, itemId: number): Promise<ItemImageDto> {
    return apiRequest<ItemImageDto>({
        apiEndpoint: apiEndpoints.item.getImage,
        authToken: accessToken,
        parameters: {
            [ITEM_ID_VARIABLE]: itemId.toString(),
        },
    });
}

export function createItemImage(accessToken: string, itemImage: ItemImageDto): Promise<ItemImageDto> {
    return apiRequest<ItemImageDto>({
        apiEndpoint: apiEndpoints.item.createImage,
        authToken: accessToken,
        data: itemImage,
        parameters: {
            [ITEM_ID_VARIABLE]: itemImage.itemId.toString(),
        },
    });
}

export function updateItemImage(accessToken: string, itemImage: ItemImageDto): Promise<ItemImageDto> {
    return apiRequest<ItemImageDto>({
        apiEndpoint: apiEndpoints.item.updateImage,
        authToken: accessToken,
        data: itemImage,
        parameters: {
            [ITEM_ID_VARIABLE]: itemImage.itemId.toString(),
            [ID_VARIABLE]: itemImage.id.toString(),
        },
    });
}

export function fetchRelatedItems(accessToken: string, itemId: number): Promise<ItemDto[]> {
    return apiRequest<ItemDto[]>({
        apiEndpoint: apiEndpoints.item.getRelatedItems,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: itemId.toString() }
    });
}

export function createItemRelation(accessToken: string, item1Id: number, item2Id: number): Promise<ItemItemDto> {
    return apiRequest<ItemItemDto>({
        apiEndpoint: apiEndpoints.item.createItemRelation,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: item1Id.toString() },
        data: { item1Id, item2Id },
    });
}

export function deleteItemRelation(accessToken: string, item1Id: number, item2Id: number): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.item.deleteItemRelation,
        authToken: accessToken,
        parameters: { [ITEM_ID_VARIABLE]: item1Id.toString(), [ID_VARIABLE]: item2Id.toString() },
        data: { item1Id, item2Id },
    })
        .then(result => undefined);
}
