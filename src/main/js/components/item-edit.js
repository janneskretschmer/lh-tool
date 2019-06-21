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
        </div>
    )
}


export default withStyles(styles)(ItemEditComponent);
