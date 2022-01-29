import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import { green, grey, red, yellow } from '@mui/material/colors';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { RIGHT_NEEDS_APPLY } from '../../permissions';
import { NeedsContext } from '../../providers/needs-provider';
import { convertToDDMMYYYY, requiresLogin } from '../../util';
import SimpleDialog from '../simple-dialog';

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

    getStyle(need) {
        const style = {
            minWidth: '105px',
            width: '100%',
            marginBottom: '3px',
        };
        switch (need.state) {
            case 'APPLIED':
                style.backgroundColor = yellow[600];
                break;
            case 'APPROVED':
                style.backgroundColor = green[600];
                break;
            case 'REJECTED':
                style.backgroundColor = red[600];
                break;
        }
        return style;
    }

    getDialogText(need) {
        return need.state === 'NONE' ? 'Möchtest du dich für die diese Schicht bewerben?' : 'Möchtest du die Bewerbung für diese Schicht wirklich zurückziehen?';
    }

    getDialogTitle() {
        const date = convertToDDMMYYYY(new Date(this.props.need.date));
        return this.props.helperTypeName + ' am ' + date + ' ' + this.props.label;
    }

    render() {
        const { classes, label, need, sessionState } = this.props;
        const { updating } = this.state;
        const appliedCount = this.props.needsState.getAppliedCount(need);
        const approvedCount = this.props.needsState.getApprovedCount(need);
        const disabled = !sessionState.hasPermission(RIGHT_NEEDS_APPLY) || !need.id || need.quantity === 0 || (need.state !== 'APPROVED' && approvedCount >= need.quantity);
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
                        sx={this.getStyle(need)}
                        color="inherit">
                        <Box sx={!disabled ? { color: grey[800] } : {}}>
                            {label}
                        </Box>&nbsp;&nbsp;{appliedCount + approvedCount}/{need.quantity}
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
                    sx={this.getStyle(need)}
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
