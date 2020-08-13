import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import InputAdornment from '@material-ui/core/InputAdornment';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import { withStyles } from '@material-ui/core/styles';
import TableCell from '@material-ui/core/TableCell';
import TextField from '@material-ui/core/TextField';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import SearchIcon from '@material-ui/icons/Search';
import React from 'react';
import { Redirect } from 'react-router';
import { fullPathOfItem, fullPathOfItemData } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import PagedTable from '../table';
import { ItemsContext } from '../../providers/items-provider';
import { CircularProgress, DialogActions, DialogContent, DialogTitle, Dialog } from '@material-ui/core';
import WithPermission from '../with-permission';
import SimpleDialog from '../simple-dialog';
import ItemSlotEditComponent from './item-slot';


const styles = theme => ({
    button: {
        margin: '7px',
    },
    link: {
        textDecoration: 'none',
    },
    formControl: {
        width: '100px',
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    searchInput: {
        marginRight: theme.spacing.unit,
    },
    new: {
        marginTop: theme.spacing.unit,
    },
    selectionText: {
        marginRight: '18px',
    },
    clickable: {
        cursor: 'pointer',
    }
});

@withStyles(styles)
class StatefulItemListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            expandFilters: false,

            slotDialogOpen: false,
        };
    }

    componentDidMount() {
        this.loadItems();
    }

    loadItems() {
        this.props.itemsState.loadItems(this.props.keepSelectedItem);
    }

    handleToggleShowFilters = () => {
        this.setState({ showFilters: !this.state.showFilters });
    }

    handleChangeSearchTechnicalCrew = event => {
        this.setState({ searchTechnicalCrew: event.target.value });
    }

    handleChangeSearchTag = event => {
        this.setState({ searchTag: event.target.value });
    }

    handleChangeSearchStore = event => {
        this.setState({ searchStore: event.target.value });
    }

    handleChangeSearchSlot = event => {
        this.setState({ searchSlot: event.target.value });
    }

    toggleSlotDialog() {
        this.setState(prevState => ({ slotDialogOpen: !prevState.slotDialogOpen }));
    }

    toggleExpandFilters() {
        this.setState(prevState => ({
            expandFilters: !prevState.expandFilters,
        }));
    }

    render() {
        const { classes, itemsState, sessionState, hideAdd, hideHeader, hiddenItemId, selected, onToggleSelect } = this.props;
        const { slotDialogOpen, expandFilters } = this.state;
        const items = itemsState.getAssembledItemList().filter(item => item.id !== hiddenItemId);
        const showAddButton = sessionState.hasPermission('ROLE_RIGHT_ITEMS_POST');

        if (this.state.redirect) {
            return (<Redirect to={fullPathOfItem(this.state.redirect)} />);
        }

        if (!items) {
            return (<CircularProgress />);
        }

        return (
            <>
                <PagedTable
                    selected={selected}
                    onToggleSelect={onToggleSelect}
                    SelectionHeader={!hideHeader && (props => (
                        <>
                            <WithPermission permission="ROLE_RIGHT_ITEMS_PATCH_SLOT">
                                <Button
                                    variant="contained"
                                    className={classes.button}
                                    disabled={itemsState.actionsDisabled}
                                    onClick={() => this.toggleSlotDialog()}
                                >
                                    Verschieben
                                    </Button>
                                <Dialog
                                    open={slotDialogOpen}
                                    transitionDuration={0}
                                    keepMounted
                                    onClose={() => this.toggleSlotDialog()}
                                    aria-labelledby="alert-dialog-slide-title"
                                    aria-describedby="alert-dialog-slide-description"
                                >
                                    <DialogTitle id="alert-dialog-slide-title">
                                        Neuer Lagerplatz
                                    </DialogTitle>
                                    <DialogContent>
                                        <ItemSlotEditComponent />
                                    </DialogContent>
                                    <DialogActions>
                                        <Button onClick={() => this.toggleSlotDialog()} color="secondary">
                                            Abbrechen
                                        </Button>
                                        <Button color="primary" onClick={() => {
                                            itemsState.bulkSaveSlot(props.selected, () => props.resetSelection());
                                            this.toggleSlotDialog();
                                        }}>
                                            Speichern
                                        </Button>
                                    </DialogActions>
                                </Dialog>
                            </WithPermission>
                            <Button
                                variant="contained"
                                className={classes.button}
                                onClick={() => itemsState.bulkUpdateBrokenState(props.selected, true, () => props.resetSelection())}
                                disabled={itemsState.actionsDisabled}
                            >
                                Defekt
                            </Button>
                            <Button
                                variant="contained"
                                className={classes.button}
                                onClick={() => itemsState.bulkUpdateBrokenState(props.selected, false, () => props.resetSelection())}
                                disabled={itemsState.actionsDisabled}
                            >
                                Repariert
                            </Button>
                            <Button
                                disabled={itemsState.actionsDisabled}
                                variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Ausleihen"')}>
                                Ausleihen
                            </Button>
                            <Button
                                disabled={itemsState.actionsDisabled}
                                variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Zurückgeben"')}>
                                Zurückgeben
                            </Button>
                            <SimpleDialog
                                title="Löschen bestätigen"
                                okText="Ja"
                                cancelText="Nein"
                                text={`Sollen die ${props.selected.length} ausgewählten Artikel wirklich gelöscht werden?`}
                                onOK={() => { itemsState.bulkDeleteItems(props.selected); props.resetSelection(); }}
                            >
                                <Button
                                    variant="outlined"
                                    className={classes.button}
                                    onClick={() => { }}
                                    disabled={itemsState.actionsDisabled}
                                >
                                    Löschen
                                </Button>
                            </SimpleDialog>
                        </>
                    ))}

                    filter={(<>
                        <TextField
                            id="free-search"
                            value={itemsState.filterFreeText}
                            onChange={event => itemsState.changeFreeTextFilter(event.target.value)}
                            variant="outlined"
                            label="Freitextsuche"
                            margin="dense"
                        />
                        <IconButton className={classes.button} onClick={() => this.toggleExpandFilters()}>
                            {expandFilters ? (<ExpandLessIcon />) : (<ExpandMoreIcon />)}
                        </IconButton>
                        <IconButton className={classes.button} onClick={() => this.loadItems()}>
                            <SearchIcon />
                        </IconButton>
                        <br />
                        {(expandFilters || itemsState.filterProjectId) && (<>
                            TODO: Name, ID, Tag, Status (Verfügbar, Defekt, Ausgeliehen), Lager, Lagerplatz, Projekt, Menge (unter, gleich, über), Maße (unter, gleich, über), Gewerk, Verbrauchsgegenstand, Wetterbeständig
                        </>)}
                    </>)}
                    headers={[
                        {
                            key: 'name',
                            name: 'Bezeichnung',
                        },
                        {
                            key: 'identifier',
                            name: 'ID',
                        },
                        {
                            key: 'state',
                            name: 'Status',
                        },
                        {
                            key: 'storeName',
                            name: 'Lager',
                            // hidden: !!storeId,
                            align: 'right',
                        },
                        {
                            key: 'slotName',
                            name: 'Platz',
                            // hidden: !!slotId,
                            align: 'right',
                        },
                        {
                            key: 'quantityWithUnit',
                            name: 'Menge',
                            align: 'right',
                        },
                        {
                            key: 'technicalCrewName',
                            name: 'Gewerk',
                            align: 'right',
                        },
                        // {
                        //     key: 'popularity',
                        //     name: 'Beliebtheit',
                        //     align: 'right',
                        // },

                    ]}
                    rows={items}
                    redirect={!hideAdd && fullPathOfItemData}
                    showAddButton={!hideAdd && showAddButton} />
            </>
        );
    }
}
/* <TextField
    id="free-search"
    variant="outlined"
    label="Freitextsuche"
    margin="dense"
    InputProps={{
        startAdornment: <InputAdornment position="start"><SearchIcon /></InputAdornment>,
    }}
/>
<IconButton className={classes.button} variant="outlined" onClick={this.handleToggleShowFilters.bind(this)}>
    {showFilters ? (<ExpandLessIcon />) : (<ExpandMoreIcon />)}
</IconButton>
{showFilters ? (
    <>
        <br />
        <TextField
            className={classes.searchInput}
            id="name-search"
            variant="outlined"
            label="Bezeichnung"
            margin="dense"
        />
        <TextField
            className={classes.searchInput}
            id="description-search"
            variant="outlined"
            label="Beschreibung"
            margin="dense"
        />
        <br />
        <FormControl className={classes.formControl}>
            <InputLabel htmlFor="technical-crew">Gewerk</InputLabel>
            <Select
                value={searchTechnicalCrew}
                onChange={this.handleChangeSearchTechnicalCrew.bind(this)}
                inputProps={{
                    name: 'technical-crew',
                    id: 'technical-crew',
                }}
            >
                <MenuItem value=""></MenuItem>
                <MenuItem value={1}>Maler</MenuItem>
                <MenuItem value={2}>Elektriker</MenuItem>
                <MenuItem value={3}>Zimmerer</MenuItem>
            </Select>
        </FormControl>
        <FormControl className={classes.formControl}>
            <InputLabel htmlFor="tag">Tag</InputLabel>
            <Select
                value={searchTag}
                onChange={this.handleChangeSearchTag.bind(this)}
                inputProps={{
                    name: 'tag',
                    id: 'tag',
                }}
            >
                <MenuItem value=""></MenuItem>
                <MenuItem value={1}>Hammer</MenuItem>
                <MenuItem value={2}>Absolut</MenuItem>
                <MenuItem value={3}>Schlagen</MenuItem>
            </Select>
        </FormControl>
        <FormControl className={classes.formControl}>
            <InputLabel htmlFor="store">Lager</InputLabel>
            <Select
                value={searchStore}
                onChange={this.handleChangeSearchStore.bind(this)}
                inputProps={{
                    name: 'store',
                    id: 'store',
                }}
            >
                <MenuItem value=""></MenuItem>
                <MenuItem value={1}>Kehlheim</MenuItem>
                <MenuItem value={2}>Stuttgart</MenuItem>
                <MenuItem value={3}>Vöhringen</MenuItem>
            </Select>
        </FormControl>
        <FormControl className={classes.formControl}>
            <InputLabel htmlFor="slot">Platz</InputLabel>
            <Select
                value={searchSlot}
                onChange={this.handleChangeSearchSlot.bind(this)}
                inputProps={{
                    name: 'slot',
                    id: 'slot',
                }}
            >
                <MenuItem value=""></MenuItem>
                <MenuItem value={1}>A1</MenuItem>
                <MenuItem value={2}>B1</MenuItem>
                <MenuItem value={3}>B2</MenuItem>
            </Select>
        </FormControl>
        <br />
    </>
) : null}
<Button variant="contained" onClick={() => alert('Vergeblich nach der Search-Funktion gesucht ;(')}>
    Suchen
        </Button> 
        
<PagedTable
    selectionHeader={(
        <>
            <Button variant="outlined" className={classes.button} onClick={() => alert('TODO: implement "Zerstörung"')}>
                Löschen
                    </Button>
            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Verschieben"')}>
                Verschieben
                    </Button>
            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Zerstörung"')}>
                Defekt
                     </Button>
            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Zerstörung"')}>
                Repariert
                    </Button>
            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Ausleihen"')}>
                Ausleihen
                    </Button>
            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Zurückgeben"')}>
                Zurückgeben
                    </Button>
        </>
    )}
    headers={[
        {
            key: 'name',
            name: 'Bezeichnung',
        },
        {
            key: 'identifier',
            name: 'ID',
        },
        {
            key: 'state',
            name: 'Status',
        },
        {
            key: 'store',
            name: 'Lager',
            hidden: !!storeId,
            align: 'right',
        },
        {
            key: 'slot',
            name: 'Platz',
            hidden: !!slotId,
            align: 'right',
        },
        {
            key: 'quantity',
            name: 'Menge',
            align: 'right',
        },
        {
            key: 'popularity',
            name: 'Beliebtheit',
            align: 'right',
        },

    ]}
    rows={rows.map(row => {
        return {
            ...row,
            state: this.getState(row),
            popularity: row.rentals.length,
            quantity: row.quantity + ' ' + row.unit,
        }
    })}
    redirect={fullPathOfItem} />
</> */


const ItemListComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <ItemsContext.Consumer>
                    {itemsState => (
                        <StatefulItemListComponent {...props} sessionState={sessionState} itemsState={itemsState} />
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemListComponent;