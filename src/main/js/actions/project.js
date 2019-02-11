import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE } from '../urlmappings';
import moment from 'moment';

function mapProjectObject(responseObj) {
    return {
        id: responseObj.id,
        name: responseObj.name,
        startDate: moment(responseObj.startDate, 'x'),
        endDate: moment(responseObj.endDate, 'x'),
    };
}

export function fetchOwnProjects({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getOwn,
            authToken: accessToken,
        })
            .then(result => result.response.content.map(robj => mapProjectObject(robj)))
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
            const createdProject = mapProjectObject(result.response);
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
