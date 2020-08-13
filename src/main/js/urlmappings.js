// Needs to be kept in sync with UrlMappings.java

export const REST_PREFIX = '/rest';

export const ID_VARIABLE = 'id';
export const USER_ID_VARIABLE = 'user_id';
export const PROJECT_ID_VARIABLE = 'project_id';
export const HELPER_TYPE_ID_VARIABLE = 'helper_type_id';
export const PROJECT_HELPER_TYPE_ID_VARIABLE = 'project_helper_type_id';
export const ITEM_ID_VARIABLE = 'item_id';
export const NOTE_ID_VARIABLE = 'note_id';

export const DATE_VARIABLE = 'date';
export const ROLE_VARIABLE = 'role';
export const STORE_VARIABLE = 'store';
export const WEEKDAY_VARIABLE = 'weekday';
export const START_DATE_VARIABLE = 'start_date';
export const END_DATE_VARIABLE = 'end_date';
export const FREE_TEXT_VARIABLE = 'free_text';

export const NO_EXTENSION = '';
export const ID_EXTENSION = '/{' + ID_VARIABLE + '}';
export const ID_USER_EXTENSION = ID_EXTENSION + '/users';
export const ID_USER_ID_EXTENSION = ID_USER_EXTENSION + '/{' + USER_ID_VARIABLE + '}';

export const INFO_PREFIX = REST_PREFIX + '/info';
export const INFO_HEARTBEAT = '/heartbeat';
export const INFO_TIMEZONE = '/timezone';

export const USER_PREFIX = REST_PREFIX + '/users';
export const USER_PASSWORD = '/password';
export const USER_CURRENT = '/current';
export const USER_ROLES = '/{' + ID_VARIABLE + '}/roles';
export const USER_ROLES_ID = '/{' + USER_ID_VARIABLE + '}/roles/{' + ID_VARIABLE + '}';
export const USER_PROJECTS = '/{' + ID_VARIABLE + '}/projects';

export const ROLES_PREFIX = REST_PREFIX + '/roles';

export const LOGIN_PREFIX = REST_PREFIX + '/login';
export const LOGIN_PASSWORD_RESET = '/pwreset';

export const PROJECT_PREFIX = REST_PREFIX + '/projects';
export const PROJECT_DELETE = PROJECT_PREFIX + ID_EXTENSION;
export const PROJECT_HELPER_TYPES = '/{' + PROJECT_ID_VARIABLE + '}/helper_types';
export const PROJECT_HELPER_TYPES_ID = PROJECT_HELPER_TYPES + ID_EXTENSION;

export const HELPER_TYPE_PREFIX = REST_PREFIX + '/helper_types';

export const NEED_PREFIX = REST_PREFIX + '/needs';

export const STORE_PREFIX = REST_PREFIX + '/stores';
export const STORE_PROJECTS = ID_EXTENSION + '/projects';

export const SLOT_PREFIX = REST_PREFIX + '/slots';

export const ITEM_PREFIX = REST_PREFIX + '/items';
export const ITEM_NOTES = '/{' + ITEM_ID_VARIABLE + '}/notes';
export const ITEM_NOTES_ID = ITEM_NOTES + '/{' + NOTE_ID_VARIABLE + '}';
export const ITEM_NOTES_USER = ITEM_NOTES_ID + '/user';
export const ITEM_TAGS = '/{' + ITEM_ID_VARIABLE + '}/tags';
export const ITEM_TAGS_ID = ITEM_TAGS + ID_EXTENSION;
export const ITEM_HISTORY = '/{' + ITEM_ID_VARIABLE + '}/history';
export const ITEM_HISTORY_USER = ITEM_HISTORY + '/{' + ID_VARIABLE + '}/user';
export const ITEM_IMAGE = '/{' + ITEM_ID_VARIABLE + '}/image';
export const ITEM_IMAGE_ID = ITEM_IMAGE + ID_EXTENSION;
export const ITEM_ITEMS = '/{' + ITEM_ID_VARIABLE + '}/items';
export const ITEM_ITEMS_ID = ITEM_ITEMS + ID_EXTENSION;

export const ITEM_TAG_PREFIX = REST_PREFIX + '/item_tags';

export const TECHNICAL_CREW_PREFIX = REST_PREFIX + '/technical_crews';

export const MEDIA_TYPE_JSON = 'application/json';

export const ASSEMBLED_PREFIX = REST_PREFIX + '/assembled';
export const ASSEMBLED_NEED_FOR_CALENDAR = '/needs_calendar';


