import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Grid from '@material-ui/core/Grid';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Redirect } from 'react-router';
import { login, requestPasswordReset } from '../actions/login';
import { SessionContext } from '../providers/session-provider';
import { fullPathOf } from '../paths';

const styles = theme => ({
    container: {
        padding: theme.spacing.unit,
        border: '1px solid',
        borderRadius: '5px',
        borderColor: theme.palette.secondary.light,
        textAlign: 'right',
    },
    button: {
        marginTop: theme.spacing.unit,
    },
});

@withSnackbar
@withStyles(styles, { withTheme: true })
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
        const { classes } = this.props;

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
                                <Grid container justify="center">
                                    <div>
                                        <Typography variant="h3" color="primary" inline={false}>Local Helper Tool</Typography>
                                        <Typography variant="subtitle1" color="secondary" inline={false}>Willkommen</Typography>
                                        <div className={classes.container}>
                                            <TextField
                                                id="username"
                                                autoFocus={true}
                                                label="Email"
                                                type="email"
                                                name="username"
                                                autoComplete="email"
                                                margin="dense"
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
                                                type="password"
                                                autoComplete="current-password"
                                                margin="dense"
                                                variant="outlined"
                                                fullWidth={true}
                                                InputProps={{
                                                    inputRef: ref => this.inputPassword = ref
                                                }}
                                            />
                                            <br />
                                            {this.state.loading ? (<CircularProgress />) : (
                                                <>
                                                    <Button size="small" color="secondary" className={classes.button} onClick={this.openPwResetDialog.bind(this)}>
                                                        Passwort vergessen
		                                        </Button>
                                                    <Button variant="contained" type="submit" className={classes.button}>
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
                                                        margin="dense"
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
                                        </div>
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
