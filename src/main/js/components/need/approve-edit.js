import { Button } from '@material-ui/core';
import CircularProgress from '@material-ui/core/CircularProgress';
import { green } from '@material-ui/core/colors';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { withStyles } from '@material-ui/core/styles';
import CheckIcon from '@material-ui/icons/Check';
import CloseIcon from '@material-ui/icons/Close';
import EventAvailableIcon from '@material-ui/icons/EventAvailable';
import EventBusyIcon from '@material-ui/icons/EventBusy';
import React from 'react';
import { Prompt } from 'react-router';
import { changeApplicationStateForNeed } from '../../actions/need';
import { fetchUser } from '../../actions/user';
import { requiresLogin } from '../../util';
import WithPermission from '../with-permission';
import { NeedsContext } from '../../providers/needs-provider';


const styles = theme => ({
    wrapper: {
        maxWidth: '344px',
        width: '100%',
        display: 'inline-block',
        verticalAlign: 'top',
        margin: theme.spacing.unit,
        border: '1px solid ' + theme.palette.primary.main,
        paddingTop: '16px',
    },
    label: {
        fontSize: 'larger',
        textAlign: 'center',
    },
    updating: {
        margin: '16px',
    },
    save: {
        margin: theme.spacing.unit,
        textAlign: 'center',
        paddingBottom: '16px',
    },
    approved: {
        color: green[600],
    }
});

class StatefulNeedApproveEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            updating: false,
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (prevState.updating) {
            return {
                updating: false,
            }
        }
        return null;
    }

    handleFailure(error) {
        console.log(error);
    }

    handleToggle(needUser, state) {
        this.props.needsState.editNeedUser({
            ...needUser,
            state,
        });
    }

    saveNeedUsers() {
        this.props.needsState.saveEditedNeedUsers(this.props.projectHelperType, this.handleFailure.bind(this));
    }

    render() {
        const { classes, label, projectHelperType, needsState } = this.props;
        const { need } = projectHelperType;
        const { updating } = this.state;
        const thingsToSave = needsState.hasNeedEditedUsers(need.id)
        const users = need.users;
        const approved = needsState.getApprovedCount(need);
        return (
            <>
                <Prompt when={thingsToSave} message="Es wurden nicht alle Änderungen gespeichert. Möchtest du diese Seite trotzdem verlassen?" />
                <div className={classes.wrapper}>
                    <div className={classes.label}>
                        {label} ({approved ? approved : 0} / {need.quantity})
                </div>
                    {users ? (
                        <>
                            <List className={classes.root}>
                                {users.map(nu => {
                                    const needUser = needsState.editedNeedUsers.has(nu.id) ? needsState.editedNeedUsers.get(nu.id) : nu;
                                    const user = needUser.user;
                                    return (
                                        <ListItem key={needUser.id} role={null} dense>
                                            {user && user.lastName ? (
                                                <>
                                                    <ListItemText primary={`${user.lastName}, ${user.firstName}`} />
                                                    <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                                                        <IconButton
                                                            disabled={user.state !== 'APPROVED' && approved >= need.quantity}
                                                            onClick={() => this.handleToggle(needUser, needUser.state !== 'APPROVED' ? 'APPROVED' : 'APPLIED')}
                                                        >
                                                            {needUser.state === 'APPROVED' ? (
                                                                <EventAvailableIcon />
                                                            ) : (
                                                                    <CheckIcon />
                                                                )}
                                                        </IconButton>
                                                        <IconButton
                                                            onClick={() => this.handleToggle(needUser, needUser.state !== 'REJECTED' ? 'REJECTED' : 'APPLIED')}
                                                        >
                                                            {needUser.state === 'REJECTED' ? (
                                                                <EventBusyIcon />
                                                            ) : (
                                                                    <CloseIcon />
                                                                )}
                                                        </IconButton>
                                                    </WithPermission>
                                                </>
                                            ) : (
                                                    <>
                                                        <CircularProgress size={15} />
                                                        <ListItemText primary="Loading..." />
                                                    </>
                                                )}
                                        </ListItem>
                                    );
                                })}
                            </List>
                        </>
                    ) : (<br />)}
                    {thingsToSave && (
                        <div className={classes.save}>
                            {updating ? (
                                <CircularProgress />
                            ) : (
                                    <Button variant="contained" onClick={() => this.saveNeedUsers()}>Speichern</Button>
                                )}
                        </div>
                    )}

                </div>
            </>
        );
    }
}
const NeedApproveEditComponent = props => (
    <>
        <NeedsContext.Consumer>
            {needsState =>
                (<StatefulNeedApproveEditComponent {...props} needsState={needsState} />)
            }
        </NeedsContext.Consumer>
    </>
);
export default requiresLogin(withStyles(styles)(NeedApproveEditComponent));
