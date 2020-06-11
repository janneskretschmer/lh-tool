import moment from 'moment';
import { apiEndpoints, apiRequest } from '../apiclient';
import { HELPER_TYPE_ID_VARIABLE, ID_VARIABLE, USER_ID_VARIABLE, WEEKDAY_VARIABLE } from '../urlmappings';
import { fetchUsersByProjectIdAndRoleAndFreeText } from './user';

// function fetchAllUsersForProject({ projectId, accessToken }) {
//     const localCoordinatorsPromise = fetchUsersByProjectIdAndRoleAndFreeText({
//         accessToken,
//         projectId,
//         role: 'ROLE_LOCAL_COORDINATOR',
//     })
//         .then(users => users)
//         .catch(() => []);

//     const publishersPromise = fetchUsersByProjectIdAndRoleAndFreeText(
//         accessToken,
//         projectId,
//         role: 'ROLE_PUBLISHER',
//     )
//         .then(users => users)
//         .catch(() => []);

//     return Promise.all([localCoordinatorsPromise, publishersPromise])
//         .then(([localCoordinators, publishers]) => ({
//             localCoordinators,
//             publishers,
//         }));
// }

// function mapProjectObject(accessToken, responseObj) {
//     const project = {
//         id: responseObj.id,
//         name: responseObj.name,
//         startDate: moment(responseObj.startDate, 'x'),
//         endDate: moment(responseObj.endDate, 'x'),
//         localCoordinator: null,
//         publishers: [],
//     };
//     return fetchAllUsersForProject({ projectId: responseObj.id, accessToken })
//         .then(users => ({
//             id: responseObj.id,
//             name: responseObj.name,
//             startDate: moment(responseObj.startDate, 'x'),
//             endDate: moment(responseObj.endDate, 'x'),
//             localCoordinators: users.localCoordinators,
//             publishers: users.publishers,
//         }))
//         // TODO Blow => error msg.
//         .catch(err => console.log(err));
// }


export function fetchProjects(accessToken) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.getOwn,
        authToken: accessToken,
    }).then(result => result.response.content.map(project => ({
        ...project,
        startDate: moment(project.startDate, 'x'),
        endDate: moment(project.endDate, 'x'),
    })));
}

export function createNewProject({ accessToken, projectsState, name, startMoment, endMoment, handleFailure }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.createNew,
        authToken: accessToken,
        data: {
            name,
            startDate: startMoment.valueOf(),
            endDate: endMoment.valueOf(),
        },
    })
        .then(result => mapProjectObject(accessToken, result.response))
        .then(createdProject => projectsState.projectAdded(createdProject))
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}

export function deleteProject({ accessToken, projectsState, projectId, handleFailure }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.delete,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId,
        },
    })
        .then(() => {
            projectsState.projectRemoved(projectId);
            return true;
        })
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}

export function addUserToProject({ accessToken, projectId, user, role, projectsState }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.addUser,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId,
            [USER_ID_VARIABLE]: user.id,
        },
    })
        .then(() => {
            if (projectsState) {
                projectsState.userAdded(projectId, user, role);
            }
        })
        // TODO Error message
        .catch(e => console.log(e));
}

export function fetchProjectHelperTypes(accessToken, projectId, helperTypeId, weekday, handleFailure) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getHelperTypes,
            authToken: accessToken,
            parameters: {
                [ID_VARIABLE]: projectId,
                [HELPER_TYPE_ID_VARIABLE]: helperTypeId,
            },
            queries: {
                [WEEKDAY_VARIABLE]: weekday,
            }
        })
            .then(result => result.response.content)
            .catch(handleFailure);
    } else {
        return Promise.resolve([]);
    }
}

export function createProjectUser(accessToken, { projectId, userId }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.addUser,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId,
            [USER_ID_VARIABLE]: userId,
        },
    })
        .then(result => result.response);
}

export function deleteProjectUser(accessToken, { projectId, userId }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.deleteUser,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId,
            [USER_ID_VARIABLE]: userId,
        },
    });
}