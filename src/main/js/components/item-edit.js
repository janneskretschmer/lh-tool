import React from 'react';
import { withStyles } from '@material-ui/core/styles';

const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },

});

const ItemEditComponent = props => {
    return (
        <div>
            edit

            <span>Hammer</span>
            Eindeutiger Bezeichner
            (Barcode)
            Menge+Einheit
            Maße
            Verbrauchsgegenstand
            Beschreibung
            Tags
            Gewerke
            Historie
        </div>
    )
}


export default withStyles(styles)(ItemEditComponent);
