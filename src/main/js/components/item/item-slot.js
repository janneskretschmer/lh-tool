import { CircularProgress } from '@mui/material';
import { Box } from '@mui/system';
import React from 'react';
import { ItemsContext } from '../../providers/items-provider';
import IdNameSelect from '../util/id-name-select';

const ItemSlotEditComponent = props => (
    <>
        <ItemsContext.Consumer>
            {itemsState => itemsState.stores ? (<>
                <Box sx={{
                    display: 'flex',
                    alignItems: 'baseline',
                    mt: 1,
                }}>
                    <IdNameSelect
                        label="Lager"
                        value={itemsState.selectedStoreId}
                        onChange={value => itemsState.changeSelectedStore(value)}
                        data={itemsState.stores}
                    />:&nbsp;
                    {itemsState.slots ? (<>
                        <IdNameSelect
                            label="Lagerplatz"
                            value={itemsState.selectedSlotId}
                            onChange={value => itemsState.changeSelectedSlot(value)}
                            data={itemsState.getSlotsBySelectedStore()}
                        />
                    </>) : (<CircularProgress size={15} />)}
                </Box>
            </>) : (<CircularProgress size={15} />)}
        </ItemsContext.Consumer>
    </>
);
export default ItemSlotEditComponent;