// Needs to be kept in sync with UrlMappings.java

export const REST_PREFIX = '/rest';

export const ID_VARIABLE = 'id';
export const USER_ID_VARIABLE = 'user_id';

export const NO_EXTENSION = '/';
export const ID_EXTENSION = '/{' + ID_VARIABLE + '}';
export const ID_USER_ID_EXTENSION = '/{' + ID_VARIABLE + '}/{' + USER_ID_VARIABLE + '}';

export const INFO_PREFIX = REST_PREFIX + '/info';
export const INFO_HEARTBEAT = '/heartbeat';

export const USER_PREFIX = REST_PREFIX + '/users';
export const USER_PASSWORD = '/password';

export const LOGIN_PEFIX = REST_PREFIX + '/login';

export const PROJECT_PREFIX = REST_PREFIX + '/projects';

export const NEED_PREFIX = REST_PREFIX + '/needs';

export const MEDIA_TYPE_JSON = 'application/json';