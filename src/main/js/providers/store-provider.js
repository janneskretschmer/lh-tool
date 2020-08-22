import React from 'react';
import { SessionContext } from './session-provider';
import { fetchOwnStores, fetchStore, createStore, updateStore, deleteStore } from '../actions/store';
import { convertToIdMap, isAnyStringBlank, wrapSetStateInPromise } from '../util';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import _ from 'lodash';
import { withSnackbar } from 'notistack';
import { fetchSlotsByStore } from '../actions/slot';

export const StoresContext = React.createContext();

@withSnackbar
class StatefulStoresProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            stores: new Map(),
            loadedForCurrentFilter: false,
            actionInProgress: false,
            changed: false,
            selectedStore: null,
        }
    }

    showErrorMessage(message) {
        this.props.enqueueSnackbar(message, { variant: 'error', });
    }

    handleError(error) {
        this.showErrorMessage('Fehler beim aktualisieren der Lager');
    }


    loadStores() {
        if (!this.state.loadedForCurrentFilter) {
            return fetchOwnStores(this.props.sessionState.accessToken).then(receivedStores => this.setState({
                stores: convertToIdMap(receivedStores),
                loadedForCurrentFilter: true,
            }))
                .catch(error => this.showErrorMessage('Fehler beim Laden der Lager'));
        }
    }

    createEmptyStore() {
        return {
            name: '',
            address: '',
            type: 'STANDARD',
            slots: [],
        }
    }

    selectStore(id) {
        if (!id) {
            return null;
        }
        if (id === NEW_ENTITY_ID_PLACEHOLDER) {
            return wrapSetStateInPromise(this, prevState => ({ selectedStore: this.createEmptyStore() }));
        }
        const parsedId = parseInt(id, 10);
        if (this.state.stores.has(parsedId)) {
            return this.handleStoreUpdatedOrSelected(this.state.stores.get(parsedId));
        } else {
            return fetchStore(this.props.sessionState.accessToken, parsedId).then(store =>
                this.handleStoreUpdatedOrSelected(store)
            )
                .catch(error => this.showErrorMessage('Fehler beim Laden des Lagers'));
        }
    }

    handleStoreUpdatedOrSelected(store) {
        return wrapSetStateInPromise(this, prevState => {
            const stores = _.cloneDeep(prevState.stores);
            stores.set(store.id, store);

            const selectedStore = store;

            return {
                stores,
                selectedStore,
                actionInProgress: false,
                changed: false,
            };
        });
    }

    isStoreValid() {
        const store = this.state.selectedStore;
        return store && !isAnyStringBlank([store.name, store.address, store.type])
    }

    saveSelectedStore() {
        return wrapSetStateInPromise(this, prevState => ({ actionInProgress: true })).then(() => {
            const store = this.state.selectedStore;
            if (store.id) {
                return updateStore(this.props.sessionState.accessToken, store);
            }
            return createStore(this.props.sessionState.accessToken, store);
        }).then(savedStore => this.handleStoreUpdatedOrSelected(savedStore));
    }

    resetSelectedStore() {
        if (this.state.selectedStore) {
            return this.selectStore(this.state.selectedStore.id);
        }
    }

    getAssembledStoreList() {
        return [...this.state.stores.values()].map(store => this.getAssembledStore(store));
    }

    getSelectedStoreAssembled() {
        return this.getAssembledStore(this.state.selectedStore);
    }

    getAssembledStore(store) {
        if (!store) {
            return null;
        }
        let typeName = '';
        if (store.type === 'MAIN') {
            typeName = 'Hauptlager';
        } else if (store.type === 'STANDARD') {
            typeName = 'Lager';
        } else if (store.type === 'MOBILE') {
            typeName = 'Magazin';
        }
        return {
            ...store,
            typeName,
        }
    }

    changeName(name) {
        this.setState(prevState => ({
            selectedStore: {
                ...prevState.selectedStore,
                name,
                changed: prevState.selectedStore.name !== prevState.stores.get(prevState.selectedStore.id).name,
            }
        }));
    }

    changeAddress(address) {
        this.setState(prevState => ({
            selectedStore: {
                ...prevState.selectedStore,
                address,
                changed: prevState.selectedStore.address !== prevState.stores.get(prevState.selectedStore.id).address,
            }
        }));
    }

    changeType(type) {
        this.setState(prevState => ({
            selectedStore: {
                ...prevState.selectedStore,
                type,
                changed: prevState.selectedStore.type !== prevState.stores.get(prevState.selectedStore.id).type,
            }
        }));
    }



    bulkDeleteStores(storeIds) {
        if (!storeIds || storeIds.length < 1) {
            return;
        }
        this.setState({ actionInProgress: true }, () => Promise.all(storeIds.map(storeId => deleteStore(this.props.sessionState.accessToken, storeId).then(() => storeId)))
            .then(deletedIds => this.setState(prevState => {
                const stores = _.cloneDeep(prevState.stores);
                deletedIds.forEach(deletedId => stores.delete(deletedId));
                return ({
                    stores,
                    actionInProgress: false,
                });
            }))
            .catch(error => {
                if (error.response && error.response.key && error.response.key === 'EX_STORE_NOT_EMPTY') {
                    this.showErrorMessage('Mindestens eines der Lager ist nicht leer');
                } else {
                    this.showErrorMessage('Fehler beim Löschen der Lager');
                }
                this.setState({ actionInProgress: false });
            }));
    }

    render() {
        return (
            <StoresContext.Provider
                value={{
                    ...this.state,
                    loadStores: this.loadStores.bind(this),
                    getAssembledStoreList: this.getAssembledStoreList.bind(this),
                    isStoreValid: this.isStoreValid.bind(this),
                    saveSelectedStore: this.saveSelectedStore.bind(this),
                    resetSelectedStore: this.resetSelectedStore.bind(this),
                    bulkDeleteStores: this.bulkDeleteStores.bind(this),

                    selectStore: this.selectStore.bind(this),
                    getSelectedStoreAssembled: this.getSelectedStoreAssembled.bind(this),

                    changeName: this.changeName.bind(this),
                    changeAddress: this.changeAddress.bind(this),
                    changeType: this.changeType.bind(this),
                }}
            >
                {this.props.children}
            </StoresContext.Provider>
        );
    }
}


const StoresProvider = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (<StatefulStoresProvider {...props} sessionState={sessionState} />)}
        </SessionContext.Consumer>
    </>
);
export default StoresProvider;