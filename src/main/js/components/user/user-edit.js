import CircularProgress from '@material-ui/core/CircularProgress';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { requiresLogin } from '../../util';
import { UsersContext } from '../../providers/users-provider';
import Button from '@material-ui/core/Button';
import { requestPasswordReset } from '../../actions/login';
import { PageContext } from '../../providers/page-provider';

const styles = theme => ({
    input: {
        margin: '2px',
    },
    button: {
        margin: '2px',
    },
    skills: {
        margin: '2px',
        maxWidth: '694px',
        width: '100%',
    },
});

@withStyles(styles)
@withSnackbar
class StatefulUserEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            //copy of user in provider
            user: null,
            passwordEmailSent: false,
        }
    }

    componentDidMount() {
        this.props.usersState.selectUser(this.props.match.params.userId, () => this.handleFailure()).then(user => {
            this.setState({ user });
            this.props.pagesState.setCurrentItemName({ name: user.firstName + ' ' + user.lastName });
        });
    }

    save() {
        const user = this.state.user;
        this.setState({ user: null });
        this.props.usersState.createOrUpdateUser(user, error => this.handleFailure()).then(savedUser => this.setState({ user: savedUser },
            () => this.props.enqueueSnackbar('Benutzer gespeichert', { variant: 'success', })));
    }

    cancel() {
        this.setState({
            user: this.props.usersState.getSelectedUser(),
        });
    }

    changePassword() {
        requestPasswordReset({ email: this.state.user.email })
            .then(() => {
                this.props.enqueueSnackbar('Anforderung abgesendet', { variant: 'success', });
                this.setState({ passwordEmailSent: true });
            })
            .catch(() => {
                this.props.enqueueSnackbar('Fehler beim Anfordern des Links', { variant: 'error', });
            });
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren der Benutzerdaten', {
            variant: 'error',
        });
    }

    handleFirstNameChanged(firstName) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                firstName,
            },
        }));
    }

    handleLastNameChanged(lastName) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                lastName,
            },
        }));
    }

    handleEmailChanged(email) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                email,
            },
        }));
    }

    handleTelephoneNumberChanged(telephoneNumber) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                telephoneNumber,
            },
        }));
    }

    handleMobileNumberChanged(mobileNumber) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                mobileNumber,
            },
        }));
    }

    handleBusinessNumberChanged(businessNumber) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                businessNumber,
            },
        }));
    }

    handleProfessionChanged(profession) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                profession,
            },
        }));
    }

    handleSkillsChanged(skills) {
        this.setState(prevState => ({
            user: {
                ...prevState.user,
                skills,
            },
        }));
    }

    render() {
        const { classes, usersState } = this.props;
        const { user, passwordEmailSent } = this.state;
        const newUser = !usersState.selectedUserId;
        return user ? (
            <>
                {newUser ? (
                    <>
                        <TextField
                            id="first_name"
                            value={user.firstName}
                            label="Vorname"
                            type="text"
                            name="first_name"
                            autoComplete="first name"
                            margin="dense"
                            variant="outlined"
                            onChange={event => this.handleFirstNameChanged(event.target.value)}
                            className={classes.input}
                            required
                        />
                        <TextField
                            id="last_name"
                            value={user.lastName}
                            label="Nachname"
                            type="text"
                            name="last_name"
                            autoComplete="last name"
                            margin="dense"
                            variant="outlined"
                            onChange={event => this.handleLastNameChanged(event.target.value)}
                            className={classes.input}
                            required
                        />
                        <TextField
                            id="email"
                            value={user.email}
                            label="Email"
                            type="email"
                            name="email"
                            autoComplete="email"
                            margin="dense"
                            variant="outlined"
                            onChange={event => this.handleEmailChanged(event.target.value)}
                            className={classes.input}
                            required
                        /><br />
                    </>
                ) : (
                        <>Benutzereintstellungen für {user.firstName} {user.lastName}<br /><br /></>

                    )
                }
                <TextField
                    id="telephone_number"
                    value={user.telephoneNumber || ''}
                    label="Telefon Festnetz"
                    type="text"
                    name="telephone_number"
                    autoComplete="telephone number"
                    margin="dense"
                    variant="outlined"
                    onChange={event => this.handleTelephoneNumberChanged(event.target.value)}
                    className={classes.input}
                />
                <TextField
                    id="mobile_number"
                    value={user.mobileNumber || ''}
                    label="Telefon Mobil"
                    type="text"
                    name="mobile_number"
                    autoComplete="telephone mobile number"
                    margin="dense"
                    variant="outlined"
                    onChange={event => this.handleMobileNumberChanged(event.target.value)}
                    className={classes.input}
                />
                <TextField
                    id="business_number"
                    value={user.businessNumber || ''}
                    label="Telefon Geschäftlich"
                    type="text"
                    name="business_number"
                    autoComplete="telephone business number"
                    margin="dense"
                    variant="outlined"
                    onChange={event => this.handleBusinessNumberChanged(event.target.value)}
                    className={classes.input}
                /><br />
                {
                    !newUser ? (
                        passwordEmailSent ? (
                            <>
                                Es wurde eine Email an {user.email} geschickt.<br /> Mit dem Link in der Email kann das Passwort geändert werden.
                            </>
                        ) : (
                                <Button variant="outlined" className={classes.button} type="submit" onClick={() => this.changePassword()}>
                                    Passwort ändern
                                </Button>
                            )
                    ) : null
                }
                <br /><br />
                <TextField
                    id="profession"
                    label="Beruf"
                    value={user.profession || ''}
                    margin="dense"
                    variant="outlined"
                    onChange={event => this.handleProfessionChanged(event.target.value)}
                    className={classes.input}
                /><br />
                <TextField
                    id="skills"
                    label="Fähigkeiten"
                    multiline
                    value={user.skills || ''}
                    margin="dense"
                    variant="outlined"
                    onChange={event => this.handleSkillsChanged(event.target.value)}
                    className={classes.skills}
                /><br /><br />
                <Button variant="contained" className={classes.button} type="submit" onClick={() => this.save()}>
                    Speichern
                </Button>
                <Button variant="outlined" className={classes.button} type="submit" onClick={() => this.cancel()}>
                    Abbrechen
                </Button>
            </>
        ) : (<CircularProgress />);
    }
}

const UserEditComponent = props => (
    <>
        <UsersContext.Consumer>
            {usersState =>
                (
                    <PageContext.Consumer>
                        {pagesState => (<StatefulUserEditComponent {...props} usersState={usersState} pagesState={pagesState} />)}
                    </PageContext.Consumer>
                )
            }
        </UsersContext.Consumer>
    </>
);
export default requiresLogin(UserEditComponent);
