import React from 'react';
import { withStyles, CircularProgress, TextField, FormControl, InputLabel, Select, MenuItem, Button, Typography, Link } from '@material-ui/core';
import StoresProvider, { StoresContext } from '../../providers/store-provider';
import { requiresLogin } from '../../util';
import { PageContext } from '../../providers/page-provider';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../../config';
import LenientRedirect from '../util/lenient-redirect';
import { fullPathOfStoreSettings, fullPathOfStoresSettings, fullPathOf, fullPathOfSlots } from '../../paths';
import { withSnackbar } from 'notistack';
import PagedTable from '../table';


const styles = theme => ({
    input: {
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },

});


@withStyles(styles)
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
        const { classes } = this.props;

        if (this.state.redirect) {
            return (<LenientRedirect to={this.state.redirect} onSamePage={() => this.setState({ redirect: null })} />);
        }

        if (!store) {
            return (<CircularProgress />);
        }

        return (<>
            <TextField
                className={classes.input}
                id="name"
                label="Name"
                value={store.name}
                onChange={event => storesState.changeName(event.target.value)}
                margin="dense"
                variant="outlined"
            />
            <FormControl
                className={classes.input}
                variant="outlined"
            >
                <InputLabel htmlFor="type">Typ</InputLabel>
                <Select
                    variant="outlined"
                    value={store.type}
                    onChange={event => storesState.changeType(event.target.value)}
                    inputProps={{
                        name: 'type',
                        id: 'type',
                    }}
                >
                    <MenuItem value={'STANDARD'}>Lager</MenuItem>
                    <MenuItem value={'MOBILE'}>Magazin</MenuItem>
                    <MenuItem value={'MAIN'}>Hauptlager</MenuItem>
                </Select>
            </FormControl>
            <br />
            <TextField
                className={classes.input}
                id="address"
                label="Adresse"
                multiline
                value={store.address}
                onChange={event => storesState.changeAddress(event.target.value)}
                margin="dense"
                variant="outlined"
            /><br />
            {storesState.changed ? (
                storesState.actionInProgress ? (<CircularProgress />) : (<>
                    <Button
                        className={classes.input}
                        disabled={disabled || storesState.actionInProgress}
                        variant="contained"
                        onClick={() => this.save()}
                    >
                        Speichern
                    </Button>
                    <Button
                        className={classes.input}
                        disabled={storesState.actionInProgress}
                        variant="outlined"
                        onClick={() => this.cancel()}
                    >
                        Abbrechen
                    </Button>
                </>)
            ) : (<>
                <Button
                    className={classes.input}
                    disabled={storesState.actionInProgress}
                    variant="contained"
                    onClick={() => this.redirectToStores()}
                >
                    Übersicht
                </Button>
                {store.id && (
                    <Button
                        className={classes.input}
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
