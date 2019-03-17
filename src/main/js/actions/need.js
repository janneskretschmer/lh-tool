import moment from 'moment';
import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE, USER_ID_VARIABLE } from '../urlmappings';

function mapNeedArray(content) {
    let needs = []
    content.forEach(need => {
        const date = moment(need.date, 'x');
        let item = needs[needs.length - 1];
        if (item === undefined || !date.isSame(item.date, 'day') || item.projectName !== need.projectName) {
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

export function fetchOwnNeeds({ accessToken, userId }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.need.getOwn,
            authToken: accessToken,
        })
            .then(result => attachOwnStateToNeeds({ needs: result.response.content, accessToken, userId }))
            .then(needs => mapNeedArray(needs))
            // TODO Proper error message
            .catch(e => console.log(e));
    } else {
        return Promise.resolve([]);
    }
}

export function createOrUpdateNeed({ accessToken, need, needsState, handleFailure }) {
    return apiRequest({
        apiEndpoint: need.id ? apiEndpoints.need.update : apiEndpoints.need.createNew,
        authToken: accessToken,
        data: need,
        parameters: need.id ? { [ID_VARIABLE]: need.id } : {},
    })
        .then(result => {
            needsState.needsUpdated(result.response);
        })
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