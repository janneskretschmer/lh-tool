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
    };
    return fetchUsersByProjectIdAndRole({
        accessToken,
        projectId: responseObj.id,
        role: 'ROLE_LOCAL_COORDINATOR',
    })
        .then(users => {
            project.localCoordinator = users[0];
            return project;
        })
        .catch(() => project);
}

export function fetchOwnProjects({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getOwn,
            authToken: accessToken,
        })
            .then(result => result.response.content.map(robj => mapProjectObject(accessToken, robj)))
            .then(Promise.all)
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
<<<<<<< Upstream, based on origin/master
        .then(result => {
            const createdProject = mapProjectObject(accessToken, result.response);
            projectsState.projectAdded(createdProject);
        })
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
=======
        .then(result => mapProjectObject(accessToken, result.response))
        .then(createdProject => projectsState.projectAdded(createdProject))
        // TODO Error message
        .catch(() => null);
>>>>>>> 6bcc30b Review
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
    console.log('sers');
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
                projectsState.userChanged(projectId, user, role);
            }
        })
        // TODO Error message
        .catch(e => console.log(e));
}
