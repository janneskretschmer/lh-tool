import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin } from '../../util';
import TextField from '@material-ui/core/TextField';
import { createOrUpdateNeed} from '../../actions/need';
import CircularProgress from '@material-ui/core/CircularProgress';

const styles = theme => ({
    quantityInput: {
        minWidth: '105px',
        width: 'calc(50% - 6px)',
        margin: '3px',
    },
    quantityUpdating: {
        minWidth: '75px',
        width: 'calc(50% - 36px)',
        margin: '3px',
    }
});

@withStyles(styles)
@withSnackbar
class NeedQuantityEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            need: props.need,
            updating: false,
        };
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren des Bedarfs', {
            variant: 'error',
        });
    }

    handleQuantityChange(event) {
        let quantity = parseInt(event.target.value, 10)
        this.latestQuantity = quantity ? quantity : 0
        this.updateValue()
    }

    //keeps calling the api, until the quantity is up to date. is "threadsafe"
    updateValue() {
        if(!this.requestInProgress) {
            this.requestInProgress = true;
            this.setState({
                updating: true,
            }, () =>
            createOrUpdateNeed({
                need: { ...this.state.need, quantity: this.latestQuantity}, sessionState: this.props.sessionState, handleFailure: this.handleFailure.bind(this)
            }).then(need => this.setState({
                ...this.state,
                need,
                updating: false,
            }, function(){
                this.requestInProgress = false;
                if(this.state.need.quantity !== this.latestQuantity) {
                    this.updateValue();
                }
            }.bind(this))
        ).catch(() => {this.requestInProgress = false;})
    );
}
}

    render() {
        const { classes, label, sessionState } = this.props;
        const { need, updating } = this.state;

        return (
            <>
                <TextField
                      id="need_quantity"
                      label={label}
                      defaultValue={need.quantity > 0 ? need.quantity : undefined}
                      type="number"
                      margin="dense"
                      variant="outlined"
                      className={updating ? classes.quantityUpdating : classes.quantityInput}
                      onChange={this.handleQuantityChange.bind(this)}
                      inputProps={{
                        'min': '0',
                        'step': '1',
                      }}
                    />
                {updating ? (
                    <CircularProgress size={30}/>
                ) : null}
            </>
        )
    }
}

export default requiresLogin(NeedQuantityEditComponent);
