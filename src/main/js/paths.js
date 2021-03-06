import { getBasePath } from './config';
import { STORE_ID_VARIABLE } from './urlmappings';

export function fullPathOf(subPath, queryMap) {
    let path = (getBasePath() || '/') + (subPath || '');
    const queryString = queryMap && Array.from(queryMap.entries())
        .filter(query => query[1])
        .map(query => `${query[0]}=${query[1]}`)
        .join('&');
    if (queryString) {
        path += '?' + queryString;
    }
    return path;
}

export const fullPathOfLogin = () => fullPathOf('/login/');

export const partialPathOfNeed = () => fullPathOf('/needs');
export const fullPathOfNeedQuantities = () => fullPathOf('/needs/quantities/');
export const fullPathOfNeedApply = () => fullPathOf('/needs/apply/');
export const fullPathOfNeedApprove = () => fullPathOf('/needs/approve/');

export const fullPathOfSlots = (filterStoreId) => fullPathOf('/slots', new Map([[STORE_ID_VARIABLE, filterStoreId]]));
export const fullPathOfSlot = (id) => fullPathOf('/slots/' + (id ? id : ':id'));

export const fullPathOfItems = () => fullPathOf('/items');
export const fullPathOfItem = (id) => fullPathOf('/items/' + (id ? id : ':id'));
export const fullPathOfItemData = (id) => fullPathOfItem(id) + '/data';
export const fullPathOfItemHistory = (id) => fullPathOfItem(id) + '/history';

export const fullPathOfChangePw = () => fullPathOf('/changepw/');

export const fullPathOfImprint = () => fullPathOf('/imprint/');
export const fullPathOfDataProtection = () => fullPathOf('/data-protection/');

export const fullPathOfSettings = () => fullPathOf('/settings');
export const fullPathOfUsersSettings = () => fullPathOf('/settings/users');
export const fullPathOfUserSettings = (id) => fullPathOf('/settings/users/' + (id ? id : ':userId'));

export const fullPathOfProjectsSettings = () => fullPathOf('/settings/projects');
export const fullPathOfProjectSettings = (id) => fullPathOf('/settings/projects/' + (id ? id : ':projectId'));

export const fullPathOfStoresSettings = () => fullPathOf('/settings/stores');
export const fullPathOfStoreSettings = (id) => fullPathOf('/settings/stores/' + (id ? id : ':id'));
