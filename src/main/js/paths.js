import { getBasePath } from './config';

export const fullPathOf = (subPath) => (getBasePath() || '/') + (subPath || '');


export const fullPathOfLogin = () => fullPathOf('/login/');

export const fullPathOfProjects = () => fullPathOf('/projects/');

export const fullPathOfNeedQuantities = () => fullPathOf('/needs/quantities/');

export const fullPathOfNeedApply = () => fullPathOf('/needs/apply/');

export const fullPathOfNeedApprove = () => fullPathOf('/needs/approve/');

export const fullPathOfStores = () => fullPathOf('/stores/');
export const fullPathOfStore = (id) => fullPathOf('/stores/' + (id ? id : ':id'));

export const fullPathOfItems = () => fullPathOf('/items/');
export const fullPathOfItem = (id) => fullPathOf('/items/' + (id ? id : ':id'));

export const fullPathOfStores = () => fullPathOf('/stores/');

export const fullPathOfItems = () => fullPathOf('/items');

export const fullPathOfItem = (id) => fullPathOf('/items/' + (id ? id : ':id'));

export const fullPathOfChangePw = () => fullPathOf('/changepw/');

export const fullPathOfImprint = () => fullPathOf('/imprint/');
export const fullPathOfDataProtection = () => fullPathOf('/data-protection/');
