import { apiRequest, apiEndpoints } from '../apiclient';
import { addUserToProject } from './project';
import {
    PROJECT_ID_VARIABLE,
    ROLE_VARIABLE,
} from '../urlmappings';

export function fetchCurrentUser({ accessToken }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.current,
        authToken: accessToken,
    })
        .then(result => result.response)
        .catch(err => null);
}

export function fetchUsersByProjectIdAndRole({ accessToken, projectId, role, callback }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.user.get,
        authToken: accessToken,
        queries: {
            [PROJECT_ID_VARIABLE]: projectId,
            [ROLE_VARIABLE]: role,
        }
    })
        .then(result => callback(result.response.content))
        .catch(err => null);
}

export function createNewUser({ accessToken, email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, role, projectId, projectsState }) {
    console.log(telephoneNumber);
    console.log(mobileNumber);
    console.log(businessNumber);
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
                addUserToProject({ accessToken, projectId, user: result.response, role, projectsState })

            }
        })
        // TODO Error message
        .catch((e) => console.log(e));
}
