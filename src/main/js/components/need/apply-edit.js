import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin } from '../../util';
import TextField from '@material-ui/core/TextField';
import { changeApplicationStateForNeed } from '../../actions/need';
import CircularProgress from '@material-ui/core/CircularProgress';
import Button from '@material-ui/core/Button';
import { green, yellow, red } from '@material-ui/core/colors';
import SimpleDialog from '../simple-dialog';
import moment from 'moment';

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
    rejected: {
        minWidth: '105px',
        width: 'calc(50% - 6px)',
        margin: '3px',
        backgroundColor: red[600],
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
                this.setState(prevState => ({
                    need: {
                        ...prevState.need,
                        ownState: newNeedUser.state,
                        appliedCount: prevState.need.appliedCount + (newNeedUser.state === 'NONE' ? -1 : 1),
                    },
                    updating: false,
                }));
            });
    }

    getClassName(need) {
        switch (need.ownState) {
            case 'APPLIED': return this.props.classes.applied;
            case 'APPROVED': return this.props.classes.approved;
            case 'REJECTED': return this.props.classes.rejected;
            default: return this.props.classes.none;
        }
    }

    getDialogText(need) {
        return need.ownState === 'NONE' ? 'Möchtest du dich für die diese Schicht bewerben?' : 'Möchtest du die Bewerbung für diese Schicht wirklich zurückziehen?';
    }

    getDialogTitle(need, label) {
        const date = moment(need.date, 'x').format('DD.MM.YYYY');
        return label + ' am ' + date;
    }

    render() {
        const { classes, label, sessionState } = this.props;
        const { need, updating } = this.state;
        return updating ? (
            <span className={classes.apply}>
                <CircularProgress size={15} />
            </span>
        ) : need.ownState !== 'REJECTED' ? (
            <>
                <SimpleDialog
                    onOK={this.toggleApplicationStatus.bind(this)}
                    title={this.getDialogTitle(need, label)}
                    text={this.getDialogText(need)}
                    okText="Ja"
                    cancelText="Nein"
                >
                    <Button
                        variant={need.ownState === 'APPLIED' || need.ownState === 'APPROVED' || need.ownState === 'REJECTED' ? 'contained' : 'outlined'}
                        disabled={!this.props.sessionState.hasPermission('ROLE_RIGHT_NEEDS_APPLY') || !need.id || need.quantity === 0 || (need.ownState !== 'APPROVED' && need.approvedCount >= need.quantity)}
                        className={this.getClassName(need)}
                        color="inherit">
                        {label} {need.appliedCount + need.approvedCount}/{need.quantity}
                    </Button>
                </SimpleDialog>
            </>
        ) : (
                    <SimpleDialog
                        title={this.getDialogTitle(need, label)}
                        text="Für diese Schicht stehen bereits genügend Helfer zur Verfügung. Bitte bewerbe dich für eine andere Aufgabe oder an einem anderen Datum."
                        cancelText="OK"
                    >
                        <Button
                            variant={'contained'}
                            className={this.getClassName(need)}
                            color="inherit">
                            {label}
                        </Button>
                    </SimpleDialog>
                );
    }
}

export default requiresLogin(NeedApplyEditComponent);
