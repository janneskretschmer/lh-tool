import moment from 'moment';
import { apiEndpoints, apiRequest } from '../apiclient';
import { HELPER_TYPE_ID_VARIABLE, ID_VARIABLE, USER_ID_VARIABLE, WEEKDAY_VARIABLE, PROJECT_ID_VARIABLE } from '../urlmappings';


function parseProjectDates(project) {
    return {
        ...project,
        startDate: moment(project.startDate, 'x'),
        endDate: moment(project.endDate, 'x'),
    };
}

function serializeProjectDates(project) {
    return {
        ...project,
        startDate: project.startDate.format('x'),
        endDate: project.endDate.format('x'),
    };
}

export function fetchProjects(accessToken) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.getOwn,
        authToken: accessToken,
    }).then(result => result.response.map(parseProjectDates));
}

export function fetchProject(accessToken, projectId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.getById,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId,
        },
    }).then(result => result.response).then(parseProjectDates);
}

export function createProject(accessToken, project) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.create,
        authToken: accessToken,
        data: serializeProjectDates(project),
    }).then(result => result.response).then(parseProjectDates);
}

export function updateProject(accessToken, project) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.update,
        authToken: accessToken,
        data: serializeProjectDates(project),
        parameters: {
            [ID_VARIABLE]: project.id,
        },
    }).then(result => result.response).then(parseProjectDates);
}

export function deleteProject(accessToken, { id }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.delete,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id,
        },
    });
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

export function fetchProjectHelperTypes(accessToken, projectId, helperTypeId, weekday) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getHelperTypes,
            authToken: accessToken,
            parameters: {
                [PROJECT_ID_VARIABLE]: projectId,
            },
            queries: {
                [HELPER_TYPE_ID_VARIABLE]: helperTypeId,
                [WEEKDAY_VARIABLE]: weekday,
            }
        })
            .then(result => result.response);
    } else {
        return Promise.resolve([]);
    }
}

export function createProjectHelperType(accessToken, projectHelperType) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.addHelperType,
        authToken: accessToken,
        data: projectHelperType,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectHelperType.projectId,
        },
    }).then(result => result.response);
}

export function updateProjectHelperType(accessToken, projectHelperType) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.updateHelperType,
        authToken: accessToken,
        data: projectHelperType,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectHelperType.projectId,
            [ID_VARIABLE]: projectHelperType.id,
        },
    }).then(result => result.response);
}

export function deleteProjectHelperType(accessToken, { id, projectId }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.deleteHelperType,
        authToken: accessToken,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectId,
            [ID_VARIABLE]: id,
        },
    });
}