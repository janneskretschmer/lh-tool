import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE, USER_ID_VARIABLE } from '../urlmappings';
import { fetchUsersByProjectIdAndRole } from './user';
import moment from 'moment';

function mapProjectObject(accessToken, responseObj) {
    const project = {
        id: responseObj.id,
        name: responseObj.name,
        startDate: moment(responseObj.startDate, 'x'),
        endDate: moment(responseObj.endDate, 'x'),
        localCoordinator: undefined,
        publishers: [],
    };
    return fetchUsersByProjectIdAndRole({
        accessToken,
        projectId: responseObj.id,
        role: 'ROLE_LOCAL_COORDINATOR',
    })
        .then(users => {
            project.localCoordinator = users[0];
            return fetchUsersByProjectIdAndRole({
                accessToken,
                projectId: project.id,
                role: 'ROLE_PUBLISHER',
            })
            .then(users => {
                project.publishers = users;
                return project;
            })
            .catch(() => project);
        })
        .catch(() => {
            return fetchUsersByProjectIdAndRole({
                accessToken,
                projectId: project.id,
                role: 'ROLE_PUBLISHER',
            })
            .then(users => {
                project.publishers = users;
                return project;
            })
            .catch(() => project);
        });
}

export function fetchOwnProjects({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getOwn,
            authToken: accessToken,
        })
            .then(result => Promise.all(result.response.content.map(robj => mapProjectObject(accessToken, robj))))
            .catch(() => []);
    } else {
        return Promise.resolve([]);
    }
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
        .then(result => {
            const createdProject = mapProjectObject(accessToken, result.response);
            projectsState.projectAdded(createdProject);
        })
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
