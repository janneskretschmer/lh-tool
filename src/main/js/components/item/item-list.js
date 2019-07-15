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
    selectionHeader: {
        width: '120px',
    },
    selectionText: {
        marginRight: '18px',
    },
});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class ItemListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            rows: [
                { id: 1, name: 'Hammer', store: 1, slot: 1, quantity: 3, unit: 'Stueck' },
                { id: 2, name: 'Hammer', store: 2, slot: 2, quantity: 3, unit: 'Stueck' },
                { id: 3, name: 'Meissel', store: 1, slot: 3, quantity: 4, unit: 'Stueck' },
                { id: 4, name: 'Presslufthammer', slot: 4, store: 1, quantity: 5, unit: 'Stueck' },
                { id: 5, name: 'Schlauch dick', slot: 5, store: 2, quantity: 2, unit: 'm' },
                { id: 6, name: 'Schlauch duenn', slot: 1, store: 2, quantity: 2, unit: 'm' },
                { id: 7, name: 'Schlauch dick', slot: 2, store: 1, quantity: 4, unit: 'm' },
                { id: 8, name: 'Silikon', store: 2, slot: 3, quantity: 2, unit: 'Stueck' },
                { id: 9, name: 'Zange', store: 2, slot: 4, quantity: 3, unit: 'Stueck' },
                { id: 10, name: 'Zange', store: 3, slot: 5, quantity: 2, unit: 'Stueck' },
                { id: 11, name: 'Zollstock', store: 2, slot: 1, quantity: 2, unit: 'Stueck' },
                { id: 12, name: 'Zollstock', store: 1, slot: 3, quantity: 7, unit: 'Stueck' },
            ],//.sort((a, b) => (a.name < b.name ? -1 : 1)),
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

    redirect(id) {
        this.setState({ redirect: id })
    }

    render() {
        const { classes } = this.props
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
                            <TableHead>
                                <TableRow>
                                    <TableCell>Bezeichnung</TableCell>
                                    <TableCell align="right">Lager</TableCell>
                                    <TableCell align="right">Platz</TableCell>
                                    <TableCell align="right">Menge</TableCell>
                                    <TableCell align="right" padding="checkbox" className={classes.selectionHeader}>
                                        {selected.length > 0 ? (
                                            <>
                                                <span className={classes.selectionText}>
                                                    Auswahl ({selected.length})
                                                </span>
                                                <IconButton onClick={() => alert('TODO: Ausgewählte Artikel verschieben')}>
                                                    <ForwardIcon />
                                                </IconButton>
                                                <IconButton onClick={() => alert('TODO: Ausgewählte Artikel löschen')}>
                                                    <DeleteIcon />
                                                </IconButton>
                                            </>
                                        ) : null}
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                                    <TableRow
                                        key={row.id}
                                    >
                                        <TableCell component="th" scope="row" onClick={event => this.redirect(row.id)}>
                                            {row.name}
                                        </TableCell>
                                        <TableCell align="right" onClick={event => this.redirect(row.id)}>
                                            {row.store}
                                        </TableCell>
                                        <TableCell align="right" onClick={event => this.redirect(row.id)}>
                                            {row.slot}
                                        </TableCell>
                                        <TableCell align="right" onClick={event => this.redirect(row.id)}>
                                            {row.quantity} {row.unit}
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
