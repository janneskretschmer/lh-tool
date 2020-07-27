import { getBasePath } from './config';

export const fullPathOf = (subPath) => (getBasePath() || '/') + (subPath || '');


export const fullPathOfLogin = () => fullPathOf('/login/');

export const partialPathOfNeed = () => fullPathOf('/needs');
export const fullPathOfNeedQuantities = () => fullPathOf('/needs/quantities/');
export const fullPathOfNeedApply = () => fullPathOf('/needs/apply/');
export const fullPathOfNeedApprove = () => fullPathOf('/needs/approve/');

export const fullPathOfStores = () => fullPathOf('/stores/');
export const fullPathOfStore = (id) => fullPathOf('/stores/' + (id ? id : ':id'));

export const fullPathOfSlots = () => fullPathOf('/slots');
export const fullPathOfSlot = (id) => fullPathOf('/slots/' + (id ? id : ':id'));

export const fullPathOfItems = () => fullPathOf('/items');
export const fullPathOfItem = (id) => fullPathOf('/items/' + (id ? id : ':id'));

export const fullPathOfChangePw = () => fullPathOf('/changepw/');

export const fullPathOfImprint = () => fullPathOf('/imprint/');
export const fullPathOfDataProtection = () => fullPathOf('/data-protection/');

export const fullPathOfSettings = () => fullPathOf('/settings');
export const fullPathOfUsersSettings = () => fullPathOf('/settings/users');
export const fullPathOfUserSettings = (id) => fullPathOf('/settings/users/' + (id ? id : ':userId'));

export const fullPathOfProjectsSettings = () => fullPathOf('/settings/projects');
export const fullPathOfProjectSettings = (id) => fullPathOf('/settings/projects/' + (id ? id : ':projectId'));
