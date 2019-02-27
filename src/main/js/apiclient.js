import superagent from 'superagent';
import URI from 'urijs';
import URITemplate from 'urijs/src/URITemplate';
import { getContextPath } from './config';
import {
    ID_VARIABLE,
    USER_ID_VARIABLE,
    ID_EXTENSION,
    ID_USER_ID_EXTENSION,
    LOGIN_PREFIX,
    INFO_PREFIX,
    INFO_HEARTBEAT,
    USER_PREFIX,
    USER_CURRENT,
    PROJECT_PREFIX,
    PROJECT_DELETE,
} from './urlmappings';

function isWellFormedEndpoint(apiEndpoint) {
    return apiEndpoint && apiEndpoint.method && apiEndpoint.path;
}

function areParametersValid(apiEndpoint, parameters) {
    if ('parameters' in apiEndpoint) {
        const parKeys = Object.keys(parameters);
        const parDiff = (apiEndpoint.parameters || []).filter(par => !parKeys.includes(par));
        return parDiff.length === 0;
    } else {
        return true;
    }
}

function renderPath({ apiEndpoint, parameters }) {
    if (!isWellFormedEndpoint(apiEndpoint)) {
        return { error: new Error('No apiEndpoint of form {method,path} given.') };
    }
    else if (!areParametersValid(apiEndpoint, parameters)) {
        return { error: new Error('Missing parameters') };
    } else {
        return { path: URI.expand(getContextPath() + apiEndpoint.path, parameters) };
    }
}

function buildRequest({ method, path, authToken, queries, data }) {
    const apiHostUrlTemplate = new URI('/');
    let pendingReq = superagent(method, apiHostUrlTemplate.path(path).toString()).type('json');
    if (authToken) {
        pendingReq = pendingReq.set('Authorization', `Bearer ${authToken}`);
    }
    Object.keys(queries).forEach(queryKey => {
        pendingReq = pendingReq.query({
            // eslint-disable-next-line security/detect-object-injection
            [queryKey]: queries[queryKey]
        });
    });
    if (data) {
        pendingReq = pendingReq.send(data);
    }
    return pendingReq;
}

function handleResponse(err, resBody, resolve, reject) {
    if (err) {
        reject({
            error: err,
            response: resBody
        });
    }
    else if (!resBody) {
        resolve({
            error: null,
            response: null
        });
    }
    else {
        resolve({
            error: null,
            response: resBody
        });
    }
}

function completeRequest(request, resolve, reject) {
    request.end((err, res) => {
        const resBody = res ? (res.body || null) : null;
        handleResponse(err, resBody, resolve, reject);
    });
}

export function apiRequest({
    apiEndpoint,
    authToken,
    queries = {},
    parameters = {},
    data
} = {
        apiEndpoint: null,
        authToken: null,
        queries: {},
        parameters: {},
        data: null,
    }) {

    
    const renderedPathResult = renderPath({ apiEndpoint, parameters });
    if (renderedPathResult.error) {
        return Promise.reject(renderedPathResult.error);
    }

    const req = buildRequest({
        method: apiEndpoint.method,
        path: renderedPathResult.path,
        authToken,
        queries,
        data,
    });

    return new Promise((resolve, reject) => {
        completeRequest(req, resolve, reject);
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
            path: LOGIN_PREFIX + '/',
        },
    },
    project: {
        getOwn: {
            method: 'GET',
            // TODO Trailing '/' also necessary?
            path: PROJECT_PREFIX + '/',
        },
        createNew: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: PROJECT_PREFIX + '/',
        },
        delete: {
            method: 'DELETE',
            path: PROJECT_DELETE,
            parameters: [ID_VARIABLE],
        },
        addUser: {
            method: 'POST',
            path: PROJECT_PREFIX + ID_USER_ID_EXTENSION,
            parameters: [ID_VARIABLE, USER_ID_VARIABLE],
        }
    },
    user: {
        current: {
            method: 'GET',
            path: USER_PREFIX + USER_CURRENT,
        },
        create: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: USER_PREFIX + '/' 
        },
        get: {
            method: 'GET',
            // TODO Trailing '/' also necessary?
            path: USER_PREFIX + '/'
        },
        delete: {
            method: 'DELETE',
            path: USER_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        put: {
            method: 'PUT',
            path: USER_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
};
