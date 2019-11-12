import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin } from '../../util';
import TextField from '@material-ui/core/TextField';
import { changeApplicationStateForNeed } from '../../actions/need';
import CircularProgress from '@material-ui/core/CircularProgress';
import Button from '@material-ui/core/Button';
import { green, yellow } from '@material-ui/core/colors';

const styles = theme => ({
    none: {
        minWidth: '105px',
        width: 'calc(50% - 6px)',
        margin: '3px',
    },
    applied: {
        minWidth: '105px',
        width: 'calc(50% - 6px)',
        margin: '3px',
        backgroundColor: yellow[600],
    },
    approved: {
        minWidth: '105px',
        width: 'calc(50% - 6px)',
        margin: '3px',
        backgroundColor: green[600],
    },
});

@withStyles(styles)
@withSnackbar
class NeedApplyEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            need: props.need,
            updating: false,
        };
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren der Bewerbung', {
            variant: 'error',
        });
    }

    toggleApplicationStatus() {
        this.setState({
            updating: true,
        })
        changeApplicationStateForNeed({
            accessToken: this.props.sessionState.accessToken,
            userId: this.props.sessionState.currentUser.id,
            needId: this.state.need.id,
            state: this.state.need.ownState === 'NONE' ? 'APPLIED' : 'NONE',
            handleFailure: null,
        })
            .then(newNeedUser => {
                this.setState({
                    need: {
                        ...this.state.need,
                        ownState: newNeedUser.state,
                    },
                    updating: false,
                })
            });
    }

    getClassName(need){
        switch(need.ownState){
            case 'APPLIED' : return this.props.classes.applied
            case 'APPROVED': return this.props.classes.approved
            default: return this.props.classes.none
        }
    }

    render() {
        const { classes, label, sessionState } = this.props;
        const { need, updating } = this.state;

        return updating ? (
            <span className={classes.apply}>
                <CircularProgress size={15} />
            </span>
        ) : (
                <>
                    <Button
                        variant={need.ownState === 'APPLIED' || need.ownState === 'APPROVED' ? 'contained' : 'outlined'}
                        disabled={!this.props.sessionState.hasPermission('ROLE_RIGHT_NEEDS_APPLY') || !need.id || need.quantity === 0}
                        className={this.getClassName(need)}
                        onClick={this.toggleApplicationStatus.bind(this)}
                        color="inherit">
                        {label}
                    </Button>
                </>
            )
    }
}

export default requiresLogin(NeedApplyEditComponent);
