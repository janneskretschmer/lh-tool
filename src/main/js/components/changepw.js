import { Checkbox, FormControlLabel } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Redirect } from 'react-router';
import URI from 'urijs';
import { changePassword } from '../actions/user';
import { fullPathOfDataProtection, fullPathOfLogin } from '../paths';
import { SessionContext } from '../providers/session-provider';
import { withContext } from '../util';
import SimpleDialog from './simple-dialog';

@withSnackbar
@withContext('sessionState', SessionContext)
export default class ChangePasswordComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            success: false,
            redirect: false,
            checkedDataprotection: false,
        };
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

    success() {
        this.setState({
            success: true,
        });
    }

    redirect() {
        this.setState({
            redirect: true,
        });
    }

    checkDataprotection(event) {
        this.setState({
            checkedDataprotection: event.target.checked,
        });
    }

    render() {
        return (
            <SessionContext.Consumer>
                {sessionState => {
                    const credentials = this.getUserCredentials(sessionState);

                    const { redirect, success, checkedDataprotection } = this.state;

                    if (!credentials) {
                        return (
                            <>
                                <Typography component="h2" variant="h1" gutterBottom>
                                    Nicht eingeloggt
                                </Typography>
                                <Typography variant="body1" gutterBottom>
                                    So kannst du dein Passwort nicht ändern
                                </Typography>
                            </>
                        );
                    }

                    if (redirect) {
                        return (
                            <Redirect to={fullPathOfLogin()} />
                        );
                    }

                    if (success) {
                        return (
                            <>
                                <SimpleDialog
                                    open={true}
                                    title="Vielen Dank für deine Anmeldung"
                                    text="Du wirst jetzt auf die Login-Seite umgeleitet. Dort kannst du dich in Zukunft mit deiner E-Mail-Adresse und dem gewählten Passwort anmelden und deine Schichten verwalten."
                                    okText="OK"
                                    onOK={this.redirect.bind(this)}
                                >
                                </SimpleDialog>
                            </>
                        );
                    }

                    return (
                        <>
                            <form onSubmit={evt => {
                                evt.preventDefault();
                                const { userId, token } = credentials;
                                const oldPassword = this.inputPasswordCurrent ? this.inputPasswordCurrent.value : null;
                                const newPassword = this.inputPasswordNew.value;
                                const confirmPassword = this.inputPasswordNewConfirm.value;
                                changePassword({ userId, token, oldPassword, newPassword, confirmPassword })
                                    .then(user => {
                                        this.success();
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
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={checkedDataprotection}
                                            onChange={this.checkDataprotection.bind(this)}
                                        />
                                    }
                                    label={(<>Ich stimme der <a href={fullPathOfDataProtection()} target="_blank">Datenschutzerklärung</a> dieser Webseite zu.</>)}
                                />
                                <br />
                                <Button disabled={!checkedDataprotection} color="primary" variant="contained" type="submit">
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
