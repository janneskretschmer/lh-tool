import React from 'react';
import { SessionContext } from './session-provider';
import { convertToIdMap, isAnyStringBlank, wrapSetStateInPromise, getQueryParams } from '../util';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import _ from 'lodash';
import { withSnackbar } from 'notistack';
import { fetchSlotsByStore, fetchSlot, updateSlot, createSlot, deleteSlot } from '../actions/slot';
import { fetchOwnStores } from '../actions/store';
import { FREE_TEXT_VARIABLE, STORE_ID_VARIABLE, DESCRIPTION_VARIABLE, NAME_VARIABLE } from '../urlmappings';

export const SlotsContext = React.createContext();

@withSnackbar
class StatefulSlotsProvider extends React.Component {

    constructor(props) {
        super(props);

        const queryParams = getQueryParams();

        this.state = {
            slots: new Map(),
            stores: new Map(),
            loadedForCurrentFilter: false,
            actionInProgress: false,
            selectedSlot: null,
            edit: false,

            filterFreeText: queryParams.get(FREE_TEXT_VARIABLE) || '',
            filterName: queryParams.get(NAME_VARIABLE) || '',
            filterDescription: queryParams.get(DESCRIPTION_VARIABLE) || '',
            filterStoreId: queryParams.get(STORE_ID_VARIABLE),
        }
    }

    componentDidMount() {
        // stores are needed for the overview and modifications, that's most of the cases
        fetchOwnStores(this.props.sessionState.accessToken).then(receivedStores => this.setState(prevState => {
            const stores = convertToIdMap(receivedStores);

            if (prevState.selectedSlot && prevState.selectedSlot.id) {
                const selectedSlot = _.cloneDeep(prevState.selectedSlot);
                selectedSlot.store = stores.get(selectedSlot.storeId);

                const slots = _.cloneDeep(prevState.slots);
                slots.get(selectedSlot.id).store = stores.get(selectedSlot.storeId);
                return {
                    stores,
                    selectedSlot,
                    slots,
                };
            }
            return { stores };
        }));
        //TODO catch
    }

    showErrorMessage(message) {
        this.props.enqueueSnackbar(message, { variant: 'error', });
    }


    loadSlots() {
        if (!this.state.loadedForCurrentFilter) {
            const { filterFreeText, filterName, filterDescription, filterStoreId } = this.state;
            return fetchSlotsByStore(this.props.sessionState.accessToken, filterFreeText, filterName, filterDescription, filterStoreId).then(receivedSlots => this.setState({
                slots: convertToIdMap(receivedSlots),
                loadedForCurrentFilter: true,
            }))
                .catch(error => this.showErrorMessage('Fehler beim Laden der Lagerplätze'));
        }
    }

    createEmptySlot() {
        return {
            name: '',
            description: '',
            width: '',
            height: '',
            depth: '',
            outside: false,
        };
    }

    selectSlot(id) {
        if (!id) {
            return null;
        }
        if (id === NEW_ENTITY_ID_PLACEHOLDER) {
            return wrapSetStateInPromise(this, prevState => ({
                selectedSlot: this.createEmptySlot(),
                edit: true,
            }));
        }
        const parsedId = parseInt(id, 10);
        if (this.state.slots.has(parsedId)) {
            return this.handleSlotUpdatedOrSelected(this.state.slots.get(parsedId));
        } else {
            return fetchSlot(this.props.sessionState.accessToken, parsedId)
                .then(slot => this.handleSlotUpdatedOrSelected(slot))
                .catch(error => this.showErrorMessage('Fehler beim Laden des Lagerplatzes'));
        }
    }

    handleSlotUpdatedOrSelected(slot) {
        return wrapSetStateInPromise(this, prevState => {
            if (!slot.store && prevState.stores.has(slot.storeId)) {
                slot.store = prevState.stores.get(slot.storeId);
            }

            const slots = _.cloneDeep(prevState.slots);
            slots.set(slot.id, slot);

            const selectedSlot = slot;

            return {
                slots,
                selectedSlot,
                actionInProgress: false,
                edit: false,
            };
        });
    }

