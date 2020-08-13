import React from 'react';
import { fetchItems, fetchItemNotes, fetchItem, fetchItemTagsByItem, fetchItemHistory, createItemNote, deleteItemNote, fetchItemNotesUser, fetchItemHistoryUser, updateItemBrokenState, updateItemSlot, createItemTag, deleteItemTag, updateItem, createItem, updateItemQuantity, fetchItemImage, createItemImage, updateItemImage, deleteItem, fetchRelatedItems, createItemRelation, deleteItemRelation } from '../actions/item';
import { SessionContext } from './session-provider';
import { convertToIdMap, generateUniqueId, isAnyStringBlank, base64toBlob, isStringBlank } from '../util';
import { fetchStore, fetchOwnStores } from '../actions/store';
import { fetchSlotsByStore } from '../actions/slot';
import { fetchTechnicalCrews } from '../actions/technical-crew';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import { fetchUser } from '../actions/user';
import { withSnackbar } from 'notistack';
import _ from 'lodash';
import { fetchItemTags } from '../actions/item-tags';
import imageCompression from 'browser-image-compression';
import { PageContext } from './page-provider';

export const ItemsContext = React.createContext();

class StatefulItemsProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            items: new Map(),
            users: new Map(),
            // state.hasOwnProperty needs to be false in the following cases
            // stores: null,
            // slots: null,
            // technicalCrews: null,
            // tags: null,

            selectedItem: null,
            edit: false,
            note: '',
            tag: '',
            modifiedQuantity: null,
            modifiedImage: null,

            selectedStoreId: null,
            selectedSlotId: null,

            copyIdentifier: '',
            copyHasBarcode: false,

            actionsDisabled: false,

            loadedItemsForCurrentFilter: false,
            filterFreeText: '',
        };
    }

    showErrorMessage(message) {
        this.props.enqueueSnackbar(message, {
            variant: 'error',
        });
    }

    handleFailure(error) {
        if (error && error.response && error.response.key) {
            if (error.response.key === 'EX_ITEM_NO_SLOT') {
                this.showErrorMessage('Es wurde kein gültiger Lagerplatz angegeben.');
            } else if (error.response.key === 'EX_ITEM_NO_IDENTIFIER') {
                this.showErrorMessage('Es wurde kein gültiger Bezeichner angegeben.');
            } else if (error.response.key === 'EX_ITEM_IDENTIFIER_ALREADY_IN_USE') {
                this.showErrorMessage('Der Bezeichner wird bereits verwendet');
            } else if (error.response.key === 'EX_ITEM_NO_NAME') {
                this.showErrorMessage('Es wurde kein gültiger Name angegeben.');
            } else if (error.response.key === 'EX_ITEM_NO_TECHNICAL_CREW') {
                this.showErrorMessage('Es wurde kein gültiges Gewerk angegeben.');
            }
        } else {
            this.showErrorMessage('Fehler beim Aktualisieren des Artikels.');
        }
    }

    componentDidUpdate() {
        const item = this.state.selectedItem;
        const accessToken = this.props.sessionState.accessToken;
        if (item && item.id) {
            const loadNotes = !item.hasOwnProperty('notes');
            const loadTags = !item.hasOwnProperty('tags');
            const loadRelatedItems = !item.hasOwnProperty('items');
            if (loadNotes || loadTags || loadRelatedItems) {
                // if this setState gets split up, componentDidUpdate will be called multiple times 
                // => the other setStates and its callbacks with api-calls will be called multiple times
                this.setState(prevState => {
                    return {
                        selectedItem: {
                            ..._.cloneDeep(prevState.selectedItem),
                            notes: loadNotes ? null : prevState.selectedItem.notes,
                            tags: loadTags ? null : prevState.selectedItem.tags,
                            items: loadRelatedItems ? null : prevState.selectedItem.items,
                        }
                    };
                }, () => {
                    if (loadNotes) {
                        this.loadNotes(item);
                    }
                    if (loadTags) {
                        fetchItemTagsByItem(accessToken, item.id)
                            .then(receivedTags => this.setState(prevState => {
                                //necessary for react-select lib
                                const tags = receivedTags.map(tag => ({
                                    ...tag,
                                    value: tag.name,
                                    label: tag.name,
                                }));

                                const updatedItem = _.cloneDeep(prevState.selectedItem);
                                updatedItem.tags = tags;

                                const items = _.cloneDeep(prevState.items);
                                items.get(updatedItem.id).tags = tags;
                                return {
                                    items,
                                    selectedItem: updatedItem,
                                };
                            }))
                            .catch(error => this.showErrorMessage('Fehler beim Laden der Schlagwörter'));
                    }
                    if (loadRelatedItems) {
                        fetchRelatedItems(accessToken, item.id)
                            .then(relatedItems => this.setState(prevState => {

                                const updatedItem = _.cloneDeep(prevState.selectedItem);
                                if (updatedItem.id === item.id) {
                                    updatedItem.items = relatedItems;
                                }

                                const items = _.cloneDeep(prevState.items);
                                items.get(item.id).items = relatedItems;

                                return {
                                    items,
                                    selectedItem: updatedItem,
                                };
                            }))
                            .catch(error => this.showErrorMessage('Fehler beim Laden der zugehörigen Artikel'));
                    }
                });
            }
        }
    }

    createEmptyItem() {
        return {
            broken: false,
            consumable: false,
            depth: '',
            description: '',
            hasBarcode: false,
            height: '',
            identifier: generateUniqueId(),
            name: '',
            outsideQualified: false,
            pictureUrl: '',
            quantity: 1,
            unit: 'Stück',
            width: '',
        };
    }

    isItemValid() {
        const item = this.state.selectedItem;
        return !isAnyStringBlank([item.name, item.identifier]) && item.technicalCrewId && this.state.technicalCrews && this.state.technicalCrews.has(item.technicalCrewId) && this.state.selectedSlotId && this.state.slots && this.state.slots.has(this.state.selectedSlotId);
    }

    loadItems(keepSelection) {
        if (!this.state.loadedItemsForCurrentFilter) {
            fetchItems(this.props.sessionState.accessToken, this.state.filterFreeText)
                .then(receivedItems => this.setState(prevState => {
                    const items = convertToIdMap(receivedItems);
                    // otherwise already loaded data of the selected item will be lost and saving deltas will result in problems 
                    if (keepSelection && prevState.selectedItem && prevState.items.has(prevState.selectedItem.id)) {
                        items.set(prevState.selectedItem.id, prevState.items.get(prevState.selectedItem.id));
                    }
                    return {
                        items,
                        loadedItemsForCurrentFilter: true,
                    };
                }));
            this.loadItemTags();
            this.loadTechnicalCrews();
            this.loadStores();
            this.loadSlots();
        }
        if (!keepSelection) {
            this.setState({
                selectedSlotId: null,
                selectedStoreId: null,
                edit: false,
            });
        }

    }

    loadStores() {
        if (!this.state.hasOwnProperty('stores')) {
            this.setState({ stores: null }, () =>
                fetchOwnStores(this.props.sessionState.accessToken)
                    .then(receivedStores => this.setState({
                        stores: convertToIdMap(receivedStores),
                    }))
            );
        }
    }

    loadSlots() {
        if (!this.state.hasOwnProperty('slots')) {
            this.setState({ slots: null }, () =>
                // FUTURE: don't load everything and only if allowed (store keeper)
                fetchSlotsByStore(this.props.sessionState.accessToken)
                    .then(receivedSlots => this.setState(prevState => {
                        const slots = convertToIdMap(receivedSlots);
                        const selectedSlotId = prevState.selectedItem && prevState.selectedItem.slotId;
                        const selectedStoreId = selectedSlotId && slots.has(selectedSlotId) && slots.get(selectedSlotId).storeId;
                        return {
                            selectedSlotId,
                            selectedStoreId,
                            slots,
                        };
                    }))
            );
        }
    }

    loadTechnicalCrews() {
        if (!this.state.hasOwnProperty('technicalCrews')) {
            this.setState({ technicalCrews: null }, () =>
                fetchTechnicalCrews(this.props.sessionState.accessToken)
                    .then(receivedTechnicalCrews => this.setState({
                        technicalCrews: convertToIdMap(receivedTechnicalCrews),
                    }))
            );
        }
    }

    loadItemTags() {
        if (!this.state.hasOwnProperty('tags') && this.props.sessionState.hasPermission('ROLE_RIGHT_ITEM_TAGS_GET')) {
            this.setState({ tags: null }, () =>
                fetchItemTags(this.props.sessionState.accessToken)
                    .then(tags => this.setState({
                        tags: tags.map(
                            tag => ({
                                ...tag,
                                // necessary for react-select lib
                                value: tag.name,
                                label: tag.name,
                            })
                        )
                    }))
            );
        }
    }

    loadNotes(item) {
        const { accessToken } = this.props.sessionState;
        fetchItemNotes(accessToken, item.id)
            .then(notes => {
                // approaches to load users this without the callback directly in componentDidUpdate resulted in too many setState-calls
                let notesWithMissingUsers = notes.filter(note => note.userId && !this.state.users.has(note.userId));
                notesWithMissingUsers = _.uniq(notesWithMissingUsers, 'userId');

                if (notesWithMissingUsers.length > 0) {
                    Promise.all(
                        notesWithMissingUsers.map(note => fetchItemNotesUser(accessToken, note))
                    ).then(receivedUsers =>
                        this.setState(prevState => {
                            const users = _.cloneDeep(prevState.users);
                            receivedUsers.forEach(user => users.set(user.id, user));
                            return { users };
                        })
                    )
                        .catch(error => this.showErrorMessage('Fehler beim Laden der Notizen'));
                }

                return notes;
            })
            .then(notes => this.setState(prevState => {

                const updatedItem = _.cloneDeep(prevState.selectedItem);
                if (updatedItem.id === item.id) {
                    updatedItem.notes = notes;
                }

                const items = _.cloneDeep(prevState.items);
                items.get(item.id).notes = notes;

                return {
                    items,
                    selectedItem: updatedItem,
                };
            }))
            .catch(error => this.showErrorMessage('Fehler beim Laden der Notizen'));
    }

    selectItem(itemId) {
        if (!itemId) {
            return;
        }

        this.loadItemTags();
        this.loadTechnicalCrews();
        this.loadStores();
        this.loadSlots();

        if (itemId === NEW_ENTITY_ID_PLACEHOLDER) {
            this.setState({
                selectedItem: this.createEmptyItem(),
                edit: true,
            });
            return;
        }
        const parsedItemId = parseInt(itemId, 10);
        if (this.state.items.has(parsedItemId)) {
            this.handleUpdatedAndSelectedItem(this.state.items.get(parsedItemId));
        } else {
            fetchItem(this.props.sessionState.accessToken, parsedItemId)
                .then(item => this.handleUpdatedAndSelectedItem(item))
                .catch(error => this.showErrorMessage('Fehler beim Laden des Artikels'));
        }
    }

    changeEdit(edit) {
        this.setState({ edit });
    }

    handleUpdatedAndSelectedItem(item, image) {
        this.setState(prevState => {
            const items = _.cloneDeep(prevState.items);
            items.set(item.id, item);

            if (prevState.selectedItem && prevState.selectedItem.id === item.id) {
                item.notes = item.notes || prevState.selectedItem.notes;
                item.tags = item.tags || prevState.selectedItem.tags;
                // history should get updated bc it maybe changed
            }
            if (image) {
                item.imageId = image.id;
                item.imageUrl = URL.createObjectURL(base64toBlob(image.image, image.mediaType));
            }

            const selectedStoreId = prevState.slots && prevState.slots.get(item.slotId) && prevState.slots.get(item.slotId).storeId;
            return {
                items,
                selectedItem: item,
                selectedSlotId: item.slotId,
                selectedStoreId,
                edit: false,
                modifiedImage: null,
            };
        }, () => {
            if (!this.state.selectedItem.hasOwnProperty('imageUrl')) {
                fetchItemImage(this.props.sessionState.accessToken, this.state.selectedItem.id)
                    .then(image => this.setState(prevState => {
                        if (!image.image) {
                            return {};
                        }
                        const selectedItem = _.cloneDeep(prevState.selectedItem);
                        if (selectedItem.id === image.itemId) {
                            selectedItem.imageId = image.id;
                            selectedItem.imageUrl = URL.createObjectURL(base64toBlob(image.image, image.mediaType));
                        }
                        return {
                            selectedItem,
                        };
                    }));
            }
        });
    }

    selectItemHistory(itemId) {
        if (itemId === NEW_ENTITY_ID_PLACEHOLDER) {
            return;
        }
        if (!itemId || isNaN(itemId)) {
            this.showErrorMessage('Artikel nicht gefunden');
            return;
        }


        const parsedItemId = parseInt(itemId, 10);
        if (this.state.items.has(parsedItemId)) {
            this.setState({ selectedItem: this.state.items.get(parsedItemId) },
                () => this.loadHistory());
        } else {
            fetchItem(this.props.sessionState.accessToken, parsedItemId)
                .then(item => this.setState(prevState => {
                    const items = _.cloneDeep(prevState.items);
                    items.set(item.id, item);
                    return {
                        items,
                        selectedItem: item,
                    };
                }, () => this.loadHistory()))
                .catch(error => this.showErrorMessage('Fehler beim Laden des Artikels'));
        }
    }

    loadHistory() {
        const item = this.state.selectedItem;
        if (item.history) {
            return;
        }
        if (this.state.items.has(item.id) && this.state.items.get(item.id).history) {
            this.setState(prevState => {
                const selectedItem = _.cloneDeep(prevState.selectedItem);
                selectedItem.history = prevState.items.get(selectedItem.id).history;
                return { selectedItem };
            });
            return;
        }

        const { accessToken } = this.props.sessionState;
        fetchItemHistory(accessToken, item.id)
            .then(history => {
                // approaches to load users this without the callback directly in componentDidUpdate resulted in too many setState-calls
                let eventsWithMissingUsers = history.filter(event => event.userId && !this.state.users.has(event.userId));
                eventsWithMissingUsers = _.uniqWith(eventsWithMissingUsers, (a, b) => a.userId === b.userId);

                if (eventsWithMissingUsers.length > 0) {
                    Promise.all(
                        eventsWithMissingUsers.map(event => fetchItemHistoryUser(accessToken, event))
                    ).then(receivedUsers =>
                        this.setState(prevState => {
                            const users = _.cloneDeep(prevState.users);
                            receivedUsers.forEach(user => users.set(user.id, user));
                            return { users };
                        })
                    )
                        .catch(error => this.showErrorMessage('Fehler beim Laden des Protokolls'));
                }
                return history;
            })
            .then(history => this.setState(prevState => {
                const updatedItem = _.cloneDeep(prevState.selectedItem);
                if (updatedItem.id === item.id) {
                    updatedItem.history = history;
                }

                const items = _.cloneDeep(prevState.items);
                items.get(updatedItem.id).history = history;

                return {
                    items,
                    selectedItem: updatedItem,
                };
            }))
            .catch(error => this.showErrorMessage('Fehler beim Laden des Protokolls'));
    }

    getSelectedItem() {
        return this.getAssembledItem(this.state.selectedItem);
    }

    getAssembledItemList() {
        return this.state.items && [...this.state.items.values()].map(item => {
            return this.getAssembledItem(item);
        });
    }

    getAssembledItem(item) {
        if (!item) {
            return null;
        }

        const slot = this.state.slots && this.state.slots.get(item.slotId);
        const store = slot && this.state.stores && this.state.stores.get(slot.storeId);
        const technicalCrew = this.state.technicalCrews && this.state.technicalCrews.get(item.technicalCrewId);
        const history = item.history && item.history.map(event => ({
            ...event,
            user: this.state.users.get(event.userId),
        }));
        const notes = item.notes && item.notes.map(note => ({
            ...note,
            user: this.state.users.get(note.userId),
        }));

        const state = item.broken ? 'Defekt' : 'Verfügbar';

        return {
            ...item,
            state,
            // popularity: item.rentals.length,
            quantityWithUnit: item.quantity + ' ' + item.unit,
            slotName: slot && slot.name,
            storeName: store && store.name,
            technicalCrewName: technicalCrew && technicalCrew.name,
            history,
            notes,
            depth: item.depth || '',
            height: item.height || '',
            width: item.width || '',
        };
    }

    deleteSelectedItem(callback) {
        const item = this.state.selectedItem;
        if (!item || !item.id) {
            return;
        }
        this.setState({ actionsDisabled: true }, () => deleteItem(this.props.sessionState.accessToken, item.id)
            .then(() => this.setState(prevState => {
                const items = _.cloneDeep(prevState.items);
                items.delete(item.id);
                return {
                    items,
                    selectedItem: null,
                    actionsDisabled: false,
                };
            }, () => callback()))
            .catch(error => {
                this.showErrorMessage('Fehler beim Löschen des Artikels');
                this.setState({ actionsDisabled: false });
            }));
    }

    bulkDeleteItems(itemIds) {
        if (!itemIds || itemIds.length < 1) {
            return;
        }
        this.setState({ actionsDisabled: true }, () => Promise.all(itemIds.map(itemId => deleteItem(this.props.sessionState.accessToken, itemId).then(() => itemId)))
            .then(deletedIds => this.setState(prevState => {
                const items = _.cloneDeep(prevState.items);
                deletedIds.forEach(deletedId => items.delete(deletedId));
                return ({
                    items,
                    actionsDisabled: false,
                });
            }))
            .catch(error => {
                this.showErrorMessage('Fehler beim Löschen der Artikel');
                this.setState({ actionsDisabled: false });
            }));
    }

    changeItemIdentifier(identifier) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.identifier = identifier;
            return { selectedItem };
        });
    }

    changeItemHasBarcode(hasBarcode) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.hasBarcode = hasBarcode;
            return { selectedItem };
        });
    }

    changeItemName(name) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.name = name;
            return { selectedItem };
        });
    }

    changeItemDescription(description) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.description = description;
            return { selectedItem };
        });
    }

    changeItemQuantity(quantity) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.quantity = quantity;
            return { selectedItem };
        });
    }

    changeItemUnit(unit) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.unit = unit;
            return { selectedItem };
        });
    }

    changeItemWidth(width) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.width = width;
            return { selectedItem };
        });
    }

    changeItemHeight(height) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.height = height;
            return { selectedItem };
        });
    }

    changeItemDepth(depth) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.depth = depth;
            return { selectedItem };
        });
    }

    changeItemOutsideQualified(outsideQualified) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.outsideQualified = outsideQualified;
            return { selectedItem };
        });
    }

    changeItemConsumable(consumable) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.consumable = consumable;
            return { selectedItem };
        });
    }

    changeItemTechnicalCrewId(technicalCrewId) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            selectedItem.technicalCrewId = technicalCrewId;
            return { selectedItem };
        });
    }

    saveSelectedItem() {
        const item = this.state.selectedItem;
        if (this.state.selectedSlotId) {
            item.slotId = this.state.selectedSlotId;
        }
        let itemPromise;
        if (item.id) {
            itemPromise = updateItem(this.props.sessionState.accessToken, item);
        } else {
            itemPromise = createItem(this.props.sessionState.accessToken, item);
        }
        return itemPromise
            .then(savedItem => Promise.all([
                ...item.items ? item.items.filter(item1 => !this.state.items.has(item.id) || !this.state.items.get(item.id).items.find(item2 => item1.id === item2.id))
                    .map(relatedItem => createItemRelation(this.props.sessionState.accessToken, savedItem.id, relatedItem.id)) : [],
                ...this.state.items.has(item.id) && this.state.items.get(item.id).items ? this.state.items.get(item.id).items.filter(item1 => !item.items || !item.items.find(item2 => item1.id === item2.id))
                    .map(relatedItem => deleteItemRelation(this.props.sessionState.accessToken, savedItem.id, relatedItem.id)) : []
            ]).then(() => ({
                ...savedItem,
                items: item.items || [],
            })))
            .then(savedItem => {
                if (this.state.modifiedImage) {

                    if (this.state.modifiedImage.id) {
                        return updateItemImage(this.props.sessionState.accessToken, this.state.modifiedImage)
                            .then(savedImage => this.handleUpdatedAndSelectedItem(savedItem, savedImage));
                    }

                    return createItemImage(this.props.sessionState.accessToken, { ...this.state.modifiedImage, itemId: savedItem.id })
                        .then(savedImage => this.handleUpdatedAndSelectedItem(savedItem, savedImage));
                } else {
                    this.handleUpdatedAndSelectedItem(savedItem);
                }
                return savedItem;
            })
            .catch(error => this.handleFailure(error));
    }

    resetSelectedItem() {
        this.selectItem(this.state.selectedItem.id);
    }



    saveNote() {
        const { selectedItem, note } = this.state;
        createItemNote(this.props.sessionState.accessToken, {
            itemId: selectedItem.id,
            userId: this.props.sessionState.currentUser.id,
            note,
        })
            .then(itemNote => this.setState(prevState => {
                const item = _.cloneDeep(prevState.selectedItem);
                if (item.id === itemNote.itemId) {
                    item.notes = [itemNote, ...item.notes];
                }
                const items = _.cloneDeep(prevState.items);
                items.get(itemNote.itemId).notes = [itemNote, ...items.get(itemNote.itemId).notes];

                const users = _.cloneDeep(prevState.users);
                users.set(this.props.sessionState.currentUser.id, this.props.sessionState.currentUser);

                return {
                    note: '',
                    items,
                    users,
                    selectedItem: item,
                };
            }))
            .catch(error => this.showErrorMessage('Fehler beim Speichern der Notiz'));
    }

    deleteNote(itemNote) {
        deleteItemNote(this.props.sessionState.accessToken, itemNote)
            .then(() => this.setState(prevState => {
                const item = _.cloneDeep(prevState.selectedItem);
                if (item.id === itemNote.itemId) {
                    item.notes = item.notes.filter(cachedNote => cachedNote.id !== itemNote.id);
                }
                const items = _.cloneDeep(prevState.items);
                items.get(itemNote.itemId).notes = items.get(itemNote.itemId).notes.filter(cachedNote => cachedNote.id !== itemNote.id);

                return {
                    items,
                    selectedItem: item,
                };
            }))
            .catch(error => this.showErrorMessage('Fehler beim Löschen der Notiz'));
    }

    saveBrokenState(broken) {
        const itemId = this.state.selectedItem.id;
        this.setState({ actionsDisabled: true }, () =>
            updateItemBrokenState(this.props.sessionState.accessToken, itemId, broken)
                .then(savedBrokenState => this.setState(prevState => {
                    const item = _.cloneDeep(prevState.selectedItem);
                    if (item.id === itemId) {
                        item.broken = savedBrokenState;
                        //causes update of history
                        delete item.history;
                    }
                    const items = _.cloneDeep(prevState.items);
                    items.get(itemId).broken = savedBrokenState;
                    delete items.get(itemId).history;

                    return {
                        items,
                        selectedItem: item,
                        actionsDisabled: false,
                    };
                }))
                .catch(() => this.showErrorMessage('Fehler beim Ändern des Artikelzustands'))
        );
    }

    bulkUpdateBrokenState(itemIds, broken, finallyCallback) {
        const promises = itemIds.filter(itemId => this.state.items.get(itemId).broken ^ broken)
            .map(itemId => updateItemBrokenState(this.props.sessionState.accessToken, itemId, broken)
                .then(savedBrokenState => ({
                    ...this.state.items.get(itemId),
                    broken: savedBrokenState,
                }))
            );
        if (promises.length > 0) {
            this.setState({ actionsDisabled: true }, () =>
                Promise.all(promises)
                    .then(updatedItems => this.setState(prevState => {
                        const items = _.cloneDeep(prevState.items);
                        updatedItems.forEach(item => {
                            delete item.history;
                            items.set(item.id, item);
                        });
                        return {
                            items,
                            actionsDisabled: false,
                        };
                    }))
                    .catch(error => this.showErrorMessage('Fehler beim Ändern der Artikelzustände'))
                    .finally(() => finallyCallback())
            );
        } else {
            finallyCallback();
        }
    }


    changeNote(note) {
        this.setState({ note });
    }

    changeTag(tag) {
        if (tag && tag.length > 0 && /\s/.test(tag)) {
            this.saveTagName(this.state.tag);
        } else {
            this.setState({ tag });
        }
    }

    saveTagName(tagName) {
        if (this.state.selectedItem.tags && !this.state.selectedItem.tags.find(itemTag => itemTag.name === tagName)) {
            this.setState(prevState => {
                const tagObject = { name: tagName, value: tagName, label: tagName };

                const item = _.cloneDeep(prevState.selectedItem);
                item.tags.push(tagObject);

                return {
                    selectedItem: item,
                    tag: '',
                };
            }, () => createItemTag(this.props.sessionState.accessToken, this.state.selectedItem.id, { name: tagName })
                .then(itemTag => this.setState(prevState => {
                    const extendedItemTag = {
                        ...itemTag,
                        value: itemTag.name,
                        label: itemTag.name,
                    };

                    const item = _.cloneDeep(prevState.selectedItem);
                    item.tags = item.tags.map(cachedTag => cachedTag.name === itemTag.name ? extendedItemTag : cachedTag);

                    const items = _.cloneDeep(prevState.items);
                    items.get(item.id).tags = item.tags;

                    const tags = _.cloneDeep(prevState.tags).map(cachedTag => cachedTag.name === itemTag.name ? extendedItemTag : cachedTag);
                    return {
                        items,
                        selectedItem: item,
                        tags,
                    };
                })).catch(error => this.showErrorMessage('Fehler beim Speichern des Schlagworts "' + tagName + '"')));
        }
    }

    deleteTag({ id, name }) {
        if (id) {
            this.setState(prevState => {
                const item = _.cloneDeep(prevState.selectedItem);
                item.tags = item.tags.filter(tag => tag.id !== id);

                const items = _.cloneDeep(prevState.items);
                items.get(item.id).tags = item.tags;

                return {
                    selectedItem: item,
                    items,
                };
            }, () => deleteItemTag(this.props.sessionState.accessToken, this.state.selectedItem.id, id)
                .catch(error => this.showErrorMessage('Fehler beim Löschen des Schlagworts "' + name + '"')));
        }
    }

    getSlotsBySelectedStore() {
        return this.getSlotsByStoreId(this.state.selectedStoreId);
    }
    getSlotsByStoreId(storeId) {
        return this.state.slots && [...this.state.slots.values()].filter(slot => slot.storeId === storeId);
    }

    changeSelectedStore(selectedStoreId) {
        const slots = this.getSlotsByStoreId(selectedStoreId);
        const selectedSlotId = slots && slots.length > 0 && slots[0].id;
        this.setState({ selectedStoreId, selectedSlotId });
    }

    changeSelectedSlot(selectedSlotId) {
        this.setState({ selectedSlotId });
    }

    saveSlot() {
        if (this.state.selectedSlotId && this.state.selectedSlotId !== this.state.selectedItem.slotId) {
            const itemId = this.state.selectedItem.id;
            this.setState({ actionsDisabled: true }, () =>
                updateItemSlot(this.props.sessionState.accessToken, itemId, this.state.selectedSlotId).then(slotId => this.setState(prevState => {
                    const item = _.cloneDeep(prevState.selectedItem);
                    if (item.id === itemId) {
                        item.slotId = slotId;
                        //causes update of history
                        delete item.history;
                    }
                    const items = _.cloneDeep(prevState.items);
                    items.get(itemId).slotId = slotId;
                    delete items.get(itemId).history;

                    return {
                        items,
                        selectedItem: item,
                        actionsDisabled: false,
                    };
                }))
                    .catch(() => this.showErrorMessage('Fehler beim Ändern des Lagerplatzes'))
            );
        }
    }

    bulkSaveSlot(itemIds, finallyCallback) {
        const slotId = this.state.selectedSlotId;
        if (!slotId) {
            return;
        }
        const promises = itemIds.filter(itemId => this.state.items.get(itemId).slotId !== slotId)
            .map(itemId => updateItemSlot(this.props.sessionState.accessToken, itemId, slotId)
                .then(savedSlotId => ({
                    ...this.state.items.get(itemId),
                    slotId: savedSlotId,
                }))
            );
        if (promises.length > 0) {
            this.setState({ actionsDisabled: true }, () =>
                Promise.all(promises)
                    .then(updatedItems => this.setState(prevState => {
                        const items = _.cloneDeep(prevState.items);
                        updatedItems.forEach(item => {
                            delete item.history;
                            items.set(item.id, item);
                        });
                        return {
                            items,
                            actionsDisabled: false,
                        };
                    }))
                    .catch(error => this.showErrorMessage('Fehler beim Verschieben der Artikel'))
                    .finally(() => finallyCallback())
            );
        } else {
            finallyCallback();
        }
    }


    changeCopyIdentifier(copyIdentifier) {
        this.setState({ copyIdentifier });
    }

    changeCopyHasBarcode(copyHasBarcode) {
        this.setState({ copyHasBarcode });
    }

    copySelectedItem() {
        if (!this.state.copyIdentifier || isStringBlank(this.state.copyIdentifier)) {
            this.showErrorMessage('Der eindeutige Bezeichner darf nicht leer sein.');
            return;
        }
        this.setState(prevState => ({
            selectedItem: {
                ...prevState.selectedItem,
                id: null,
                identifier: prevState.copyIdentifier,
                hasBarcode: prevState.copyHasBarcode,
                history: null,
            }
        }), () => this.saveSelectedItem());
    }


    editQuantity() {
        this.changeModifiedQuantity(this.state.selectedItem.quantity);
    }

    resetQuantity() {
        this.changeModifiedQuantity(null);
    }

    changeModifiedQuantity(modifiedQuantity) {
        this.setState({ modifiedQuantity });
    }

    saveQuantity() {
        const { modifiedQuantity, selectedItem } = this.state;
        if (modifiedQuantity) {
            if (modifiedQuantity === selectedItem.quantity) {
                this.changeModifiedQuantity(null);
            } else {
                this.setState({ actionsDisabled: true }, () => {
                    const itemId = selectedItem.id;
                    updateItemQuantity(this.props.sessionState.accessToken, itemId, modifiedQuantity).then(quantity => this.setState(prevState => {
                        const item = _.cloneDeep(prevState.selectedItem);
                        if (item.id === itemId) {
                            item.quantity = quantity;
                            //causes update of history
                            delete item.history;
                        }
                        const items = _.cloneDeep(prevState.items);
                        items.get(itemId).quantity = quantity;
                        delete items.get(itemId).history;

                        return {
                            items,
                            selectedItem: item,
                            actionsDisabled: false,
                            modifiedQuantity: null,
                        };
                    }));
                });
            }
        }
    }

    changeImage(uploadedFile) {
        if (uploadedFile) {
            if (uploadedFile.type === 'image/jpeg' || uploadedFile.type === 'image/png') {
                imageCompression(uploadedFile, {
                    maxSizeMB: 1, //max size 1MB
                    quality: 0.75,
                    maxWidthOrHeight: 512,
                    resize: true,
                }).then(resizedImage => {
                    this.setState(prevState => {
                        const item = _.cloneDeep(prevState.selectedItem);
                        item.imageUrl = URL.createObjectURL(resizedImage);
                        item.imageName = resizedImage.name;
                        return {
                            selectedItem: item,
                        };
                    });
                    return imageCompression.getDataUrlFromFile(resizedImage);
                }).then(base64 => this.setState(prevState => ({
                    modifiedImage: {
                        id: prevState.selectedItem.imageId,
                        itemId: prevState.selectedItem.id,
                        //base64 is an URL like data:image/...;base64,...
                        image: base64.substr(base64.indexOf(';base64,') + 8),
                        mediaType: uploadedFile.type,
                    },
                })));
            } else {
                this.showErrorMessage('Es können nur Bilder mit den Endungen .jpg und .png hochgeladen werden.');
            }
        }
    }


    changeFreeTextFilter(filterFreeText) {
        this.setState({ filterFreeText, loadedItemsForCurrentFilter: false });
    }


    toggleItemRelation(itemId) {
        this.setState(prevState => {
            const selectedItem = _.cloneDeep(prevState.selectedItem);
            if (!selectedItem.items) {
                selectedItem.items = [prevState.items.get(itemId)];
            } else if (selectedItem.items.find(item => item.id === itemId)) {
                selectedItem.items = selectedItem.items.filter(item => item.id !== itemId);
            } else {
                selectedItem.items.push(prevState.items.get(itemId));
            }
            return {
                selectedItem,
            };
        });
    }

    render() {
        return (
            <ItemsContext.Provider
                value={{
                    ...this.state,

                    loadItems: this.loadItems.bind(this),

                    selectItem: this.selectItem.bind(this),
                    selectItemHistory: this.selectItemHistory.bind(this),
                    getAssembledItemList: this.getAssembledItemList.bind(this),
                    getSelectedItem: this.getSelectedItem.bind(this),
                    saveSelectedItem: this.saveSelectedItem.bind(this),
                    resetSelectedItem: this.resetSelectedItem.bind(this),
                    changeEdit: this.changeEdit.bind(this),
                    isItemValid: this.isItemValid.bind(this),
                    deleteSelectedItem: this.deleteSelectedItem.bind(this),
                    bulkDeleteItems: this.bulkDeleteItems.bind(this),

                    changeItemConsumable: this.changeItemConsumable.bind(this),
                    changeItemDepth: this.changeItemDepth.bind(this),
                    changeItemDescription: this.changeItemDescription.bind(this),
                    changeItemHasBarcode: this.changeItemHasBarcode.bind(this),
                    changeItemHeight: this.changeItemHeight.bind(this),
                    changeItemIdentifier: this.changeItemIdentifier.bind(this),
                    changeItemName: this.changeItemName.bind(this),
                    changeItemOutsideQualified: this.changeItemOutsideQualified.bind(this),
                    changeItemQuantity: this.changeItemQuantity.bind(this),
                    changeItemTechnicalCrewId: this.changeItemTechnicalCrewId.bind(this),
                    changeItemUnit: this.changeItemUnit.bind(this),
                    changeItemWidth: this.changeItemWidth.bind(this),

                    saveNote: this.saveNote.bind(this),
                    deleteNote: this.deleteNote.bind(this),
                    changeNote: this.changeNote.bind(this),

                    changeTag: this.changeTag.bind(this),
                    saveTagName: this.saveTagName.bind(this),
                    deleteTag: this.deleteTag.bind(this),

                    saveBrokenState: this.saveBrokenState.bind(this),
                    bulkUpdateBrokenState: this.bulkUpdateBrokenState.bind(this),

                    getSlotsBySelectedStore: this.getSlotsBySelectedStore.bind(this),
                    changeSelectedSlot: this.changeSelectedSlot.bind(this),
                    changeSelectedStore: this.changeSelectedStore.bind(this),
                    saveSlot: this.saveSlot.bind(this),
                    bulkSaveSlot: this.bulkSaveSlot.bind(this),

                    editQuantity: this.editQuantity.bind(this),
                    changeModifiedQuantity: this.changeModifiedQuantity.bind(this),
                    saveQuantity: this.saveQuantity.bind(this),
                    resetQuantity: this.resetQuantity.bind(this),

                    changeCopyIdentifier: this.changeCopyIdentifier.bind(this),
                    changeCopyHasBarcode: this.changeCopyHasBarcode.bind(this),
                    copySelectedItem: this.copySelectedItem.bind(this),

                    changeImage: this.changeImage.bind(this),

                    changeFreeTextFilter: this.changeFreeTextFilter.bind(this),

                    toggleItemRelation: this.toggleItemRelation.bind(this),
                }}
            >
                {this.props.children}
            </ItemsContext.Provider>
        );
    }
}

const ItemsProvider = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (<StatefulItemsProvider {...props} sessionState={sessionState} />)}
        </SessionContext.Consumer>
    </>
);
export default withSnackbar(ItemsProvider);