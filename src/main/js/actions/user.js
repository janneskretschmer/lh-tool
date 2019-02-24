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

export function createNewUser({ accessToken, email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, role, projectId, projectsState }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.create,
        authToken: accessToken,
        data: {
            email,
            firstName,
            lastName,
            gender,
            telephoneNumber,
            mobileNumber,
            businessNumber,
            role,
        },
    })
        .then(result => {
            if (projectId) {
                addUserToProject({ accessToken, projectId, user: result.response, role, projectsState });s
                // TODO @Jannes: Möchtest du hier eher folgendes machen:
                // return addUserToProject({ accessToken, projectId, user: result.response, role, projectsState });
            }
        })
        // TODO Error message
        .catch(e => console.log(e));
}

export function deleteUser({ accessToken, userId, projectsState }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.delete,
        authToken: accessToken,
        parameters:{
            [ID_VARIABLE]: userId,
        }
    })
        .then(() => {
            // TODO @Jannes: return fehlt? (siehe oben)
            projectsState.userRemoved(userId);
        })
        // TODO Error message
        .catch(e => console.log(e));
}


