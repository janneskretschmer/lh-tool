import React from 'react';
import { Helmet } from 'react-helmet';
import URI from 'urijs';
import { withSnackbar } from 'notistack';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { SessionContext } from '../providers/session-provider';
import { changePassword } from '../actions/user';
import { withContext } from '../util';

@withSnackbar
@withContext('sessionState', SessionContext)
export default class ChangePasswordComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    getUserCredentials(sessionState) {
        const { uid, token } = URI(this.props.location.search).query(true);
        const isTokenBased = !!token && !!uid;
        const userId = isTokenBased ? uid : sessionState.isLoggedIn() ? sessionState.currentUser.id : null;
        return userId ? { userId, token, isTokenBased } : null;
    }

    componentWillMount() {
        if (!this.getUserCredentials(this.props.sessionState)) {
            // FUTURE: Redirect for login
        }
    }

    render() {
        return (
            <SessionContext.Consumer>
                {sessionState => {
                    const credentials = this.getUserCredentials(sessionState);

                    if (!credentials) {
                        return (
                            <>
                                <Helmet titleTemplate="Passwort ändern - %s" />
                                <Typography component="h2" variant="h1" gutterBottom>
                                    Nicht eingeloggt
                                </Typography>
                                <Typography variant="body1" gutterBottom>
                                    So kannst du dein Passwort nicht ändern
                                </Typography>
                            </>
                        )
                    }

                    return (
                        <>
                            <Helmet titleTemplate="Passwort ändern - %s" />
                            <form onSubmit={evt => {
                                evt.preventDefault();
                                const { userId, token } = credentials;
                                const oldPassword = this.inputPasswordCurrent ? this.inputPasswordCurrent.value : null;
                                const newPassword = this.inputPasswordNew.value;
                                const confirmPassword = this.inputPasswordNewConfirm.value;
                                changePassword({ userId, token, oldPassword, newPassword, confirmPassword })
                                .then(user => {
                                    this.props.enqueueSnackbar(`Passwort für ${user.firstName} ${user.lastName} geändert`, { variant: 'success', });
                                })
                                .catch(() => {
                                    this.props.enqueueSnackbar('Fehler beim Ändern des Passworts', { variant: 'error', });
                                });
                            }}>
                                {!credentials.isTokenBased ? (
                                    <TextField
                                        id="passwordCurrent"
                                        label="Aktuelles Passwort"
                                        type="password"
                                        autoComplete="current-password"
                                        margin="dense"
                                        variant="outlined"
                                        fullWidth={true}
                                        InputProps={{
                                            inputRef: ref => this.inputPasswordCurrent = ref
                                        }}
                                    />
                                ) : null}
                                <br />
                                <TextField
                                    id="passwordNew"
                                    label="Neues Passwort"
                                    type="password"
                                    margin="dense"
                                    variant="outlined"
                                    fullWidth={true}
                                    InputProps={{
                                        inputRef: ref => this.inputPasswordNew = ref
                                    }}
                                />
                                <br />
                                <TextField
                                    id="passwordNewConfirm"
                                    label="Neues Passwort bestätigen"
                                    type="password"
                                    margin="dense"
                                    variant="outlined"
                                    fullWidth={true}
                                    InputProps={{
                                        inputRef: ref => this.inputPasswordNewConfirm = ref
                                    }}
                                />
                                <br />
                                <Button color="primary" type="submit">
                                    Passwort ändern
                                </Button>
                            </form>
                        </>
                    );
                }}
            </SessionContext.Consumer>
        );
    }
}
