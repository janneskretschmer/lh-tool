import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Checkbox from '@material-ui/core/Checkbox';
import IconButton from '@material-ui/core/IconButton';
import CommentIcon from '@material-ui/icons/Comment';
import { requiresLogin } from '../../util';
import { fetchUser } from '../../actions/user';
import { changeApplicationStateForNeed } from '../../actions/need';
import CircularProgress from '@material-ui/core/CircularProgress';
import { Button } from '@material-ui/core';
import { Prompt } from 'react-router'


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
});

class NeedApproveEditComponent extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            users: props.need.users,
            approved: props.need.approvedCount,
            changes: {},
            saving: false,
        };
    }

    componentWillReceiveProps(props) {
        if (this.props.need.id !== props.need.id) {
            this.setState({
                users: props.need.users,
                approved: props.need.approvedCount,
                changes: {},
            }, this.componentDidMount)
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
                                if (user.userId === result.response.id) {
                                    user.firstName = result.response.firstName
                                    user.lastName = result.response.lastName
                                }
                                return user
                            })
                        })
                    })
                }
            });
        }
    }

    handleToggle = value => {
        const newState = value.state === 'APPROVED' ? 'APPLIED' : 'APPROVED'
        value.state = newState
        let diff = (newState === 'APPROVED' ? 1 : -1)
        this.setState(prevState => {
            let res = {
                ...prevState,
                users: prevState.users.map(user => user.userId === value.userId ? value : user),
                approved: prevState.approved + diff,
            }
            res.changes[value.userId] = newState
            return res
        });

        this.props.onApprove(diff)
    };

    save() {
        let requests = []
        this.setState({
            saving: true,
        })
        for (let [userId, newState] of Object.entries(this.state.changes)) {
            requests.push(
                changeApplicationStateForNeed({
                    accessToken: this.props.sessionState.accessToken,
                    userId,
                    needId: this.props.need.id,
                    state: newState,
                    handleFailure: err => { }
                })
            )
        };
        Promise.all(requests).then(() => this.setState({
            saving: false,
            changes: {},
        }))
    }

    render() {
        const { classes, label, need } = this.props
        const { users, approved, changes, saving } = this.state
        const thingsToSave = Object.entries(changes).length > 0
        return (
            <>
                <Prompt when={thingsToSave} message="Es wurden nicht alle Änderungen gespeichert. Möchtest du diese Seite trotzdem verlassen?" />
                <div className={classes.wrapper}>
                    <div className={classes.label}>
                        {label} ({approved} / {need.quantity})
                </div>
                    {users ? (
                        <>
                            <List className={classes.root}>
                                {users.map(user => {
                                    return (
                                        <ListItem key={user.id} role={null} dense button onClick={() => this.handleToggle(user)} disabled={user.state !== 'APPROVED' && approved >= need.quantity}>
                                            {user.lastName ? (
                                                <>
                                                    {user.updating ? (
                                                        <CircularProgress size={16} className={classes.updating} />
                                                    ) : (
                                                            <Checkbox
                                                                checked={user.state === 'APPROVED'}
                                                                disabled={user.state !== 'APPROVED' && approved > need.quantity}
                                                                disableRipple
                                                            />
                                                        )}
                                                    <ListItemText primary={`${user.lastName}, ${user.firstName}`} />
                                                </>
                                            ) : (
                                                    <>
                                                        <CircularProgress size={15} />
                                                        <ListItemText primary="Loading..." />
                                                    </>
                                                )}
                                        </ListItem>
                                    )
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
        )
    }
}

export default requiresLogin(withStyles(styles)(NeedApproveEditComponent));
