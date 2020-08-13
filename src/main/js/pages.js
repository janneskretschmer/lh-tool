import { fullPathOf, fullPathOfItems, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfSettings, fullPathOfStores, fullPathOfUsersSettings, fullPathOfUserSettings, fullPathOfStore, fullPathOfDataProtection, fullPathOfImprint, fullPathOfShiftsSettings, fullPathOfShiftSettings, fullPathOfProjectsSettings, fullPathOfProjectSettings, fullPathOfItem, fullPathOfItemData, fullPathOfItemHistory } from './paths';

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
            subPages: [
                {
                    path: fullPathOfItem(),
                    permissions: ['ROLE_RIGHT_ITEMS_GET_BY_ID'],
                    tabs: true,
                    subPages: [
                        {
                            title: 'Daten',
                            path: fullPathOfItemData(),
                            permissions: ['ROLE_RIGHT_ITEMS_GET_BY_ID'],
                        },
                        {
                            title: 'Protokoll',
                            path: fullPathOfItemHistory(),
                            permissions: ['ROLE_RIGHT_ITEMS_GET_BY_ID'],
                        },
                    ],
                },
            ]
        },
        {
            title: 'Einstellungen',
            path: fullPathOfSettings(),
            tabs: true,
            subPages: [
                {
                    title: 'Benutzer',
                    path: fullPathOfUsersSettings(),
                    subPages: [
                        {
                            path: fullPathOfUserSettings(),
                            permissions: ['ROLE_RIGHT_USERS_GET_BY_ID'],
                        }
                    ]
                },
                {
                    title: 'Projekte',
                    path: fullPathOfProjectsSettings(),
                    permissions: ['ROLE_RIGHT_PROJECTS_POST'],
                    subPages: [
                        {
                            path: fullPathOfProjectSettings(),
                            permissions: ['ROLE_RIGHT_PROJECTS_POST'],
                        }
                    ]
                },
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