import { getBasePath } from './config';

export const fullPathOf = (subPath) => (getBasePath() || '/') + (subPath || '');


export const fullPathOfLogin = () => fullPathOf('/login/');

export const fullPathOfProjects = () => fullPathOf('/projects/');

export const fullPathOfNeeds = () => fullPathOf('/needs/');

export const fullPathOfChangePw = () => fullPathOf('/changepw/');
