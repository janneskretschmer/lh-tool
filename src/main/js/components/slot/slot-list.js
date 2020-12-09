import { CircularProgress } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import SearchIcon from '@material-ui/icons/Search';
import React from 'react';
import { fullPathOfSlot } from '../../paths';
import { RIGHT_SLOTS_PUT } from '../../permissions';
import { SessionContext } from '../../providers/session-provider';
import { SlotsContext } from '../../providers/slots-provider';
import PagedTable from '../table';
import IdNameSelect from '../util/id-name-select';


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
class StatefulSlotListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            expandFilters: false,
        };
    }

    handleToggleShowFilters = () => {
        this.setState({ showFilters: !this.state.showFilters });
    }

    componentDidMount() {
        this.props.slotsState.loadSlots();
    }


    toggleExpandFilters() {
        this.setState(prevState => ({
            expandFilters: !prevState.expandFilters,
        }));
    }

    render() {
        const { classes, store, sessionState, slotsState } = this.props;
        const { expandFilters } = this.state;
        const expandedFilterSet = !!(slotsState.filterName || slotsState.filterDescription || slotsState.filterStoreId);
        const slots = slotsState && slotsState.slots && [...slotsState.slots.values()];
        const showAddButton = sessionState.hasPermission(RIGHT_SLOTS_PUT);
        return (
            <>
                {slots ? (
                    <PagedTable
                        SelectionHeader={props => (
                            <>
                                <Button variant="outlined" className={classes.button} onClick={() => { slotsState.bulkDeleteSlots(props.selected); props.resetSelection(); }}>
                                    Löschen
                                </Button>
                            </>
                        )}
                        filter={(<>
                            <TextField
                                id="free-search"
                                value={slotsState.filterFreeText}
                                onChange={event => slotsState.changeFilterFreeText(event.target.value)}
                                variant="outlined"
                                label="Freitextsuche"
                                margin="dense"
                            />
                            <IconButton
                                className={classes.button}
                                onClick={() => this.toggleExpandFilters()}
                                disabled={expandedFilterSet}
                            >
                                {expandFilters || expandedFilterSet ? (<ExpandLessIcon />) : (<ExpandMoreIcon />)}
                            </IconButton>
                            <IconButton className={classes.button} onClick={() => slotsState.loadSlots()}>
                                <SearchIcon />
                            </IconButton>
                            <br />
                            {(expandFilters || expandedFilterSet) && (<>
                                <TextField
                                    className={classes.searchInput}
                                    id="name-search"
                                    variant="outlined"
                                    label="Name"
                                    margin="dense"
                                    value={slotsState.filterName}
                                    onChange={event => slotsState.changeFilterName(event.target.value)}
                                />
                                <TextField
                                    className={classes.searchInput}
                                    id="description-search"
                                    variant="outlined"
                                    label="Beschreibung"
                                    margin="dense"
                                    value={slotsState.filterDescription}
                                    onChange={event => slotsState.changeFilterDescription(event.target.value)}
                                />
                                <IdNameSelect
                                    label="Lager"
                                    value={slotsState.filterStoreId}
                                    onChange={value => slotsState.changeFilterStoreId(value)}
                                    data={slotsState.stores}
                                    nullable
                                />
                                {/* TODO: Größensuche, Drinnen/Draußen */}
                            </>)}
                        </>)}
                        headers={[
                            {
                                key: 'name',
                                name: 'Name',
                            },
                            {
                                key: 'storeId',
                                name: 'Lager',
                                converter: storeId => slotsState.stores && slotsState.stores.has(storeId) && slotsState.stores.get(storeId).name,
                            },
                            {
                                key: 'outside',
                                name: 'Draußen',
                                converter: outside => outside ? 'Ja' : 'Nein'
                            },

                        ]}
                        rows={slots}
                        redirect={fullPathOfSlot}
                        showAddButton={showAddButton} />
                ) : (<CircularProgress />)
                }
            </>
        );
    }
}


const SlotListComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <SlotsContext.Consumer>
                    {slotsState => (<StatefulSlotListComponent {...props} sessionState={sessionState} slotsState={slotsState} />)}
                </SlotsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default SlotListComponent;
