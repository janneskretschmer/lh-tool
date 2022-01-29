import { apiEndpoints, apiRequest } from '../apiclient';
import { NeedDto, NeedUserDto } from '../types/generated';
import { ID_VARIABLE, USER_ID_VARIABLE } from '../urlmappings';
import { convertToYYYYMMDD } from '../util';

export function fetchOwnNeedUser(accessToken: string, needId: number, userId: number): Promise<NeedUserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.getStatus,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: needId.toString(),
            [USER_ID_VARIABLE]: userId.toString(),
        },
    });
}

export function fetchNeedUsers(accessToken: string, needId: number): Promise<NeedUserDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.getUsers,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: needId.toString(),
        },
    });
}

export function createOrUpdateNeed(accessToken: string, need: NeedDto): Promise<NeedDto> {
    return apiRequest<NeedDto>({
        apiEndpoint: need.id ? apiEndpoints.need.update : apiEndpoints.need.createNew,
        authToken: accessToken,
        data: {
            ...need,
            date: convertToYYYYMMDD(need.date),
        },
        parameters: need.id ? { [ID_VARIABLE]: need.id.toString() } : {},
    })
        .then(saved => ({
            ...saved,
            date: new Date(saved.date),
        }));
}

export function changeApplicationStateForNeed(accessToken: string, { userId, needId, state }: NeedUserDto): Promise<NeedUserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.apply,
        data: {
            needId,
            state,
            userId,
        },
        parameters: {
            [ID_VARIABLE]: needId.toString(),
            [USER_ID_VARIABLE]: userId.toString(),
        },
        authToken: accessToken
    });
}

