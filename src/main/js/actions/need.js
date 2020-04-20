import moment from 'moment';
import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE, USER_ID_VARIABLE, PROJECT_ID_VARIABLE, NEED_START_DIFF_VARIABLE, NEED_END_DIFF_VARIABLE, PROJECT_HELPER_TYPE_ID_VARIABLE, DATE_VARIABLE } from '../urlmappings';

export function fetchOwnNeedUser(accessToken, needId, userId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.getStatus,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: needId,
            [USER_ID_VARIABLE]: userId,
        },
    }).then(
        result => result.response
    );
}

export function fetchNeedUsers(accessToken, needId) {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.getUsers,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: needId,
        },
    }).then(
        result => result.response.content
    );
}

export function fetchNeedByProjectHelperTypeIdAndDate(accessToken, projectHelperTypeId, date, handleFailure) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.need.getByProjectHelperTypeAndDate,
            authToken: accessToken,
            queries: {
                [PROJECT_HELPER_TYPE_ID_VARIABLE]: projectHelperTypeId,
                [DATE_VARIABLE]: date,
            }
        })
            .then(result => result.response)
            .then(need => ({
                ...need,
                date: moment(need.date, 'x'),
            }))
            .catch(handleFailure);
    } else {
        return Promise.resolve(null);
    }
}

export function createOrUpdateNeed(accessToken, need, handleFailure) {
    return apiRequest({
        apiEndpoint: need.id ? apiEndpoints.need.update : apiEndpoints.need.createNew,
        authToken: accessToken,
        data: need,
        parameters: need.id ? { [ID_VARIABLE]: need.id } : {},
    })
        .then(result => result.response)
        .then(need => ({
            ...need,
            date: moment(need.date, 'x'),
        }))
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}

export function changeApplicationStateForNeed(accessToken, { userId, needId, state }, handleFailure) {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.apply,
        data: {
            needId,
            state,
            userId,
        },
        parameters: {
            [ID_VARIABLE]: needId,
            [USER_ID_VARIABLE]: userId,
        },
        authToken: accessToken
    })
        .then(result => result.response)
        .catch(handleFailure);
}

