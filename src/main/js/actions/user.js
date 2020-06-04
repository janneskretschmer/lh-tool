import { apiRequest, apiEndpoints } from '../apiclient';
import { addUserToProject } from './project';
import {
    PROJECT_ID_VARIABLE,
    ROLE_VARIABLE,
    ID_VARIABLE,
    USER_ID_VARIABLE,
    FREE_TEXT_VARIABLE,
} from '../urlmappings';

export function fetchCurrentUser({ accessToken }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.current,
        authToken: accessToken,
    })
        .then(result => result.response)
        .catch(err => null);
}

export function changePassword({ userId, token, oldPassword, newPassword, confirmPassword }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.password,
        data: { userId, token, oldPassword, newPassword, confirmPassword }
    })
        .then(result => result.response);
}

export function fetchUsersByProjectIdAndRoleAndFreeText(accessToken, projectId, role, freeText) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.get,
        authToken: accessToken,
        queries: {
            [PROJECT_ID_VARIABLE]: projectId,
            [ROLE_VARIABLE]: role,
            [FREE_TEXT_VARIABLE]: freeText,
        }
    })
        .then(result => result.response.content);
}

export function fetchUser(accessToken, userId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getById,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: userId,
        }
    })
        .then(result => result.response);
}

export function createUser(accessToken, user) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.create,
        authToken: accessToken,
        data: user,
    })
        .then(result => result.response);
}

export function deleteUser(accessToken, { id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.delete,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id,
        }
    });
}

export function updateUser(accessToken, user) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.put,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: user.id,
        },
        data: user,
    })
        .then(result => result.response);
}

export function fetchUserRoles(accessToken, { id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getRoles,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id,
        }
    }).then(result => result.response.content)
}

export function createUserRole(accessToken, userRole) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.createRole,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: userRole.userId,
        },
        data: userRole,
    })
        .then(result => result.response);
}
export function deleteUserRole(accessToken, { id, userId }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.deleteRole,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id,
            [USER_ID_VARIABLE]: userId,
        },
    })
        .then(result => result.response);
}

export function fetchUserProjects(accessToken, { id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getProjects,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id,
        }
    }).then(result => result.response.content);
}
