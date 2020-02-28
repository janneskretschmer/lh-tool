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
import { fullPathOfSlot } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import PagedTable from '../table';
import { fetchSlotsByStore } from '../../actions/slot';
import { CircularProgress } from '@material-ui/core';


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
export default class SlotListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            showFilters: false,
        };
    }

    handleToggleShowFilters = () => {
        this.setState({ showFilters: !this.state.showFilters });
    }

    componentDidMount() {
        fetchSlotsByStore({ accessToken: this.props.sessionState.accessToken, storeId: this.props.storeId }).then(slots => this.setState({ slots }));
    }

    render() {
        const { classes, store } = this.props;
        const { slots, showFilters } = this.state;
        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <>
                        <TextField
                            id="free-store-search"
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
                        {showFilters && (
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
                                TODO: Größensuche
                                <br />
                            </>
                        )}
                        <Button variant="contained" onClick={() => alert('Vergeblich nach der Search-Funktion gesucht ;(')}>
                            Suchen
                        </Button>


                        {slots ? (
                            <PagedTable
                                selectionHeader={(
                                    <>
                                        <Button variant="outlined" className={classes.button} onClick={() => alert('TODO: implement "Zerstörung"')}>
                                            Löschen
                                    </Button>
                                    </>
                                )}
                                headers={[
                                    {
                                        key: 'id',
                                        name: 'ID',
                                    },
                                    {
                                        key: 'name',
                                        name: 'Bezeichnung',
                                    },
                                    {
                                        key: 'size',
                                        name: 'Größe',
                                    },
                                    {
                                        key: 'outside',
                                        name: 'Draußen',
                                    },
                                    {
                                        key: 'itemCount',
                                        name: '#Artikel',
                                        align: 'right',
                                    },

                                ]}
                                rows={slots.map(slot => {
                                    return {
                                        ...slot,
                                        size: slot.width && slot.height && slot.depth ? slot.width + ' x ' + slot.height + ' x ' + slot.depth : '',
                                        outside: slot.outside ? 'Ja' : 'Nein',
                                        itemCount: 'TODO',
                                    }
                                })}
                                redirect={fullPathOfSlot} />
                        ) : (<CircularProgress />)
                        }
                    </>
                )}
            </SessionContext.Consumer>
        );
    }
}
