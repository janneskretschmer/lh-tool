import moment from 'moment';
import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE, USER_ID_VARIABLE, NEED_START_DIFF_VARIABLE, NEED_END_DIFF_VARIABLE } from '../urlmappings';

function mapNeedArray(accessToken, content) {
    let needs = []
    content.forEach(need => {
        const date = moment(need.date, 'x');
        let item = needs[needs.length - 1];
        if (!item || !date.isSame(item.date, 'day') || item.projectName !== need.projectName) {
            item = {
                date,
                projectName: need.projectName,
                projectId: need.projectId
            }
            needs.push(item);
        }
        item[need.helperType] = need;
    });
    return needs;
}

export function fetchOwnNeedStatus({ accessToken, needId, userId }) {
    return apiRequest({
        apiEndpoint: apiEndpoints.need.getStatus,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: needId,
            [USER_ID_VARIABLE]: userId,
        },
    });
}

function attachOwnStateToNeeds({ needs, accessToken, userId }) {
    const promNeeds = needs.map(need => {
        if (need.id) {
            return fetchOwnNeedStatus({ accessToken, needId: need.id, userId })
                .then(result => ({ ...need, ownState: result.response.state }))
        } else {
            return Promise.resolve(need);
        }
    });
    return Promise.all(promNeeds);
}

export function fetchOwnNeeds({ accessToken, userId, startDiff, endDiff }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.need.getOwn,
            authToken: accessToken,
            queries: {
                [NEED_START_DIFF_VARIABLE]: startDiff,
                [NEED_END_DIFF_VARIABLE]: endDiff,
            }
        })
            .then(result => attachOwnStateToNeeds({ needs: result.response.content, accessToken, userId }))
            .then(needs => mapNeedArray(accessToken, needs))
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function fetchNeed({ accessToken, needId, userId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.need.get,
            authToken: accessToken,
            parameters: { [ID_VARIABLE]: needId }
        })
            .then(result => attachOwnStateToNeeds({ needs: [result.response], accessToken, userId }))
            .then(needs => needs[0])
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve(null);
    }
}

export function createOrUpdateNeed({ need, sessionState, handleFailure }) {
    const userId = sessionState.currentUser.id;
    return apiRequest({
        apiEndpoint: need.id ? apiEndpoints.need.update : apiEndpoints.need.createNew,
        authToken: sessionState.accessToken,
        data: need,
        parameters: need.id ? { [ID_VARIABLE]: need.id } : {},
    })
        .then(result => attachOwnStateToNeeds({ needs: [result.response], accessToken: sessionState.accessToken, userId }))
        .then(needs => needs[0])
        .catch(err => {
            if (handleFailure) {
                handleFailure(err);
            }
        });
}

export function applyForNeed({ sessionState, needId, handleFailure }) {
    const userId = sessionState.currentUser.id;
    return apiRequest({
        apiEndpoint: apiEndpoints.need.apply,
        data: {
            needId,
            state: 'APPLIED',
            userId,
        },
        parameters: {
            [ID_VARIABLE]: needId,
            [USER_ID_VARIABLE]: userId,
        },
        authToken: sessionState.accessToken
    })
        .then(result => result.response);
    // TODO handleFailure
}

export function revokeApplicationForNeed({ sessionState, needId, handleFailure }) {
    const userId = sessionState.currentUser.id;
    return apiRequest({
        apiEndpoint: apiEndpoints.need.apply,
        data: {
            needId,
            state: 'NONE',
            userId,
        },
        parameters: {
            [ID_VARIABLE]: needId,
            [USER_ID_VARIABLE]: userId,
        },
        authToken: sessionState.accessToken
    })
        .then(result => result.response);
    // TODO handleFailure
}

//TODO duplication ?
export function changeApplicationStateForNeed({ accessToken, userId, needId, state, handleFailure }) {
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
        .then(result => result.response);
    // TODO handleFailure
}

/*
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