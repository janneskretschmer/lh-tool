import { Checkbox, FormControlLabel, TextField } from '@mui/material';
import React from 'react';
import { ItemsContext } from '../../providers/items-provider';

const ItemIdentifierEditComponent = props => (
    <>
        <ItemsContext.Consumer>
            {itemsState => (<>
                <TextField
                    label="Neuer eindeutiger Bezeichner"
                    variant="outlined"
                    sx={{ mt: 1 }}
                    value={itemsState.copyIdentifier}
                    onChange={event => itemsState.changeCopyIdentifier(event.target.value)}
                /><br />
                <FormControlLabel
                    label="Bezeichner ist Barcode"
                    control={
                        <Checkbox
                            checked={itemsState.copyHasBarcode}
                            onChange={event => itemsState.changeCopyHasBarcode(event.target.checked)}
                            disableRipple
                        />
                    }
                />
            </>)}
        </ItemsContext.Consumer>
    </>
);
export default ItemIdentifierEditComponent;