import { apiRequest, apiEndpoints } from '../apiclient';
import { ID_VARIABLE } from '../urlmappings';
import moment from 'moment';

function mapNeedArray(content) {
    let needs = []
    content.forEach(need => {
        const date = moment(need.date, 'x');
        let item = needs[needs.length -1];
        if(item === undefined || !date.isSame(item.date,'day') || item.projectName !== need.projectName){
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

export function fetchOwnNeeds({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.need.getOwn,
            authToken: accessToken,
        })
            .then(result => mapNeedArray(result.response.content))
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
        parameters:need.id ? {[ID_VARIABLE]:need.id} : {},
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