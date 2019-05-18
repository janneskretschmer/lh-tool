import { getBasePath } from './config';

export const fullPathOf = (subPath) => (getBasePath() || '/') + (subPath || '');


export const fullPathOfLogin = () => fullPathOf('/login/');

export const fullPathOfProjects = () => fullPathOf('/projects/');

export const fullPathOfNeeds = () => fullPathOf('/needs/');

export const fullPathOfStores = () => fullPathOf('/stores/');

export const fullPathOfItems = () => fullPathOf('/items');

export const fullPathOfItem = (id) => fullPathOf('/items/' + (id ? id : ':id'));

export const fullPathOfChangePw = () => fullPathOf('/changepw/');
