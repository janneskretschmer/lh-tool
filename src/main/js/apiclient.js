import superagent from 'superagent';
import URI from 'urijs';
import URITemplate from 'urijs/src/URITemplate';
import { getContextPath } from './config';
import {
    LOGIN_PEFIX,
    INFO_PREFIX,
    INFO_HEARTBEAT,
} from './urlmappings';

export function apiRequest({
    apiEndpoint,
    authToken,
    queries = {},
    parameters = {},
    data
} = {
        apiEndpoint: undefined,
        authToken: undefined,
        queries: {},
        parameters: {},
        data: undefined
    }) {
    const apiHostUrlTemplate = new URI('/');

    if (!apiEndpoint || (!apiEndpoint.method) || (!apiEndpoint.path))
        return Promise.reject(new Error('No apiEndpoint of form {method,path} given.'));

    if ('parameters' in apiEndpoint) {
        const parKeys = Object.keys(parameters);
        const parDiff = (apiEndpoint.parameters || []).filter(par => !parKeys.includes(par));
        if (parDiff.length > 0)
            return Promise.reject(new Error(`Missing parameters: ${JSON.stringify(parDiff)}`));
    }

    const renderedPath = URI.expand(getContextPath() + apiEndpoint.path, parameters);

    let pendingReq = superagent(apiEndpoint.method, apiHostUrlTemplate.path(renderedPath).toString()).type('json');

    if (authToken)
        pendingReq = pendingReq.set('Authorization', `Bearer ${authToken}`);

    Object.keys(queries).forEach(queryKey => {
        pendingReq = pendingReq.query({
            [queryKey]: queries[queryKey]
        });
    });

    if (data)
        pendingReq = pendingReq.send(data);

    return new Promise((resolve, reject) => {
        pendingReq.end((err, res) => {
            if (err)
                reject({
                    error: err,
                    response: res ? (res.body || null) : null
                });
            else if (!res.body)
                reject({
                    error: new Error('No valid data received.'),
                    response: null
                });
            else
                resolve({
                    error: null,
                    response: res.body
                });
        });
    });
}

export const apiEndpoints = {
    info: {
        heartbeat: {
            method: 'GET',
            path: INFO_PREFIX + INFO_HEARTBEAT,
        },
    },
    login: {
        login: {
            method: 'POST',
            // TODO Trailing '/' seems to be necessary
            path: LOGIN_PEFIX + '/',
        },
    },
    project: undefined,
    user: undefined,
};
