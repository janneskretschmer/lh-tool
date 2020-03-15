// Needs to be kept in sync with UrlMappings.java

export const REST_PREFIX = '/rest';

export const ID_VARIABLE = 'id';
export const USER_ID_VARIABLE = 'user_id';
export const PROJECT_ID_VARIABLE = 'project_id';
export const HELPER_TYPE_ID_VARIABLE = 'helper_type_id';
export const PROJECT_HELPER_TYPE_ID_VARIABLE = 'project_helper_type_id';

export const DATE_VARIABLE = 'date';
export const ROLE_VARIABLE = 'role';
export const STORE_VARIABLE = 'store';
export const WEEKDAY_VARIABLE = 'weekday';

export const NO_EXTENSION = '/';
export const ID_EXTENSION = '/{' + ID_VARIABLE + '}';
export const ID_USER_ID_EXTENSION = '/{' + ID_VARIABLE + '}/{' + USER_ID_VARIABLE + '}';

export const INFO_PREFIX = REST_PREFIX + '/info';
export const INFO_HEARTBEAT = '/heartbeat';

export const USER_PREFIX = REST_PREFIX + '/users';
export const USER_PASSWORD = '/password';
export const USER_CURRENT = '/current';
export const USER_ROLES = '/{' + ID_VARIABLE + '}/roles';

export const LOGIN_PREFIX = REST_PREFIX + '/login';
export const LOGIN_PASSWORD_RESET = '/pwreset';

export const PROJECT_PREFIX = REST_PREFIX + '/projects';
export const PROJECT_DELETE = PROJECT_PREFIX + ID_EXTENSION;
export const PROJECT_HELPER_TYPES = PROJECT_PREFIX + ID_EXTENSION + '/helper_types/{'
    + HELPER_TYPE_ID_VARIABLE + '}';

export const HELPER_TYPE_PREFIX = REST_PREFIX + '/helper_types';

export const NEED_PREFIX = REST_PREFIX + '/needs';

export const STORE_PREFIX = REST_PREFIX + '/stores';
export const STORE_PROJECTS = ID_EXTENSION + '/projects';

export const SLOT_PREFIX = REST_PREFIX + '/slots';

export const ITEM_PREFIX = REST_PREFIX + '/items';
export const ITEM_NOTES = ID_EXTENSION + '/notes';
export const ITEM_TAGS = ID_EXTENSION + '/tags';
export const ITEM_HISTORY = ID_EXTENSION + '/history';

export const TECHNICAL_CREW_PREFIX = REST_PREFIX + '/technical_crews';

export const MEDIA_TYPE_JSON = 'application/json';

