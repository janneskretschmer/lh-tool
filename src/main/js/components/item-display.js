import React from 'react';
import { withStyles } from '@material-ui/core/styles';

const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },

});

const ItemDisplayComponent = props => {
    return (
        <div>
            Show
        </div>
    )
}


export default withStyles(styles)(ItemDisplayComponent);
