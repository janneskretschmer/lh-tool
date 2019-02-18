import React from 'react';
import { Helmet } from 'react-helmet';
import { SessionContext } from '../providers/session-provider';
import { login } from '../actions/login';
import Grid from '@material-ui/core/Grid';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

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

@withStyles(styles, { withTheme: true })
export default class LoginComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        const { classes } = this.props;

        return (
            <SessionContext.Consumer>
                {loginState => (
                    <form onSubmit={evt => {
                        evt.preventDefault();
                        const email = this.inputUsername.value;
                        const password = this.inputPassword.value;
                        this.inputPassword.value = '';
                        login({ loginState, email, password });
                    }}>
                        <Helmet titleTemplate="Login - %s" />
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
                                    <Button size="small" color="secondary" className={classes.button}>
                                        Passwort vergessen
                                    </Button>
                                    <Button variant="contained" type="submit" className={classes.button}>
                                        Anmelden
                                    </Button>
                                </div></div>
                        </Grid>
                    </form>
                )}
            </SessionContext.Consumer>
        );
    }
}
