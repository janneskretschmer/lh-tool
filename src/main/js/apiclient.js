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
    LOGIN_PASSWORD_RESET,
    INFO_PREFIX,
    INFO_HEARTBEAT,
    USER_PREFIX,
    USER_CURRENT,
    USER_PASSWORD,
    PROJECT_PREFIX,
    PROJECT_DELETE,
    PROJECT_ID_VARIABLE,
    NEED_PREFIX,
    NEED_START_DIFF_VARIABLE,
    NEED_END_DIFF_VARIABLE,
    STORE_PREFIX,
    STORE_PROJECTS,
    SLOT_PREFIX,
    SLOT_STORE_VARIABLE,
    ITEM_PREFIX,
    ITEM_NOTES,
    ITEM_TAGS,
    ITEM_HISTORY,
    TECHNICAL_CREW_PREFIX,
    PROJECT_HELPER_TYPE_ID_VARIABLE,
    DATE_VARIABLE,
    PROJECT_HELPER_TYPES,
    HELPER_TYPE_ID_VARIABLE,
    WEEKDAY_VARIABLE,
    HELPER_TYPE_PREFIX,
    ID_USER_EXTENSION,
    ASSEMBLED_PREFIX,
    ASSEMBLED_NEED_FOR_CALENDAR,
    START_DATE_VARIABLE,
    END_DATE_VARIABLE,
    ROLES_PREFIX,
    USER_ROLES,
    USER_ROLES_ID,
    USER_PROJECTS,
    ROLE_VARIABLE,
    FREE_TEXT_VARIABLE,

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
    pendingReq.set('Pragma', 'no-cache');
    pendingReq.set('Cache-Control', 'no-cache');
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
        pwreset: {
            method: 'POST',
            path: LOGIN_PREFIX + LOGIN_PASSWORD_RESET,
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
        },
        deleteUser: {
            method: 'DELETE',
            path: PROJECT_PREFIX + ID_USER_ID_EXTENSION,
            parameters: [ID_VARIABLE, USER_ID_VARIABLE],
        },
        getHelperTypes: {
            method: 'GET',
            path: PROJECT_PREFIX + PROJECT_HELPER_TYPES,
            parameters: [ID_VARIABLE, HELPER_TYPE_ID_VARIABLE],
            queries: [WEEKDAY_VARIABLE]
        }
    },
    user: {
        current: {
            method: 'GET',
            path: USER_PREFIX + USER_CURRENT,
        },
        password: {
            method: 'PUT',
            path: USER_PREFIX + USER_PASSWORD,
        },
        create: {
            method: 'POST',
            path: USER_PREFIX
        },
        get: {
            method: 'GET',
            path: USER_PREFIX,
            queries: [PROJECT_ID_VARIABLE, ROLE_VARIABLE, FREE_TEXT_VARIABLE],
        },
        getById: {
            method: 'GET',
            path: USER_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
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
        getRoles: {
            method: 'GET',
            path: USER_PREFIX + USER_ROLES,
            parameters: [ID_VARIABLE],
        },
        createRole: {
            method: 'POST',
            path: USER_PREFIX + USER_ROLES,
            parameters: [ID_VARIABLE],
        },
        deleteRole: {
            method: 'DELETE',
            path: USER_PREFIX + USER_ROLES_ID,
            parameters: [USER_ID_VARIABLE, ID_VARIABLE],
        },
        getProjects: {
            method: 'GET',
            path: USER_PREFIX + USER_PROJECTS,
            parameters: [ID_VARIABLE],
        },
    },
    role: {
        get: {
            method: 'GET',
            path: ROLES_PREFIX,
        },
    },
    helperType: {
        get: {
            method: 'GET',
            path: HELPER_TYPE_PREFIX + '/',
            queries: [PROJECT_ID_VARIABLE, WEEKDAY_VARIABLE]
        },
        getById: {
            method: 'GET',
            path: HELPER_TYPE_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
    need: {
        getByProjectHelperTypeAndDate: {
            method: 'GET',
            // TODO Trailing '/' also necessary?
            path: NEED_PREFIX + '/',
            queries: [PROJECT_HELPER_TYPE_ID_VARIABLE, DATE_VARIABLE],
        },
        get: {
            method: 'GET',
            path: NEED_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: NEED_PREFIX + '/',
        },
        update: {
            method: 'PUT',
            path: NEED_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        apply: {
            method: 'PUT',
            path: NEED_PREFIX + ID_USER_ID_EXTENSION,
            parameters: [ID_VARIABLE, USER_ID_VARIABLE],
        },
        getStatus: {
            method: 'GET',
            path: NEED_PREFIX + ID_USER_ID_EXTENSION,
            parameters: [ID_VARIABLE, USER_ID_VARIABLE],
        },
        getUsers: {
            method: 'GET',
            path: NEED_PREFIX + ID_USER_EXTENSION,
            parameters: [ID_VARIABLE],
        }
    },
    store: {
        get: {
            method: 'GET',
            path: STORE_PREFIX + '/',
        },
        getById: {
            method: 'GET',
            path: STORE_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        getProjects: {
            method: 'GET',
            path: STORE_PREFIX + STORE_PROJECTS,
            parameters: [ID_VARIABLE],
        },
        setProjects: {
            method: 'POST',
            path: STORE_PREFIX + STORE_PROJECTS,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: STORE_PREFIX + '/',
        },
        update: {
            method: 'PUT',
            path: STORE_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
    slot: {
        getByStore: {
            method: 'GET',
            path: SLOT_PREFIX + '/',
            queries: [SLOT_STORE_VARIABLE],
        },
        getById: {
            method: 'GET',
            path: SLOT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: SLOT_PREFIX + '/',
        },
        update: {
            method: 'PUT',
            path: SLOT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
    item: {
        get: {
            method: 'GET',
            path: ITEM_PREFIX + '/',
        },
        getById: {
            method: 'GET',
            path: ITEM_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: ITEM_PREFIX + '/',
        },
        update: {
            method: 'PUT',
            path: ITEM_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        getNotes: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_NOTES,
            parameters: [ID_VARIABLE],
        },
        createNotes: {
            method: 'POST',
            // TODO Trailing '/' also necessary?
            path: ITEM_PREFIX + ITEM_NOTES,
            parameters: [ID_VARIABLE],
        },
        getTags: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_TAGS,
            parameters: [ID_VARIABLE],
        },
        getHistory: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_HISTORY,
            parameters: [ID_VARIABLE],
        },
    },
    technicalCrew: {
        get: {
            method: 'GET',
            path: TECHNICAL_CREW_PREFIX + '/',
        },
        getById: {
            method: 'GET',
            path: TECHNICAL_CREW_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
    assembled: {
        getNeedsForCalendar: {
            method: 'GET',
            path: ASSEMBLED_PREFIX + ASSEMBLED_NEED_FOR_CALENDAR,
            queries: [PROJECT_ID_VARIABLE, START_DATE_VARIABLE, END_DATE_VARIABLE],
        }
    },
};
