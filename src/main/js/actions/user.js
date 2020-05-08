import { apiRequest, apiEndpoints } from '../apiclient';
import { addUserToProject } from './project';
import {
    PROJECT_ID_VARIABLE,
    ROLE_VARIABLE,
    ID_VARIABLE,
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

export function fetchUsersByProjectIdAndRole({ accessToken, projectId, role }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.get,
        authToken: accessToken,
        queries: {
            [PROJECT_ID_VARIABLE]: projectId,
            [ROLE_VARIABLE]: role,
        }
    })
        .then(result => result.response.content)
        .catch(err => null);
}

export function fetchUser(accessToken, userId, handleFailure) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.getById,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: userId,
        }
    })
        .then(result => result.response)
        .catch(handleFailure);
}

export function createUser(accessToken, user, handleFailure) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.create,
        authToken: accessToken,
        data: user,
    })
        .then(result => result.response)
        .catch(handleFailure);
}

export function deleteUser({ accessToken, userId, projectsState }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.delete,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: userId,
        }
    })
        .then(() => {
            return projectsState.userRemoved(userId);
        })
        // TODO Error message
        .catch(e => console.log(e));
}

export function updateUser(accessToken, user, handleFailure) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.put,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: user.id,
        },
        data: user,
    })
        .then(result => result.response)
        .catch(handleFailure);
}


