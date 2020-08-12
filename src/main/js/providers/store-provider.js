import React from 'react';
import { SessionContext } from './session-provider';
import { fetchOwnStores, fetchStore, createStore, updateStore } from '../actions/store';
import { convertToIdMap, isAnyStringBlank } from '../util';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import _ from 'lodash';
import { withSnackbar } from 'notistack';

export const StoresContext = React.createContext();

@withSnackbar
class StatefulStoresProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            stores: new Map(),
            loadedForCurrentFilter: false,
            actionsDisabled: false,
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
        }
    }

    selectStore(id) {
        if (!id) {
            return null;
        }
        if (id === NEW_ENTITY_ID_PLACEHOLDER) {
            this.setState({ selectedStore: this.createEmptyStore() });
            return;
        }
        const parsedId = parseInt(id, 10);
        if (this.state.stores.has(parsedId)) {
            this.handleStoreUpdatedOrSelected(this.state.stores.get(parsedId));
        } else {
            fetchStore(this.props.sessionState.accessToken, parsedId).then(store =>
                this.handleStoreUpdatedOrSelected(store)
            )
                .catch(error => this.showErrorMessage('Fehler beim Laden des Lagers'));
        }
    }

    handleStoreUpdatedOrSelected(store) {
        this.setState(prevState => {
            const stores = _.cloneDeep(prevState.stores);
            stores.set(store.id, store);

            const selectedStore = store;

            return {
                stores,
                selectedStore,
                actionsDisabled: false,
            };
        })
    }

    isStoreValid() {
        const store = this.state.selectedStore;
        return store && !isAnyStringBlank([store.name, store.address, store.type])
    }

    saveSelectedStore() {
        this.setState({ actionsDisabled: true }, () => {
            const store = this.state.selectedStore;
            let storePromise;
            if (store.id) {
                storePromise = updateStore(this.props.sessionState.accessToken, store);
            } else {
                storePromise = createStore(this.props.sessionState.accessToken, store);
            }
            storePromise.then(savedStore => this.handleStoreUpdatedOrSelected(savedStore))
                .then(() => this.props.enqueueSnackbar('Lager erfolgreich gespeichert', { variant: 'success' }))
                .catch(error => this.handleError(error));
        });
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
            }
        }));
    }

    changeAddress(address) {
        this.setState(prevState => ({
            selectedStore: {
                ...prevState.selectedStore,
                address,
            }
        }));
    }

    changeType(type) {
        this.setState(prevState => ({
            selectedStore: {
                ...prevState.selectedStore,
                type,
            }
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