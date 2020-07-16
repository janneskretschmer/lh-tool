import React from 'react';
import { fetchItems, fetchItemNotes, fetchItem, fetchItemTagsByItem, fetchItemHistory, createItemNote, deleteItemNote, fetchItemNotesUser, fetchItemHistoryUser, updateItemBrokenState, updateItemSlot, createItemTag, deleteItemTag } from '../actions/item';
import { SessionContext } from './session-provider';
import { convertToIdMap } from '../util';
import { fetchStore, fetchOwnStores } from '../actions/store';
import { fetchSlotsByStore } from '../actions/slot';
import { fetchTechnicalCrews } from '../actions/technical-crew';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import { fetchUser } from '../actions/user';
import { withSnackbar } from 'notistack';
import _ from 'lodash';
import { fetchItemTags } from '../actions/item-tags';

export const ItemsContext = React.createContext();

class StatefulItemsProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            items: new Map(),
            stores: null,
            slots: null,
            technicalCrews: null,
            tags: null,
            users: new Map(),

            selectedItem: null,
            note: '',
            tag: '',

            selectedStoreId: null,
            selectedSlotId: null,

            actionsDisabled: false,
        };
    }

    showErrorMessage(message) {
        this.props.enqueueSnackbar(message, {
            variant: 'error',
        });
    }

    componentDidMount() {
        // FUTURE: don't load everything and only if allowed (store keeper)
        this.loadItems();
        this.loadStores();
        this.loadSlots();
        this.loadTechnicalCrews();
        this.loadItemTags();
    }

    componentDidUpdate() {
        const item = this.state.selectedItem;
        const accessToken = this.props.sessionState.accessToken;
        if (item) {
            const loadNotes = !item.hasOwnProperty('notes');
            const loadTags = !item.hasOwnProperty('tags');
            const loadHistory = !item.hasOwnProperty('history');
            if (loadNotes || loadTags || loadHistory) {
                // if this setState gets split up, componentDidUpdate will be called multiple times 
                // => the other setStates and its callbacks with api-calls will be called multiple times
                this.setState(prevState => {
                    return {
                        selectedItem: {
                            ..._.cloneDeep(prevState.selectedItem),
                            notes: loadNotes ? null : prevState.selectedItem.notes,
                            tags: loadTags ? null : prevState.selectedItem.tags,
                            history: loadHistory ? null : prevState.selectedItem.history,
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
                                }))

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
                    if (loadHistory) {
                        this.loadHistory(item);
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
            identifier: Date.now().toString(36),
            name: '',
            outsideQualified: false,
            pictureUrl: '',
            quantity: 1,
            unit: 'Stück',
            width: '',
        };
    }

    loadItems() {
        fetchItems(this.props.sessionState.accessToken)
            .then(receivedItems => this.setState({
                items: convertToIdMap(receivedItems),
            }));
    }

    loadStores() {
        fetchOwnStores(this.props.sessionState.accessToken)
            .then(receivedStores => this.setState({
                stores: convertToIdMap(receivedStores),
            }));
    }

    loadSlots() {
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
            }));
    }

    loadTechnicalCrews() {
        fetchTechnicalCrews(this.props.sessionState.accessToken)
            .then(receivedTechnicalCrews => this.setState({
                technicalCrews: convertToIdMap(receivedTechnicalCrews),
            }));
    }

    loadItemTags() {
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
            }));
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
                if (updatedItem.id == item.id) {
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

    loadHistory(item) {
        const { accessToken } = this.props.sessionState;
        fetchItemHistory(accessToken, item.id)
            .then(history => {
                // approaches to load users this without the callback directly in componentDidUpdate resulted in too many setState-calls
                let eventsWithMissingUsers = history.filter(event => event.userId && !this.state.users.has(event.userId));
                eventsWithMissingUsers = _.uniq(eventsWithMissingUsers, 'userId');

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
                        .catch(error => this.showErrorMessage('Fehler beim Laden der Historie'));
                }
                return history;
            })
            .then(history => this.setState(prevState => {
                const updatedItem = _.cloneDeep(prevState.selectedItem);
                if (updatedItem.id === item.id) {
                    updatedItem.history = history;
                }

                const items = _.cloneDeep(prevState.items);
                items.get(item.id).history = history;

                return {
                    items,
                    selectedItem: updatedItem,
                };
            }))
            .catch(error => this.showErrorMessage('Fehler beim Laden der Historie'));
    }

    selectItem(itemId, handleFailure) {
        if (!itemId) {
            return;
        }

        if (itemId === NEW_ENTITY_ID_PLACEHOLDER) {
            this.setState({
                selectedItem: this.createEmptyItem()
            });
            return;
        }
        const parsedItemId = parseInt(itemId);
        if (this.state.items.has(parsedItemId)) {
            this.setState({
                selectedItem: this.state.items.get(parsedItemId),
            });
        } else {
            fetchItem(this.props.sessionState.accessToken, parsedItemId)
                .then(item => this.handleUpdatedAndSelectedItem(item))
                .catch(error => this.showErrorMessage('Fehler beim Laden des Artikels'));
        }
    }

    handleUpdatedAndSelectedItem(item) {
        this.setState(prevState => {
            const items = _.cloneDeep(prevState.items);
            items.set(item.id, item);

            const selectedStoreId = prevState.slots && prevState.slots.get(item.slotId) && prevState.slots.get(item.slotId).storeId
            return {
                items,
                selectedItem: item,
                selectedSlotId: item.slotId,
                selectedStoreId,
            };
        });
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
            quantity: item.quantity + ' ' + item.unit,
            slotName: slot && slot.name,
            storeName: store && store.name,
            technicalCrewName: technicalCrew && technicalCrew.name,
            history,
            notes,
        }
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
                    }
                    const items = _.cloneDeep(prevState.items);
                    items.get(itemId).broken = savedBrokenState;

                    return {
                        items,
                        selectedItem: item,
                        actionsDisabled: false,
                    };
                }))
                .then(() => this.loadHistory(this.state.selectedItem))
                .catch(() => this.showErrorMessage('Fehler beim Ändern des Artikelzustands'))
        );
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

                    const tags = _.cloneDeep(prevState.tags).map(cachedTag => cachedTag.name === itemTag.name ? extendedItemTag : cachedTag);
                    return {
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

                return {
                    selectedItem: item,
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
        if (this.state.selectedSlotId && this.state.selectedSlotId != this.state.selectedItem.slotId) {
            const itemId = this.state.selectedItem.id;
            this.setState({ actionsDisabled: true }, () =>
                updateItemSlot(this.props.sessionState.accessToken, itemId, this.state.selectedSlotId).then(slotId => this.setState(prevState => {
                    const item = _.cloneDeep(prevState.selectedItem);
                    if (item.id === itemId) {
                        item.slotId = slotId;
                    }
                    const items = _.cloneDeep(prevState.items);
                    items.get(itemId).slotId = slotId;

                    return {
                        items,
                        selectedItem: item,
                        actionsDisabled: false,
                    };
                }))
                    .then(() => this.loadHistory(this.state.selectedItem))
                    .catch(() => this.showErrorMessage('Fehler beim Ändern des Lagerplatzes'))
            );
        }
    }

    render() {
        return (
            <ItemsContext.Provider
                value={{
                    ...this.state,
                    selectItem: this.selectItem.bind(this),
                    getAssembledItemList: this.getAssembledItemList.bind(this),
                    getSelectedItem: this.getSelectedItem.bind(this),

                    saveNote: this.saveNote.bind(this),
                    deleteNote: this.deleteNote.bind(this),
                    changeNote: this.changeNote.bind(this),

                    changeTag: this.changeTag.bind(this),
                    saveTagName: this.saveTagName.bind(this),
                    deleteTag: this.deleteTag.bind(this),

                    saveBrokenState: this.saveBrokenState.bind(this),

                    getSlotsBySelectedStore: this.getSlotsBySelectedStore.bind(this),
                    changeSelectedSlot: this.changeSelectedSlot.bind(this),
                    changeSelectedStore: this.changeSelectedStore.bind(this),
                    saveSlot: this.saveSlot.bind(this),
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