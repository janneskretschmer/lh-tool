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
        if (store && this.props.pagesState.currentItemName !== store.name) {
            this.props.pagesState.setCurrentItemName(store);
        }
    }

    save() {
        this.props.storesState.saveSelectedStore()
            .then(() => this.setState({ redirect: fullPathOfStoresSettings() }))
            .then(() => this.props.enqueueSnackbar('Lager erfolgreich gespeichert', { variant: 'success' }))
            .catch(() => this.props.enqueueSnackbar('Fehler beim Speichern des Lagers', { variant: 'error' }));
    }

    cancel() {
        this.props.storesState.resetSelectedStore()
            .then(() => this.setState({ redirect: fullPathOfStoresSettings() }));
    }

    redirectToSlots() {
        this.setState({ redirect: fullPathOfSlots() })
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
                    onClick={() => this.cancel()}
                >
                    Übersicht
                </Button>
                <Button
                    className={classes.input}
                    disabled={storesState.actionInProgress}
                    variant="contained"
                    onClick={() => this.redirectToSlots()}
                >
                    Lagerplätze
                </Button>
            </>)}
            <br />
            {store.slots && (
                <PagedTable
                    title="Lagerplätze"
                    SelectionHeader={props => (<>hi</>)}
                    headers={[
                        {
                            key: 'name',
                            name: 'Name',
                        },
                        {
                            key: 'width',
                            name: 'Breite (cm)',
                            unimportant: true,
                        },
                        {
                            key: 'height',
                            name: 'Höhe (cm)',
                            unimportant: true,
                        },
                        {
                            key: 'depth',
                            name: 'Tiefe (cm)',
                            unimportant: true,
                        },
                        {
                            key: 'outside',
                            name: 'Draußen',
                            converter: outside => outside ? 'Ja' : 'Nein'
                        },
                    ]}
                    rows={store.slots}
                    showAddButton={true}
                />
            )}
        </>)
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
