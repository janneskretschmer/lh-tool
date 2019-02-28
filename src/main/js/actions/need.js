import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE } from '../urlmappings';
import moment from 'moment';

export function fetchOwnNeeds({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.need.getOwn,
            authToken: accessToken,
        })
            .then(result => result.response.content)
            .catch(() => []);
    } else {
        return Promise.resolve([]);
    }
}

/*
export function createNewNeed({ accessToken, projectsState, name, startMoment, endMoment, handleFailure }) {
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
*/