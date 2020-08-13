import React from 'react';
import { SessionContext } from '../../providers/session-provider';
import { ItemsContext } from '../../providers/items-provider';
import { withStyles, CircularProgress, Select, MenuItem, TextField, FormControlLabel, Checkbox } from '@material-ui/core';

const styles = theme => ({
    textField: {
        marginTop: theme.spacing.unit,
    }
});

const ItemIdentifierEditComponent = props => (
    <>
        <ItemsContext.Consumer>
            {itemsState => (<>
                <TextField
                    label="Neuer eindeutiger Bezeichner"
                    variant="outlined"
                    className={props.classes.textField}
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
export default withStyles(styles)(ItemIdentifierEditComponent);