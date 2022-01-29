import { apiRequest, apiEndpoints, apiRequestRaw } from '../apiclient';
import {
    PROJECT_ID_VARIABLE,
    ROLE_VARIABLE,
    ID_VARIABLE,
    USER_ID_VARIABLE,
    FREE_TEXT_VARIABLE,
} from '../urlmappings';
import { PasswordChangeDto, ProjectUserDto, UserDto, UserRoleDto } from '../types/generated';

export function fetchCurrentUser(accessToken: string): Promise<UserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.current,
        authToken: accessToken,
    });
}

export function changePassword(data: PasswordChangeDto): Promise<UserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.password,
        data,
    });
}

export function fetchUsersByProjectIdAndRoleAndFreeText(accessToken: string, projectId?: number, role?: string, freeText?: string): Promise<UserDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.get,
        authToken: accessToken,
        queries: {
            [PROJECT_ID_VARIABLE]: projectId?.toString(),
            [ROLE_VARIABLE]: role,
            [FREE_TEXT_VARIABLE]: freeText,
        }
    });
}

export function fetchUser(accessToken: string, userId: number): Promise<UserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getById,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: userId.toString(),
        }
    });
}

export function createUser(accessToken: string, user: UserDto): Promise<UserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.create,
        authToken: accessToken,
        data: user,
    });
}

export function deleteUser(accessToken: string, { id }: UserDto): Promise<undefined> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.user.delete,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id.toString(),
        }
    }).then(result => undefined);
}

export function updateUser(accessToken: string, user: UserDto): Promise<UserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.put,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: user.id.toString(),
        },
        data: user,
    });
}

export function fetchUserRoles(accessToken: string, { id }: UserDto): Promise<UserRoleDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getRoles,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id.toString(),
        }
    });
}

export function createUserRole(accessToken: string, userRole: UserRoleDto): Promise<UserRoleDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.createRole,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: userRole.userId.toString(),
        },
        data: userRole,
    });
}
export function deleteUserRole(accessToken: string, { id, userId }: UserRoleDto): Promise<void> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.deleteRole,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id.toString(),
            [USER_ID_VARIABLE]: userId.toString(),
        },
    });
}

export function fetchUserProjects(accessToken: string, { id }: UserDto): Promise<ProjectUserDto[]> {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getProjects,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id.toString(),
        }
    });
}
