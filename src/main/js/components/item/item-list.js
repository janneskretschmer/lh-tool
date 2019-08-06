import React from 'react';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableFooter from '@material-ui/core/TableFooter';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import SearchIcon from '@material-ui/icons/Search';
import DeleteIcon from '@material-ui/icons/Delete';
import ForwardIcon from '@material-ui/icons/Forward';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import TextField from '@material-ui/core/TextField';
import InputAdornment from '@material-ui/core/InputAdornment';
import { withStyles } from '@material-ui/core/styles';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import { fullPathOfItem } from '../../paths';
import { Redirect } from 'react-router'
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import Checkbox from '@material-ui/core/Checkbox';


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
            ].filter(v => !props.store || v.store === props.store),//.sort((a, b) => (a.name < b.name ? -1 : 1)),
            page: 0,
            rowsPerPage: 10,
            selected: [],

            showFilters: false,
            searchTechnicalCrew: '',
            searchTag: '',
            searchStore: '',
            searchSlot: '',
            redirect: undefined,
        };
    }

    handleSelection = (index) => {
        this.setState({
            selected: this.state.selected.includes(index) ? this.state.selected.filter(item => item !== index) : this.state.selected.concat(index),
        })
    }

    handleChangePage = (event, page) => {
        this.setState({ page });
    };

    handleChangeRowsPerPage = event => {
        this.setState({ page: 0, rowsPerPage: event.target.value });
    };

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

    handleRowClick(id) {
        if(this.state.selected.length > 0) {
            this.handleSelection(id)
        } else {
            this.setState({ redirect: id })
        }
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
        const { classes, store } = this.props
        const { rows, rowsPerPage, page, showFilters, searchTechnicalCrew, searchTag, searchStore, searchSlot, selected } = this.state;
        const emptyRows = rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
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
                        <Table>
                            {selected.length > 0 ? (
                                <>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell align="right" colSpan="8">
                                                <span className={classes.selectionText}>
                                                    Auswahl ({selected.length})
                                                </span>
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
                                            </TableCell>
                                        </TableRow>
                                    </TableHead>
                                </>
                            ) : (
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Bezeichnung</TableCell>
                                            <TableCell>ID</TableCell>
                                            <TableCell>Status</TableCell>
                                            {!store ? (
                                                <TableCell align="right">Lager</TableCell>
                                            ) : null}
                                            <TableCell align="right">Platz</TableCell>
                                            <TableCell align="right">Menge</TableCell>
                                            <TableCell align="right">Beliebtheit</TableCell>
                                            <TableCell align="right" padding="checkbox">
                                            </TableCell>
                                        </TableRow>
                                    </TableHead>
                                )}
                            <TableBody>
                                {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                                    <TableRow
                                        key={row.id}
                                        className={classes.clickable}
                                    >
                                        <TableCell component="th" scope="row" onClick={event => this.handleRowClick(row.id)}>
                                            {row.name}
                                        </TableCell>
                                        <TableCell component="th" scope="row" onClick={event => this.handleRowClick(row.id)}>
                                            {row.id}
                                        </TableCell>
                                        <TableCell component="th" scope="row" onClick={event => this.handleRowClick(row.id)}>
                                            {this.getState(row)}
                                        </TableCell>
                                        {!store ? (
                                            <TableCell align="right" onClick={event => this.handleRowClick(row.id)}>
                                                {row.store}
                                            </TableCell>
                                        ) : null}
                                        <TableCell align="right" onClick={event => this.handleRowClick(row.id)}>
                                            {row.slot}
                                        </TableCell>
                                        <TableCell align="right" onClick={event => this.handleRowClick(row.id)}>
                                            {row.quantity} {row.unit}
                                        </TableCell>
                                        <TableCell align="right" onClick={event => this.handleRowClick(row.id)}>
                                            {row.rentals.length}
                                        </TableCell>
                                        <TableCell align="right" padding="checkbox">
                                            <Checkbox
                                                checked={selected.includes(row.id)}
                                                onChange={() => this.handleSelection(row.id)}
                                            />
                                        </TableCell>
                                    </TableRow>
                                ))}
                                {emptyRows > 0 && (
                                    <TableRow style={{ height: 48 * emptyRows }}>
                                        <TableCell colSpan={6} />
                                    </TableRow>
                                )}
                            </TableBody>
                            <TableFooter>
                                <TableRow>
                                    <TableCell>
                                        <Button variant="contained" onClick={() => alert('TODO: Umleitung auf leere Bearbeiten-Seite')} className={classes.new}>
                                            Hinzufügen
                                        </Button>
                                    </TableCell>
                                    <TablePagination
                                        rowsPerPageOptions={[5, 10, 25]}
                                        colSpan={3}
                                        count={rows.length}
                                        rowsPerPage={rowsPerPage}
                                        page={page}
                                        SelectProps={{
                                            native: true,
                                        }}
                                        onChangePage={this.handleChangePage}
                                        onChangeRowsPerPage={this.handleChangeRowsPerPage}
                                    />
                                </TableRow>
                            </TableFooter>
                        </Table>
                    </>
                )}
            </SessionContext.Consumer>
        );
    }
}
