import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import { Button } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Prompt } from 'react-router-dom';
import { RIGHT_NEEDS_APPROVE } from '../../permissions';
import { NeedsContext } from '../../providers/needs-provider';
import { requiresLogin } from '../../util';
import WithPermission from '../with-permission';

@withSnackbar
class StatefulNeedApproveEditComponent extends React.Component {

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
        this.props.enqueueSnackbar('Fehler beim Speichern der Zuteilungen', {
            variant: 'error',
        });
    }

    handleToggle(needUser, state) {
        this.props.needsState.editNeedUser({
            ...needUser,
            state,
        });
    }

    saveNeedUsers() {
        this.props.needsState.saveEditedNeedUsers(this.props.projectHelperType, err => this.handleFailure());
    }

    render() {
        const { classes, label, projectHelperType, needsState } = this.props;
        const { need } = projectHelperType;
        const { updating } = this.state;
        const thingsToSave = needsState.hasNeedEditedUsers(need.id);
        const users = need.users;
        const approved = needsState.getApprovedCount(need);
        return <>
            <Prompt when={thingsToSave} message="Es wurden nicht alle Änderungen gespeichert. Möchtest du diese Seite trotzdem verlassen?" />
            <Box sx={{
                maxWidth: '344px',
                width: '100%',
                display: 'inline-block',
                verticalAlign: 'top',
                m: 1,
                border: '1px solid',
                borderColor: 'primary.main',
                pt: 2,
            }}>
                <Box sx={{
                    fontSize: 'larger',
                    textAlign: 'center',
                }}>
                    {label} ({approved ? approved : 0} / {need.quantity})
                </Box>
                {users ? (
                    <>
                        <List>
                            {users.map(nu => {
                                const needUser = needsState.editedNeedUsers.has(nu.id) ? needsState.editedNeedUsers.get(nu.id) : nu;
                                const user = needUser.user;
                                return (
                                    <ListItem key={needUser.id} role={null} dense>
                                        {user && user.lastName ? (
                                            <>
                                                <ListItemText primary={`${user.lastName}, ${user.firstName}`} />
                                                <WithPermission permission={RIGHT_NEEDS_APPROVE}>
                                                    <IconButton
                                                        disabled={needUser.state !== 'APPROVED' && approved >= need.quantity}
                                                        onClick={() => this.handleToggle(needUser, needUser.state !== 'APPROVED' ? 'APPROVED' : 'APPLIED')}
                                                        size="large">
                                                        {needUser.state === 'APPROVED' ? (
                                                            <EventAvailableIcon />
                                                        ) : (
                                                            <CheckIcon />
                                                        )}
                                                    </IconButton>
                                                    <IconButton
                                                        disabled={needUser.state === 'REJECTED'}
                                                        onClick={() => this.handleToggle(needUser, needUser.state !== 'REJECTED' ? 'REJECTED' : 'APPLIED')}
                                                        size="large">
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
                    <Box sx={{
                        m: 1,
                        textAlign: 'center',
                        pb: 2,
                    }}>
                        {updating ? (
                            <CircularProgress />
                        ) : (
                            <Button variant="contained" onClick={() => this.saveNeedUsers()}>Speichern</Button>
                        )}
                    </Box>
                )}

            </Box>
        </>;
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
export default requiresLogin(NeedApproveEditComponent);
