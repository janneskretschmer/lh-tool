import CircularProgress from '@material-ui/core/CircularProgress';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { requiresLogin } from '../../util';

const styles = theme => ({
    quantityInput: {
        minWidth: '115px',
        width: '100%',
    },
    quantityUpdating: {
        minWidth: '85px',
        width: 'calc(100% - 36px)',
    },
});

@withStyles(styles)
@withSnackbar
class StatefulNeedQuantityEditComponent extends React.Component {

    constructor(props) {
        super(props);
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren des Bedarfs', {
            variant: 'error',
        });
    }

    handleQuantityChange(event) {
        const quantity = parseInt(event.target.value, 10);
        const { projectId, helperTypeId } = this.props.projectHelperType;
        const need = {
            ...this.props.projectHelperType.need,
            quantity,
        };
        this.props.needsState.updateNeedQuantity(projectId, helperTypeId, need, err => this.handleFailure());
    }

    render() {
        const { classes, label } = this.props;
        const need = this.props.projectHelperType.need;
        const update = this.props.needsState.getQuantityUpdate(need);
        return (
            <>
                <TextField
                    id="need_quantity"
                    label={label}
                    defaultValue={need.quantity > 0 ? need.quantity : null}
                    type="number"
                    margin="dense"
                    variant="outlined"
                    className={update ? classes.quantityUpdating : classes.quantityInput}
                    onChange={this.handleQuantityChange.bind(this)}
                    inputProps={{
                        'min': '0',
                        'step': '1',
                    }}
                />
                {update ? (
                    <CircularProgress size={30} />
                ) : null}
            </>
        );
    }
}

const NeedQuantityEditComponent = props => (
    <>
        <NeedsContext.Consumer>
            {needsState =>
                (<StatefulNeedQuantityEditComponent {...props} needsState={needsState} />)
            }
        </NeedsContext.Consumer>
    </>
);
export default requiresLogin(NeedQuantityEditComponent);
