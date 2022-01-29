import {
    Button, CircularProgress, FormControl,
    InputLabel, MenuItem, Select, TextField
} from '@mui/material';
import { withSnackbar } from 'notistack';
import React from 'react';
import { fullPathOfSlots, fullPathOfStoreSettings, fullPathOfStoresSettings } from '../../paths';
import { PageContext } from '../../providers/page-provider';
import { StoresContext } from '../../providers/store-provider';
import { requiresLogin } from '../../util';
import IdNameSelect from '../util/id-name-select';
import LenientRedirect from '../util/lenient-redirect';


@withSnackbar
class StatefulStoreEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            redirect: null,
        };
    }

    componentDidMount() {
        this.props.storesState.selectStore(this.props.match.params.id);
    }

    componentDidUpdate() {
        const store = this.props.storesState.selectedStore;
        if (!this.state.redirect && store && store.id && parseInt(this.props.match.params.id, 10) !== store.id) {
            this.setState({ redirect: fullPathOfStoreSettings(store.id) });
        }
        if (store && this.props.pagesState.currentItemName !== store.name) {
            this.props.pagesState.setCurrentItemName(store);
        }
    }

    save() {
        this.props.storesState.saveSelectedStore();
    }

    cancel() {
        this.props.storesState.resetSelectedStore();
    }

    redirectToSlots() {
        this.setState({ redirect: fullPathOfSlots(this.props.storesState.selectedStore.id) });
    }

    redirectToStores() {
        this.setState({ redirect: fullPathOfStoresSettings() });
    }

    render() {
        const { storesState } = this.props;
        const store = storesState.getSelectedStoreAssembled();
        const disabled = !storesState.isStoreValid();

        if (this.state.redirect) {
            return (<LenientRedirect to={this.state.redirect} onSamePage={() => this.setState({ redirect: null })} />);
        }

        if (!store) {
            return (<CircularProgress />);
        }

        return (<>
            <TextField
                sx={{ mr: 1, mb: 1 }}
                id="name"
                label="Name"
                value={store.name}
                onChange={event => storesState.changeName(event.target.value)}
                size="small"
                variant="outlined"
            />
            <IdNameSelect
                label="Typ"
                value={store.type}
                onChange={value => storesState.changeType(value)}
                // TODO: REST-Endpoint
                data={[{ id: 'STANDARD', name: 'Lager' }, { id: 'MOBILE', name: 'Magazin' }, { id: 'MAIN', name: 'Hauptlager' }]}
            />
            <br />
            <TextField
                sx={{ mr: 1, mb: 1, width: '230px' }}
                id="address"
                label="Adresse"
                multiline
                value={store.address}
                onChange={event => storesState.changeAddress(event.target.value)}
                size="small"
                variant="outlined"
            /><br />
            {storesState.changed ? (
                storesState.actionInProgress ? (<CircularProgress />) : (<>
                    <Button
                        sx={{ mr: 1, mb: 1 }}
                        disabled={disabled || storesState.actionInProgress}
                        variant="contained"
                        onClick={() => this.save()}
                    >
                        Speichern
                    </Button>
                    <Button
                        sx={{ mr: 1, mb: 1 }}
                        disabled={storesState.actionInProgress}
                        variant="outlined"
                        onClick={() => this.cancel()}
                    >
                        Abbrechen
                    </Button>
                </>)
            ) : (<>
                <Button
                    sx={{ mr: 1, mb: 1 }}
                    disabled={storesState.actionInProgress}
                    variant="contained"
                    onClick={() => this.redirectToStores()}
                >
                    Übersicht
                </Button>
                {store.id && (
                    <Button
                        sx={{ mr: 1, mb: 1 }}
                        disabled={storesState.actionInProgress}
                        variant="contained"
                        onClick={() => this.redirectToSlots()}
                    >
                        Lagerplätze
                    </Button>
                )}
            </>)}
        </>);
    }
}

// TODO: Project selection for rentals

const StoreEditComponent = props => (
    <PageContext.Consumer>
        {pagesState => (
            <StoresContext.Consumer>
                {storesState => (
                    <StatefulStoreEditComponent {...props} pagesState={pagesState} storesState={storesState} />
                )}
            </StoresContext.Consumer>
        )}
    </PageContext.Consumer>
);
export default requiresLogin(StoreEditComponent);
