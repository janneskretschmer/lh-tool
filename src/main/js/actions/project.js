import { apiRequest, apiEndpoints } from '../apiclient';
import moment from 'moment';

function mapProjectObject(responseObj) {
    return responseObj.content.map(cntItem => ({
        id: cntItem.id,
        name: cntItem.name,
        startDate: moment(cntItem.startDate, 'x'),
        endDate: moment(cntItem.endDate, 'x'),
    }));
}

export function fetchOwnProjects({ accessToken }) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.project.getOwn,
            authToken: accessToken,
        })
            .then(result => mapProjectObject(result.response))
            .catch(() => []);
    } else {
        return Promise.resolve([]);
    }
}
