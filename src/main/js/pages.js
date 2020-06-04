import { fullPathOf, fullPathOfItems, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfSettings, fullPathOfStores, fullPathOfUsersSettings, fullPathOfUserSettings, fullPathOfStore, fullPathOfDataProtection, fullPathOfImprint } from './paths';

const PAGES = {
    title: 'LH-Tool',
    path: fullPathOf(),
    subPages: [
        {
            title: 'Bedarf',
            path: fullPathOfNeedQuantities(),
            permissions: ['ROLE_RIGHT_NEEDS_POST'],
        },
        {
            title: 'Bewerben',
            path: fullPathOfNeedApply(),
            permissions: ['ROLE_RIGHT_NEEDS_APPLY'],
        },
        {
            title: 'Zuteilen',
            path: fullPathOfNeedApprove(),
            permissions: ['ROLE_RIGHT_NEEDS_VIEW_APPROVED'],

        },
        {
            title: 'Lager',
            path: fullPathOfStores(),
            permissions: ['ROLE_RIGHT_STORES_GET'],
            subPages: [
                {
                    path: fullPathOfStore(),
                },
            ],
        },
        {
            title: 'Artikel',
            path: fullPathOfItems(),
            permissions: ['ROLE_RIGHT_ITEMS_GET'],
        },
        {
            title: 'Einstellungen',
            path: fullPathOfSettings(),
            subPages: [
                {
                    title: 'Benutzer',
                    path: fullPathOfUsersSettings(),
                    permissions: ['ROLE_RIGHT_USERS_GET'],
                    subPages: [
                        {
                            path: fullPathOfUserSettings(),
                            permissions: ['ROLE_RIGHT_USERS_GET_BY_ID'],
                        }
                    ]
                },
                {
                    title: 'Projekte',
                    path: fullPathOfProjects(),
                }
            ],
        },
        {
            title: 'Datenschutzerklärung',
            path: fullPathOfDataProtection(),
        },
        {
            title: 'Impressum',
            path: fullPathOfImprint(),
        },

    ]
};
export default PAGES;