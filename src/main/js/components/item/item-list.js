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
import { fullPathOfItem } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import PagedTable from '../table';


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
@withContext('sessionState', SessionContext)
export default class ItemListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            rows: [
                {
                    id: 1235, name: 'Hammer', store: 1, slot: 1, quantity: 3, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 6, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 7, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 8, userName: "Bauhelfer 4", timestamp: null, givenBack: true },
                        { id: 9, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 10, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 11, userName: "Bauhelfer 5", timestamp: null, givenBack: false },
                    ]
                },
                {
                    id: 2623, name: 'Hammer', store: 2, slot: 2, quantity: 3, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 6, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 302934, name: 'Meissel', store: 1, slot: 3, quantity: 4, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 6, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 7, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 8, userName: "Bauhelfer 4", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 423562, name: 'Presslufthammer', slot: 4, store: 1, quantity: 5, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 6, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 7, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 8, userName: "Bauhelfer 4", timestamp: null, givenBack: true },
                        { id: 9, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 10, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 11, userName: "Bauhelfer 5", timestamp: null, givenBack: false },
                    ]
                },
                {
                    id: 594346, name: 'Schlauch dick', slot: 5, store: 2, quantity: 2, unit: 'm', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 623843, name: 'Schlauch duenn', slot: 1, store: 2, quantity: 2, unit: 'm', broken: true, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 712582, name: 'Schlauch dick', slot: 2, store: 1, quantity: 4, unit: 'm', broken: true, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 85124, name: 'Silikon', store: 2, slot: 3, quantity: 2, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 11, userName: "Bauhelfer 5", timestamp: null, givenBack: false },
                    ]
                },
                {
                    id: 91252, name: 'Zange', store: 2, slot: 4, quantity: 3, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 102512, name: 'Zange', store: 3, slot: 5, quantity: 2, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 1132891, name: 'Zollstock', store: 2, slot: 1, quantity: 2, unit: 'Stueck', broken: true, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 6, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 9, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 10, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                    ]
                },
                {
                    id: 1228129, name: 'Zollstock', store: 1, slot: 3, quantity: 7, unit: 'Stueck', broken: false, rentals: [
                        { id: 1, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 2, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 3, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 4, userName: "Bauhelfer 34", timestamp: null, givenBack: true },
                        { id: 5, userName: "Bauhelfer 2", timestamp: null, givenBack: true },
                        { id: 6, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 7, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 8, userName: "Bauhelfer 4", timestamp: null, givenBack: true },
                        { id: 9, userName: "Bauhelfer 5", timestamp: null, givenBack: true },
                        { id: 10, userName: "Bauhelfer 1", timestamp: null, givenBack: true },
                        { id: 11, userName: "Bauhelfer 5", timestamp: null, givenBack: false },
                    ]
                },
            ].filter(v => !props.storeId || v.store === props.storeId && !props.slotId || v.slot == props.slotId),//.sort((a, b) => (a.name < b.name ? -1 : 1)),

            showFilters: false,
            searchTechnicalCrew: '',
            searchTag: '',
            searchStore: '',
            searchSlot: '',
        };
    }

    handleToggleShowFilters = () => {
        this.setState({ showFilters: !this.state.showFilters })
    }

    handleChangeSearchTechnicalCrew = event => {
        this.setState({ searchTechnicalCrew: event.target.value })
    }

    handleChangeSearchTag = event => {
        this.setState({ searchTag: event.target.value })
    }

    handleChangeSearchStore = event => {
        this.setState({ searchStore: event.target.value })
    }

    handleChangeSearchSlot = event => {
        this.setState({ searchSlot: event.target.value })
    }

    getState(item) {
        if (item.broken) {
            return "Defekt"
        }
        let lastRental = item.rentals[item.rentals.length - 1];
        if (!lastRental.givenBack) {
            return "Ausgeliehen an " + lastRental.userName;
        }
        return "Verfügbar"
    }

    render() {
        const { classes, storeId, slotId } = this.props
        const { rows, rowsPerPage, page, showFilters, searchTechnicalCrew, searchTag, searchStore, searchSlot, selected } = this.state;
        const emptyRows = rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
        const singlePage = emptyRows < rows.length;
        if (this.state.redirect) {
            return (<Redirect to={fullPathOfItem(this.state.redirect)} />)
        }
        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <>
                        <TextField
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
                    </>
                )}
            </SessionContext.Consumer>
        );
    }
}
