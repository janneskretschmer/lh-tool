import { apiRequest, apiEndpoints } from '../apiclient';
import moment from 'moment';

export function fetchOwnProjects({ accessToken }) {
    // TODO currently mocked
    if (accessToken) {
        return Promise.resolve([{
            id: 1,
            name: 'München',
            startDate: moment("2018-01-01T05:06:07", moment.ISO_8601),
            endDate: moment("2019-05-04T05:06:07", moment.ISO_8601),
        }, {
            id: 3,
            name: 'Stuttgart',
            startDate: moment("2010-01-01T05:06:07", moment.ISO_8601),
            endDate: moment("2010-05-04T05:06:07", moment.ISO_8601),
        }]);
    } else {
        return Promise.resolve([]);
    }
}
