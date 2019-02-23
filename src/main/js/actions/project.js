import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE, USER_ID_VARIABLE } from '../urlmappings';
import { fetchUsersByProjectIdAndRole } from './user';
import moment from 'moment';

function mapProjectObject(accessToken, responseObj) {
    var project = {
        id: responseObj.id,
        name: responseObj.name,
        startDate: moment(responseObj.startDate, 'x'),
        endDate: moment(responseObj.endDate, 'x'),
    };
    fetchUsersByProjectIdAndRole({ accessToken: accessToken, projectId: responseObj.id, role: 'ROLE_LOCAL_COORDINATOR', callback:(users) => {
        project.localCoordinator = users[0];
    }});
    return project;
}

export function fetchOwnProjects({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getOwn,
            authToken: accessToken,
        })
            .then(result => result.response.content.map(robj => mapProjectObject(accessToken, robj)))
            .catch(() => []);
    } else {
        return Promise.resolve([]);
    }
}

export function createNewProject({ accessToken, projectsState, name, startMoment, endMoment }) {
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
        // TODO Error message
        .catch(() => null);
}

export function deleteProject({ accessToken, projectsState, projectId }) {
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
        // TODO Error message
        .catch(err => false);
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
        .then(_ => {
            if (projectsState) {
                projectsState.userChanged(projectId, user, role);
            }
        })
        // TODO Error message
        .catch((e) => console.log(e));
}
