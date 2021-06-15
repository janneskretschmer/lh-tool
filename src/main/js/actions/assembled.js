import { apiEndpoints, apiRequest } from '../apiclient';
import { PROJECT_ID_VARIABLE, START_DATE_VARIABLE, END_DATE_VARIABLE } from '../urlmappings';
import { convertFromMUIFormat } from '../util';
import moment from 'moment';

export function fetchNeedsForCalendar(accessToken, projectId, startDate, endDate, handleFailure) {
    if (accessToken) {
        return apiRequest({
            apiEndpoint: apiEndpoints.assembled.getNeedsForCalendar,
            authToken: accessToken,
            queries: {
                [PROJECT_ID_VARIABLE]: projectId,
                [START_DATE_VARIABLE]: startDate,
                [END_DATE_VARIABLE]: endDate,
            }
        })
            .then(result => result.response)
            .then(dateObject => {
                let dateMap = new Map();
                Object.keys(dateObject).forEach(
                    dateString => dateMap.set(dateString, {
                        date: convertFromMUIFormat(dateString), helperTypes: dateObject[dateString].helperTypes.map(
                            helperType => ({
                                ...helperType,
                                shifts: helperType.shifts.map(
                                    shift => ({
                                        ...shift,
                                        need: {
                                            ...shift.need,
                                            date: moment(shift.need.date, 'x'),
                                        }
                                    })
                                )
                            })
                        )
                    })
                );
                return dateMap;
            });
    } else {
        return Promise.resolve([]);
    }
}