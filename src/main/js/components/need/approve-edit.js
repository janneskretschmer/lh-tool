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

class NeedApproveEditComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            users: props.need.users,
            approved: props.need.approvedCount,
            changes: new Map(),
            saving: false,
        };
    }

    componentWillReceiveProps(props) {
        if (this.props.need.id !== props.need.id) {
            this.setState(prevState => ({
                users: props.need.users,
                approved: props.need.approvedCount,
            }), this.componentDidMount);
        }
    }


    componentDidMount() {
        if (this.state.users) {
            let self = this;
            this.state.users.forEach(user => {
                if (!user.lastName) {
                    fetchUser({ accessToken: this.props.sessionState.accessToken, userId: user.userId }).then(result => {
                        self.setState({
                            users: self.state.users.map(user => {
                                if(user.userId === result.id) {
                                    user.firstName = result.firstName;
                                    user.lastName = result.lastName;
                                }
                                return user;
                            })
                        });
                    });
                }
            });
        }
    }

    handleToggle = (value, newState) => {
        value.state = newState;
        this.setState(prevState => {
            let users = prevState.users.map(user => user.userId === value.userId ? value : user);
            let approved = users.filter(user => user.state === 'APPROVED').length;
            let res = {
                ...prevState,
                users,
                approved,
            };
            let needId = this.props.need.id;
            if (!res.changes.has(needId)) {
                res.changes.set(needId, new Map());
            }
            res.changes.get(needId).set(value.userId, newState);

            this.props.onApprove(approved);

            return res;
        });
    };

    save() {
        let requests = [];
        this.setState({
            saving: true,
        });
        for (let [userId, newState] of this.state.changes.get(this.props.need.id)) {
            requests.push(
                changeApplicationStateForNeed({
                    accessToken: this.props.sessionState.accessToken,
                    userId,
                    needId: this.props.need.id,
                    state: newState,
                    handleFailure: err => { }
                })
            );
        };
        Promise.all(requests).then(() => this.setState({
            saving: false,
            changes: new Map(),
        }));
    }

    render() {
        const { classes, label, need } = this.props;
        const { users, approved, changes, saving } = this.state;
        const thingsToSave = !!(changes.has(need.id) && changes.get(need.id).size > 0);
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
                                {users.map(user => {
                                    return (
                                        <ListItem key={user.id} role={null} dense>
                                            {user.lastName ? (
                                                <>
                                                    <ListItemText primary={`${user.lastName}, ${user.firstName}`} />
                                                    {user.updating ? (
                                                        <CircularProgress size={16} className={classes.updating} />
                                                    ) : (
                                                            <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                                                                <IconButton
                                                                    disabled={user.state !== 'APPROVED' && approved >= need.quantity}
                                                                    onClick={() => this.handleToggle(user, user.state !== 'APPROVED' ? 'APPROVED' : 'APPLIED')}
                                                                >
                                                                    {user.state === 'APPROVED' ? (
                                                                        <EventAvailableIcon />
                                                                    ) : (
                                                                            <CheckIcon />
                                                                        )}
                                                                </IconButton>
                                                                <IconButton
                                                                    onClick={() => this.handleToggle(user, user.state !== 'REJECTED' ? 'REJECTED' : 'APPLIED')}
                                                                >
                                                                    {user.state === 'REJECTED' ? (
                                                                        <EventBusyIcon />
                                                                    ) : (
                                                                            <CloseIcon />
                                                                        )}
                                                                </IconButton>
                                                            </WithPermission>
                                                        )}
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
                            {saving ? (
                                <CircularProgress />
                            ) : (
                                    <Button variant="contained" onClick={this.save.bind(this)}>Speichern</Button>
                                )}
                        </div>
                    )}

                </div>
            </>
        );
    }
}

export default requiresLogin(withStyles(styles)(NeedApproveEditComponent));
