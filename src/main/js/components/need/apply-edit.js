import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin } from '../../util';
import TextField from '@material-ui/core/TextField';
import { changeApplicationStateForNeed } from '../../actions/need';
import CircularProgress from '@material-ui/core/CircularProgress';
import Button from '@material-ui/core/Button';
import { green, yellow, red, grey } from '@material-ui/core/colors';
import SimpleDialog from '../simple-dialog';
import moment from 'moment';
import { NeedsContext } from '../../providers/needs-provider';

const styles = theme => ({
    none: {
        minWidth: '105px',
        width: '100%',
        marginBottom: '3px',
    },
    buttonText: {
        color: grey[800],
    },
    applied: {
        minWidth: '105px',
        width: '100%',
        backgroundColor: yellow[600],
        marginBottom: '3px',
    },
    approved: {
        minWidth: '105px',
        width: '100%',
        backgroundColor: green[600],
        marginBottom: '3px',
    },
    rejected: {
        minWidth: '105px',
        width: '100%',
        backgroundColor: red[600],
        marginBottom: '3px',
    },
});

@withStyles(styles)
@withSnackbar
class StatefulNeedApplyEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            updating: false,
        };
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (prevState.updating) {
            return {
                updating: false,
            };
        }
        return null;
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren der Bewerbung', {
            variant: 'error',
        });
    }

    toggleApplicationStatus() {
        this.setState({
            updating: true,
        });
        this.props.needsState.updateOwnNeedState(this.props.projectHelperType, this.props.need.id, this.props.need.state === 'NONE' ? 'APPLIED' : 'NONE', err => this.handleFailure());
    }

    getClassName(need) {
        switch (need.state) {
            case 'APPLIED': return this.props.classes.applied;
            case 'APPROVED': return this.props.classes.approved;
            case 'REJECTED': return this.props.classes.rejected;
            default: return this.props.classes.none;
        }
    }

    getDialogText(need) {
        return need.state === 'NONE' ? 'Möchtest du dich für die diese Schicht bewerben?' : 'Möchtest du die Bewerbung für diese Schicht wirklich zurückziehen?';
    }

    getDialogTitle() {
        const date = moment(this.props.need.date, 'x').format('DD.MM.YYYY');
        return this.props.helperTypeName + ' am ' + date + ' ' + this.props.label;
    }

    render() {
        const { classes, label, need, sessionState } = this.props;
        const { updating } = this.state;
        const appliedCount = this.props.needsState.getAppliedCount(need);
        const approvedCount = this.props.needsState.getApprovedCount(need);
        const disabled = !sessionState.hasPermission('ROLE_RIGHT_NEEDS_APPLY') || !need.id || need.quantity === 0 || (need.state !== 'APPROVED' && approvedCount >= need.quantity);
        return updating ? (
            <span className={classes.apply}>
                <CircularProgress size={15} />
            </span>
        ) : need.state !== 'REJECTED' ? (
            <>
                <SimpleDialog
                    onOK={event => this.toggleApplicationStatus(event)}
                    title={this.getDialogTitle()}
                    text={this.getDialogText(need)}
                    okText="Ja"
                    cancelText="Nein"
                >
                    <Button
                        variant={need.state === 'APPLIED' || need.state === 'APPROVED' || need.state === 'REJECTED' ? 'contained' : 'outlined'}
                        disabled={disabled}
                        className={this.getClassName(need)}
                        color="inherit">
                        <span className={!disabled ? classes.buttonText : null}>{label}</span>&nbsp;&nbsp;{appliedCount + approvedCount}/{need.quantity}
                    </Button>
                </SimpleDialog>
            </>
        ) : (
                    <SimpleDialog
                        title={this.getDialogTitle()}
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

const NeedApplyEditComponent = props => (
    <>
        <NeedsContext.Consumer>
            {needsState =>
                (<StatefulNeedApplyEditComponent {...props} needsState={needsState} />)
            }
        </NeedsContext.Consumer>
    </>
);
export default requiresLogin(NeedApplyEditComponent);
