import { apiEndpoints, apiRequest } from '../apiclient';
import { AssembledHelperTypeWrapperDto } from '../types/generated';
import { END_DATE_VARIABLE, PROJECT_ID_VARIABLE, START_DATE_VARIABLE } from '../urlmappings';
import { convertToYYYYMMDD } from '../util';

// TODO: proper return type
export function fetchNeedsForCalendar(accessToken: string, projectId: number, startDate: Date, endDate: Date): Promise<any> {
    return apiRequest<Map<string, AssembledHelperTypeWrapperDto>>({
        apiEndpoint: apiEndpoints.assembled.getNeedsForCalendar,
        authToken: accessToken,
        queries: {
            [PROJECT_ID_VARIABLE]: projectId.toString(),
            [START_DATE_VARIABLE]: convertToYYYYMMDD(startDate),
            [END_DATE_VARIABLE]: convertToYYYYMMDD(endDate),
        }
    })
        .then(dateObject => {
            let dateMap = new Map();
            Object.keys(dateObject).forEach(
                dateString => dateMap.set(dateString, {
                    date: new Date(dateString),
                    helperTypes: dateObject[dateString].helperTypes.map(
                        helperType => ({
                            ...helperType,
                            shifts: helperType.shifts.map(
                                shift => ({
                                    ...shift,
                                    need: {
                                        ...shift.need,
                                        date: new Date(shift.need.date),
                                    }
                                })
                            )
                        })
                    )
                })
            );
            return dateMap;
        });
}