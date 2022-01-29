import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Redirect } from 'react-router-dom';
import { login, requestPasswordReset } from '../actions/login';
import { fullPathOf } from '../paths';
import { SessionContext } from '../providers/session-provider';

@withSnackbar
export default class LoginComponent extends React.Component {

    state = {
        pwResetDialogOpen: false,
        loading: false,
        redirectAdmin: false,
    };

    handleLoginFailure() {
        this.props.enqueueSnackbar('Fehler beim Anmelden', {
            variant: 'error',
        });
        this.setState({
            pwResetDialogOpen: false,
            loading: false,
        });
    }

    closePwResetDialog() {
        this.setState({
            pwResetDialogOpen: false,
            loading: false,
        });
    }

    openPwResetDialog() {
        this.setState({
            pwResetDialogOpen: true,
            loading: false,
        });
    }

    setWaiting() {
        this.setState({
            pwResetDialogOpen: this.state.pwResetDialogOpen,
            loading: true,
            redirectAdmin: true,
        })

    }

    render() {

        return (
            <SessionContext.Consumer>
                {loginState => {
                    if (!loginState.isLoggedIn() || (!this.state.redirectAdmin && loginState.hasPermission('ROLE_ADMIN'))) {
                        return (
                            <form onSubmit={evt => {
                                evt.preventDefault();
                                const email = this.inputUsername.value;
                                const password = this.inputPassword.value;
                                this.inputPassword.value = '';
                                this.setWaiting();
                                login({
                                    loginState,
                                    email,
                                    password,
                                    handleLoginFailure: this.handleLoginFailure.bind(this),
                                });
                            }}>
                                <Grid container justifyContent="center">
                                    <div>
                                        <Typography variant="h3" color="primary">Local Helper Tool</Typography>
                                        <Typography variant="subtitle1" color="secondary">Willkommen</Typography>
                                        <Box sx={{
                                            p: 1,
                                            border: '1px solid',
                                            borderRadius: '5px',
                                            borderColor: 'secondary.light',
                                            textAlign: 'right',
                                        }}>
                                            <TextField
                                                id="username"
                                                autoFocus={true}
                                                label="Email"
                                                type="email"
                                                name="username"
                                                autoComplete="email"
                                                size="small"
                                                variant="outlined"
                                                fullWidth={true}
                                                InputProps={{
                                                    inputRef: ref => this.inputUsername = ref
                                                }}
                                            />
                                            <br />
                                            <TextField
                                                id="password"
                                                label="Passwort"
                                                sx={{ mt: 1 }}
                                                type="password"
                                                autoComplete="current-password"
                                                size="small"
                                                variant="outlined"
                                                fullWidth={true}
                                                InputProps={{
                                                    inputRef: ref => this.inputPassword = ref
                                                }}
                                            />
                                            <br />
                                            {this.state.loading ? (<CircularProgress />) : (
                                                <>
                                                    <Button size="small" color="secondary" sx={{ mt: 1 }} onClick={this.openPwResetDialog.bind(this)}>
                                                        Passwort vergessen
                                                    </Button>
                                                    <Button variant="contained" type="submit" sx={{ mt: 1 }}>
                                                        Anmelden
                                                    </Button>
                                                </>
                                            )}
                                            <Dialog
                                                open={this.state.pwResetDialogOpen}
                                                onClose={this.closePwResetDialog.bind(this)}
                                                aria-labelledby="form-dialog-title"
                                            >
                                                <DialogTitle id="form-dialog-title">Passwort vergessen</DialogTitle>
                                                <DialogContent>
                                                    <DialogContentText>
                                                        Wenn du dein Passwort vergessen hast, kannst du dir per E-Mail einen Link zusenden lassen, mit dem du dein Passwort neu setzen kannst.
                                                    </DialogContentText>
                                                    <TextField
                                                        autoFocus
                                                        size="small"
                                                        id="resetEmail"
                                                        label="E-Mail"
                                                        type="email"
                                                        fullWidth
                                                        InputProps={{
                                                            inputRef: ref => this.inputResetEmail = ref
                                                        }}
                                                    />
                                                </DialogContent>
                                                <DialogActions>
                                                    <Button onClick={this.closePwResetDialog.bind(this)} color="secondary">
                                                        Abbrechen
                                                    </Button>
                                                    <Button onClick={() => {
                                                        const email = this.inputResetEmail.value;
                                                        requestPasswordReset({ email })
                                                            .then(() => {
                                                                this.props.enqueueSnackbar('Anforderung abgesendet', { variant: 'success', });
                                                                this.closePwResetDialog();
                                                            })
                                                            .catch(() => {
                                                                this.props.enqueueSnackbar('Fehler beim Anfordern des Links', { variant: 'error', });
                                                            });
                                                    }} color="primary">
                                                        Link für neues Passwort anfordern
                                                    </Button>
                                                </DialogActions>
                                            </Dialog>
                                        </Box>
                                    </div>
                                </Grid>
                            </form>
                        );
                    } else {
                        return (<Redirect to={fullPathOf()} />);
                    }
                }}
            </SessionContext.Consumer>
        );
    }
}