    // isSlotValid() {
    //     const slot = this.state.selectedSlot;
    //     //return slot && !isAnyStringBlank([slot.name, store.address, store.type])
    // }

    saveSelectedSlot() {
        return wrapSetStateInPromise(this, prevState => ({ actionInProgress: true })).then(() => {
            const slot = this.state.selectedSlot;
            if (slot.id) {
                return updateSlot(this.props.sessionState.accessToken, slot);
            }
            return createSlot(this.props.sessionState.accessToken, slot);
        })
            .then(savedSlot => this.handleSlotUpdatedOrSelected(savedSlot));
    }

    resetSelectedSlot() {
        if (this.state.selectedSlot) {
            return this.selectSlot(this.state.selectedSlot.id);
        }
    }

    changeEdit(edit) {
        this.setState({ edit });
    }

    changeName(name) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                name,
            }
        }));
    }

    changeDescription(description) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                description,
            }
        }));
    }

    changeWidth(width) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                width,
            }
        }));
    }

    changeHeight(height) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                height,
            }
        }));
    }

    changeDepth(depth) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                depth,
            }
        }));
    }

    changeOutside(outside) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                outside,
            }
        }));
    }

    changeStoreId(storeId) {
        this.setState(prevState => ({
            selectedSlot: {
                ...prevState.selectedSlot,
                storeId,
            }
        }));
    }



    bulkDeleteSlots(slotIds) {
        if (!slotIds || slotIds.length < 1) {
            return;
        }
        this.setState({ actionInProgress: true }, () => Promise.all(slotIds.map(slotId => deleteSlot(this.props.sessionState.accessToken, slotId).then(() => slotId)))
            .then(deletedIds => this.setState(prevState => {
                const slots = _.cloneDeep(prevState.slots);
                deletedIds.forEach(deletedId => slots.delete(deletedId));
                return ({
                    slots,
                    actionInProgress: false,
                });
            }))
            .catch(error => {
                if (error.response && error.response.key && error.response.key === 'EX_SLOT_NOT_EMPTY') {
                    this.showErrorMessage('Mindestens eines der Lager ist nicht leer');
                } else {
                    this.showErrorMessage('Fehler beim Löschen der Lager');
                }
                this.setState({ actionInProgress: false });
            }));
    }


    changeFilterFreeText(filterFreeText) {
        this.setState({
            filterFreeText,
            loadedForCurrentFilter: false,
        });
    }

    changeFilterName(filterName) {
        this.setState({
            filterName,
            loadedForCurrentFilter: false,
        });
    }

    changeFilterDescription(filterDescription) {
        this.setState({
            filterDescription,
            loadedForCurrentFilter: false,
        });
    }

    changeFilterStoreId(filterStoreId) {
        this.setState({
            filterStoreId,
            loadedForCurrentFilter: false,
        });
    }

    render() {
        return (
            <SlotsContext.Provider
                value={{
                    ...this.state,
                    loadSlots: this.loadSlots.bind(this),
                    selectSlot: this.selectSlot.bind(this),
                    changeEdit: this.changeEdit.bind(this),
                    saveSelectedSlot: this.saveSelectedSlot.bind(this),
                    resetSelectedSlot: this.resetSelectedSlot.bind(this),
                    bulkDeleteSlots: this.bulkDeleteSlots.bind(this),

                    changeDepth: this.changeDepth.bind(this),
                    changeDescription: this.changeDescription.bind(this),
                    changeHeight: this.changeHeight.bind(this),
                    changeName: this.changeName.bind(this),
                    changeOutside: this.changeOutside.bind(this),
                    changeStoreId: this.changeStoreId.bind(this),
                    changeWidth: this.changeWidth.bind(this),

                    changeFilterDescription: this.changeFilterDescription.bind(this),
                    changeFilterFreeText: this.changeFilterFreeText.bind(this),
                    changeFilterName: this.changeFilterName.bind(this),
                    changeFilterStoreId: this.changeFilterStoreId.bind(this),
                }}
            >
                {this.props.children}
            </SlotsContext.Provider>
        );
    }
}


const SlotsProvider = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (<StatefulSlotsProvider {...props} sessionState={sessionState} />)}
        </SessionContext.Consumer>
    </>
);
export default SlotsProvider;