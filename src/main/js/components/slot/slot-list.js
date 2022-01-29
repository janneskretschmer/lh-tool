import { CircularProgress } from '@mui/material';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import React from 'react';
import { fullPathOfSlot } from '../../paths';
import { RIGHT_SLOTS_PUT } from '../../permissions';
import { SessionContext } from '../../providers/session-provider';
import { SlotsContext } from '../../providers/slots-provider';
import PagedTable from '../table';
import IdNameSelect from '../util/id-name-select';

class StatefulSlotListComponent extends React.Component {

    constructor(props) {
        super(props);
    }

    handleToggleShowFilters = () => {
        this.setState({ showFilters: !this.state.showFilters });
    }

    componentDidMount() {
        this.props.slotsState.loadSlots();
    }

    render() {
        const { classes, store, sessionState, slotsState } = this.props;
        const slots = slotsState && slotsState.slots && [...slotsState.slots.values()];
        const showAddButton = sessionState.hasPermission(RIGHT_SLOTS_PUT);
        return <>
            {slots ? (
                <PagedTable
                    SelectionHeader={props => (
                        <>
                            <Button variant="outlined" onClick={() => { slotsState.bulkDeleteSlots(props.selected); props.resetSelection(); }}>
                                Löschen
                            </Button>
                        </>
                    )}
                    freeTextValue={slotsState.filterFreeText}
                    onChangeFreeText={text => slotsState.changeFilterFreeText(text)}
                    onFilter={() => slotsState.loadSlots()}
                    keepFiltersExpanded={slotsState.filterName || slotsState.filterDescription || slotsState.filterStoreId}
                    additionalFilters={(<>
                        <TextField
                            sx={{ ml: 1 }}
                            id="name-search"
                            variant="outlined"
                            label="Name"
                            size="small"
                            value={slotsState.filterName}
                            onChange={event => slotsState.changeFilterName(event.target.value)}
                        />
                        <TextField
                            sx={{ ml: 1 }}
                            id="description-search"
                            variant="outlined"
                            label="Beschreibung"
                            size="small"
                            value={slotsState.filterDescription}
                            onChange={event => slotsState.changeFilterDescription(event.target.value)}
                        />
                        <IdNameSelect
                            sx={{ ml: 1, width: '96px' }}
                            label="Lager"
                            value={slotsState.filterStoreId}
                            onChange={value => slotsState.changeFilterStoreId(value)}
                            data={slotsState.stores}
                            nullable
                        />
                        {/* TODO: Größensuche, Drinnen/Draußen */}
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
        </>;
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
