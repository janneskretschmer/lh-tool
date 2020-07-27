import React from 'react';
import { SessionContext } from '../../providers/session-provider';
import { ItemsContext } from '../../providers/items-provider';
import { withStyles, CircularProgress, Select, MenuItem } from '@material-ui/core';
import IdNameSelect from '../util/id-name-select';

const styles = theme => ({
    select: {
        minWidth: '100px',
    }
});

const ItemSlotEditComponent = props => (
    <>
        <ItemsContext.Consumer>
            {itemsState => itemsState.stores ? (<>
                <IdNameSelect
                    className={props.classes.select}
                    label="Lager"
                    value={itemsState.selectedStoreId}
                    onChange={value => itemsState.changeSelectedStore(value)}
                    data={itemsState.stores}
                />:&nbsp;
                {itemsState.slots ? (<>
                    <IdNameSelect
                        className={props.classes.select}
                        label="Lagerplatz"
                        value={itemsState.selectedSlotId}
                        onChange={value => itemsState.changeSelectedSlot(value)}
                        data={itemsState.getSlotsBySelectedStore()}
                    />
                </>) : (<CircularProgress size={15} />)}
            </>) : (<CircularProgress size={15} />)}
        </ItemsContext.Consumer>
    </>
);
export default withStyles(styles)(ItemSlotEditComponent);