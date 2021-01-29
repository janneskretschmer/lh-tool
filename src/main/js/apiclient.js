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
    STORE_PREFIX,
    STORE_PROJECTS,
    SLOT_PREFIX,
    ITEM_PREFIX,
    ITEM_NOTES,
    ITEM_TAGS,
    ITEM_TAGS_ID,
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
    PROJECT_HELPER_TYPES_ID,
    ITEM_ID_VARIABLE,
    ITEM_NOTES_ID,
    NOTE_ID_VARIABLE,
    ITEM_NOTES_USER,
    ITEM_HISTORY_USER,
    ITEM_TAG_PREFIX,
    ITEM_IMAGE,
    ITEM_IMAGE_ID,
    ITEM_ITEMS,
    ITEM_ITEMS_ID,
    NAME_VARIABLE,
    DESCRIPTION_VARIABLE,
    STORE_ID_VARIABLE,

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
        return {
            path: URI.expand("/foo/{dir}/{file}", {
                dir: "bar",
                file: "world.html"
            })
        };
        //return { path: URI.expand(getContextPath() + apiEndpoint.path, parameters) };
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
            path: PROJECT_PREFIX,
        },
        getById: {
            method: 'GET',
            path: PROJECT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        create: {
            method: 'POST',
            path: PROJECT_PREFIX,
        },
        update: {
            method: 'PUT',
            path: PROJECT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
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
            parameters: [PROJECT_ID_VARIABLE],
            queries: [WEEKDAY_VARIABLE, HELPER_TYPE_ID_VARIABLE]
        },
        addHelperType: {
            method: 'POST',
            path: PROJECT_PREFIX + PROJECT_HELPER_TYPES,
            parameters: [PROJECT_ID_VARIABLE],
        },
        updateHelperType: {
            method: 'PUT',
            path: PROJECT_PREFIX + PROJECT_HELPER_TYPES_ID,
            parameters: [PROJECT_ID_VARIABLE, ID_VARIABLE],
        },
        deleteHelperType: {
            method: 'DELETE',
            path: PROJECT_PREFIX + PROJECT_HELPER_TYPES_ID,
            parameters: [PROJECT_ID_VARIABLE, ID_VARIABLE],
        },
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
            path: HELPER_TYPE_PREFIX,
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
            path: NEED_PREFIX,
            queries: [PROJECT_HELPER_TYPE_ID_VARIABLE, DATE_VARIABLE],
        },
        get: {
            method: 'GET',
            path: NEED_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            path: NEED_PREFIX,
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
            path: STORE_PREFIX,
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
            path: STORE_PREFIX,
        },
        update: {
            method: 'PUT',
            path: STORE_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        delete: {
            method: 'DELETE',
            path: STORE_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
    slot: {
        getByStore: {
            method: 'GET',
            path: SLOT_PREFIX,
            queries: [FREE_TEXT_VARIABLE, NAME_VARIABLE, DESCRIPTION_VARIABLE, STORE_ID_VARIABLE],
        },
        getById: {
            method: 'GET',
            path: SLOT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            path: SLOT_PREFIX,
        },
        update: {
            method: 'PUT',
            path: SLOT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        delete: {
            method: 'DELETE',
            path: SLOT_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
    },
    item: {
        get: {
            method: 'GET',
            path: ITEM_PREFIX,
            queries: [FREE_TEXT_VARIABLE],
        },
        getById: {
            method: 'GET',
            path: ITEM_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        createNew: {
            method: 'POST',
            path: ITEM_PREFIX,
        },
        update: {
            method: 'PUT',
            path: ITEM_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        patch: {
            method: 'PATCH',
            path: ITEM_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        delete: {
            method: 'DELETE',
            path: ITEM_PREFIX + ID_EXTENSION,
            parameters: [ID_VARIABLE],
        },
        getNotes: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_NOTES,
            parameters: [ITEM_ID_VARIABLE],
        },
        getNotesUser: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_NOTES_USER,
            parameters: [ITEM_ID_VARIABLE, NOTE_ID_VARIABLE],
        },
        createNotes: {
            method: 'POST',
            path: ITEM_PREFIX + ITEM_NOTES,
            parameters: [ITEM_ID_VARIABLE],
        },
        deleteNotes: {
            method: 'DELETE',
            path: ITEM_PREFIX + ITEM_NOTES_ID,
            parameters: [ITEM_ID_VARIABLE, NOTE_ID_VARIABLE],
        },
        getTags: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_TAGS,
            parameters: [ITEM_ID_VARIABLE],
        },
        createTag: {
            method: 'POST',
            path: ITEM_PREFIX + ITEM_TAGS,
            parameters: [ITEM_ID_VARIABLE],
        },
        deleteTag: {
            method: 'DELETE',
            path: ITEM_PREFIX + ITEM_TAGS_ID,
            parameters: [ITEM_ID_VARIABLE, ID_VARIABLE],
        },
        getHistory: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_HISTORY,
            parameters: [ITEM_ID_VARIABLE],
        },
        getHistoryUser: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_HISTORY_USER,
            parameters: [ITEM_ID_VARIABLE, ID_VARIABLE],
        },
        getImage: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_IMAGE,
            parameters: [ITEM_ID_VARIABLE],
        },
        createImage: {
            method: 'POST',
            path: ITEM_PREFIX + ITEM_IMAGE,
            parameters: [ITEM_ID_VARIABLE],
        },
        updateImage: {
            method: 'PUT',
            path: ITEM_PREFIX + ITEM_IMAGE_ID,
            parameters: [ITEM_ID_VARIABLE, ID_VARIABLE],
        },
        getRelatedItems: {
            method: 'GET',
            path: ITEM_PREFIX + ITEM_ITEMS,
            parameters: [ITEM_ID_VARIABLE],
        },
        createItemRelation: {
            method: 'POST',
            path: ITEM_PREFIX + ITEM_ITEMS,
            parameters: [ITEM_ID_VARIABLE],
        },
        deleteItemRelation: {
            method: 'DELETE',
            path: ITEM_PREFIX + ITEM_ITEMS_ID,
            parameters: [ITEM_ID_VARIABLE, ID_VARIABLE],
        },
    },
    itemTag: {
        get: {
            method: 'GET',
            path: ITEM_TAG_PREFIX,
        },
    },
    technicalCrew: {
        get: {
            method: 'GET',
            path: TECHNICAL_CREW_PREFIX,
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
