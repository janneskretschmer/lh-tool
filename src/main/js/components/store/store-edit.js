import React from 'react';
import { withStyles, CircularProgress, TextField, FormControl, InputLabel, Select, MenuItem, Button } from '@material-ui/core';
import StoresProvider, { StoresContext } from '../../providers/store-provider';
import { requiresLogin } from '../../util';
import { PageContext } from '../../providers/page-provider';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../../config';
import LenientRedirect from '../util/lenient-redirect';
import { fullPathOfStoreSettings, fullPathOfStoresSettings } from '../../paths';


const styles = theme => ({
    button: {
        marginRight: theme.spacing.unit,
    },
    bold: {
        fontWeight: '500',
    },
    title: {
        fontSize: '30px',
        marginBottom: '10px',
    },
    container: {
        display: 'inline-block',
        verticalAlign: 'top',
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        marginTop: theme.spacing.unit,
    },
    verticalCenteredContainer: {
        display: 'flex',
        alignItems: 'baseline',
    },
    margin: {
        margin: theme.spacing.unit,
    }

});


@withStyles(styles)
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
        // if (!this.state.redirect && store && store.id && parseInt(this.props.match.params.projectId, 10) !== store.id) {
        //     this.setState({ redirect: fullPathOfStoreSettings(store.id) });
        // }
        if (store && this.props.pagesState.currentItemName !== store.name) {
            this.props.pagesState.setCurrentItemName(store);
        }
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
                id="name"
                label="Name"
                value={store.name}
                onChange={event => storesState.changeName(event.target.value)}
                margin="dense"
                variant="outlined"
            />&nbsp;
            <FormControl
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
            </FormControl><br />
            <TextField
                id="address"
                label="Adresse"
                multiline
                value={store.address}
                onChange={event => storesState.changeAddress(event.target.value)}
                margin="dense"
                variant="outlined"
            /><br />
            <Button
                disabled={disabled || storesState.actionsDisabled}
                variant="contained"
                onClick={() => storesState.saveSelectedStore()}
            >
                Speichern
            </Button>
            <Button
                disabled={storesState.actionsDisabled}
                variant="outlined"
                onClick={() => storesState.resetSelectedStore()}
            >
                Abbrechen
            </Button>
        </>)
    }
}

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
