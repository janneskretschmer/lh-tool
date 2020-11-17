import { fullPathOf, fullPathOfItems, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfSettings, fullPathOfStores, fullPathOfUsersSettings, fullPathOfUserSettings, fullPathOfStore, fullPathOfDataProtection, fullPathOfImprint, fullPathOfShiftsSettings, fullPathOfShiftSettings, fullPathOfProjectsSettings, fullPathOfProjectSettings, fullPathOfItem, fullPathOfItemData, fullPathOfItemHistory, fullPathOfStoresSettings, fullPathOfStoreSettings, fullPathOfSlots, fullPathOfSlot } from './paths';
import { RIGHT_ITEMS_GET, RIGHT_NEEDS_APPLY, RIGHT_NEEDS_POST, RIGHT_NEEDS_VIEW_APPROVED, RIGHT_PROJECTS_POST, RIGHT_SLOTS_GET, RIGHT_STORES_POST, RIGHT_USERS_GET } from './permissions';

const PAGES = {
    title: 'LH-Tool',
    path: fullPathOf(),
    subPages: [
        {
            title: 'Bedarf',
            path: fullPathOfNeedQuantities(),
            permissions: [RIGHT_NEEDS_POST],
        },
        {
            title: 'Bewerben',
            path: fullPathOfNeedApply(),
            permissions: [RIGHT_NEEDS_APPLY],
        },
        {
            title: 'Zuteilen',
            path: fullPathOfNeedApprove(),
            permissions: [RIGHT_NEEDS_VIEW_APPROVED],

        },
        {
            title: 'Lagerplätze',
            path: fullPathOfSlots(),
            permissions: [RIGHT_SLOTS_GET],
            subPages: [
                {
                    path: fullPathOfSlot(),
                },
            ],
        },
        {
            title: 'Artikel',
            path: fullPathOfItems(),
            permissions: [RIGHT_ITEMS_GET],
            subPages: [
                {
                    path: fullPathOfItem(),
                    permissions: [RIGHT_ITEMS_GET],
                    tabs: true,
                    subPages: [
                        {
                            title: 'Daten',
                            path: fullPathOfItemData(),
                            permissions: [RIGHT_ITEMS_GET],
                        },
                        {
                            title: 'Protokoll',
                            path: fullPathOfItemHistory(),
                            permissions: [RIGHT_ITEMS_GET],
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
                            permissions: [RIGHT_USERS_GET],
                        }
                    ]
                },
                {
                    title: 'Projekte',
                    path: fullPathOfProjectsSettings(),
                    permissions: [RIGHT_PROJECTS_POST],
                    subPages: [
                        {
                            path: fullPathOfProjectSettings(),
                            permissions: [RIGHT_PROJECTS_POST],
                        }
                    ]
                },
                {
                    title: 'Lager',
                    path: fullPathOfStoresSettings(),
                    permissions: [RIGHT_STORES_POST],
                    subPages: [
                        {
                            path: fullPathOfStoreSettings(),
                            permissions: [RIGHT_STORES_POST],
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