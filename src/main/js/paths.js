import { getBasePath } from './config';

export const fullPathOf = (subPath) => (getBasePath() || '/') + (subPath || '');

export const fullPathOfHome = () => fullPathOf('/');

export const fullPathOfLogin = () => fullPathOf('/login/');

export const fullPathOfHeartbeat = () => fullPathOf('/heartbeat/');

export const fullPathOfProjects = () => fullPathOf('/projects/');
