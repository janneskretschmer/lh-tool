import React from 'react';
import { SessionContext } from '../../providers/session-provider';
import { ItemsContext } from '../../providers/items-provider';
import { withStyles, CircularProgress, Select, MenuItem } from '@material-ui/core';

const styles = theme => ({
});

const ItemSlotEditComponent = props => (
    <>
        <ItemsContext.Consumer>
            {itemsState => itemsState.stores && itemsState.selectedStoreId ? (<>
                <Select
                    value={itemsState.selectedStoreId}
                    onChange={event => itemsState.changeSelectedStore(event.target.value)}>
                    {[...itemsState.stores.values()].map(store => (
                        <MenuItem key={store.id} value={store.id}>{store.name}</MenuItem>
                    ))}
                </Select>:&nbsp;
                {itemsState.slots ? (<>
                    <Select
                        value={itemsState.selectedSlotId}
                        onChange={event => itemsState.changeSelectedSlot(event.target.value)}>
                        {itemsState.getSlotsBySelectedStore().map(slot => (
                            <MenuItem key={slot.id} value={slot.id}>{slot.name}</MenuItem>
                        ))}
                    </Select>
                </>) : (<CircularProgress size={15} />)}
            </>) : (<CircularProgress size={15} />)}
        </ItemsContext.Consumer>
    </>
);
export default withStyles(styles)(ItemSlotEditComponent);