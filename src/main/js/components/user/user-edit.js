import { Checkbox, FormControlLabel, Tooltip, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import TextField from '@mui/material/TextField';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { requestPasswordReset } from '../../actions/login';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../../config';
import { EX_NO_EMAIL, EX_NO_FIRST_NAME, EX_NO_GENDER, EX_NO_LAST_NAME, EX_USER_EMAIL_ALREADY_IN_USE } from '../../exceptions';
import { fullPathOfUserSettings, fullPathOfUsersSettings } from '../../paths';
import { RIGHT_USERS_GET } from '../../permissions';
import { PageContext } from '../../providers/page-provider';
import { UsersContext } from '../../providers/users-provider';
import { getRoleName, requiresLogin } from '../../util';
import LenientRedirect from '../util/lenient-redirect';

@withSnackbar
class StatefulUserEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            saving: false,
            passwordEmailSent: false,
            redirectUrl: null,
            userId: null,
        };
    }

    componentDidUpdate() {
        const { pagesState, usersState, match } = this.props;
        if (this.state.userId !== match.params.userId) {
            this.setState({
                userId: match.params.userId,
            }, () => this.props.usersState.selectUser(this.props.match.params.userId,
                error => this.handleFailure(error)));
        }
        if (usersState.selectedUser && pagesState.currentItemName !== (usersState.selectedUser.firstName + ' ' + usersState.selectedUser.lastName)) {
            pagesState.setCurrentItemName({ name: usersState.selectedUser.firstName + ' ' + usersState.selectedUser.lastName });
        } else if (!usersState.selectedUser && pagesState.currentItemName) {
            pagesState.setCurrentItemName({ name: null });
        }
    }

    save(redirectToUser) {
        this.setState({ saving: true });
        const { usersState, match } = this.props;
        usersState.saveSelectedUser()
            .then(() => this.props.enqueueSnackbar('Benutzer gespeichert', { variant: 'success', }))
            .then(() => {
                let redirectUrl;
                if (redirectToUser) {
                    if (match.params.userId === redirectToUser) {
                        usersState.selectUser(redirectToUser, error => this.handleFailure(error));
                    } else {
                        redirectUrl = fullPathOfUserSettings(redirectToUser);
                    }
                } else {
                    if (this.props.sessionState.hasPermission(RIGHT_USERS_GET)) {
                        redirectUrl = fullPathOfUsersSettings();
                    } else if (match.params.userId !== usersState.selectedUser.id) {
                        redirectUrl = fullPathOfUserSettings(usersState.selectedUser.id);
                    }
                }
                redirectUrl && this.setState({ redirectUrl });
            })
            .catch(error => this.handleFailure(error)).finally(() => this.setState({ saving: false }));
    }

    cancel() {
        const { usersState } = this.props;
        usersState.resetSelectedUser();
        if (this.props.sessionState.hasPermission(RIGHT_USERS_GET)) {
            this.setState({
                redirectUrl: fullPathOfUsersSettings(),
            });
        }
    }

    changePassword() {
        requestPasswordReset({ email: this.props.usersState.selectedUser.email })
            .then(() => {
                this.props.enqueueSnackbar('Anforderung abgesendet', { variant: 'success', });
                this.setState({ passwordEmailSent: true });
            })
            .catch(() => {
                this.props.enqueueSnackbar('Fehler beim Anfordern des Links', { variant: 'error', });
            });
    }

    handleFailure(error) {
        let message;
        if (error && error.response && error.response.key) {
            if (error.response.key === EX_NO_EMAIL) {
                message = 'Es wurde keine gültige Email-Adresse angegeben.';
            } else if (error.response.key === EX_NO_FIRST_NAME) {
                message = 'Es wurde kein Vorname angegeben.';
            } else if (error.response.key === EX_NO_LAST_NAME) {
                message = 'Es wurde kein Nachname angegeben.';
            } else if (error.response.key === EX_NO_GENDER) {
                message = 'Es wurde kein Geschlecht angegeben.';
            } else if (error.response.key === EX_USER_EMAIL_ALREADY_IN_USE) {
                message = 'Diese Email-Adresse wird bereits verwendet.';
            }
        } else {
            message = 'Fehler beim Aktualisieren der Benutzerdaten.';
        }
        this.props.enqueueSnackbar(message, {
            variant: 'error',
        });
    }

    redirect(redirectUrl) {
        this.setState({ redirectUrl });
    }

    render() {
        const { usersState, pagesState } = this.props;
        const { passwordEmailSent, saving, redirectUrl } = this.state;
        if (redirectUrl) {
            return (<LenientRedirect to={redirectUrl} onSamePage={() => this.redirect(null)} />);
        }
        const user = usersState.selectedUser;
        const isNewUser = usersState.selectedUser && !usersState.selectedUser.id;
        const disableSave = !usersState.isUserValid();
        return user ? (
            <>
                <Box sx={{
                    display: 'inline-block',
                    verticalAlign: 'top',
                    mr: 3,
                    mb: 3,
                }}>
                    {isNewUser ? (
                        <>
                            <TextField
                                id="first_name"
                                value={user.firstName || ''}
                                label="Vorname"
                                type="text"
                                name="first_name"
                                autoComplete="first name"
                                size="small"
                                variant="outlined"
                                onChange={event => usersState.changeFirstName(event.target.value)}
                                sx={{ mr: 1, mb: 1 }}
                                required
                            />
                            <TextField
                                id="last_name"
                                value={user.lastName || ''}
                                label="Nachname"
                                type="text"
                                name="last_name"
                                autoComplete="last name"
                                size="small"
                                variant="outlined"
                                onChange={event => usersState.changeLastName(event.target.value)}
                                sx={{ mr: 1, mb: 1 }}
                                required
                            />
                            <TextField
                                id="email"
                                value={user.email || ''}
                                label="Email"
                                type="email"
                                name="email"
                                autoComplete="email"
                                size="small"
                                variant="outlined"
                                onChange={event => usersState.changeEmail(event.target.value)}
                                sx={{ mb: 1 }}
                                required
                            /><br />
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={user.gender === 'FEMALE'}
                                        value="gender"
                                        onChange={event => usersState.changeGender(event.target.checked ? 'FEMALE' : 'MALE')}
                                    />
                                }
                                label="Schwester"
                            /><br />
                        </>
                    ) : (
                        <Typography variant='h6'>Benutzereintstellungen für {user.firstName} {user.lastName}</Typography>

                    )
                    }
                    <br />
                    <TextField
                        id="telephone_number"
                        value={user.telephoneNumber || ''}
                        label="Telefon Festnetz"
                        type="text"
                        name="telephone_number"
                        autoComplete="telephone number"
                        size="small"
                        variant="outlined"
                        onChange={event => usersState.changeTelephoneNumber(event.target.value)}
                        sx={{ mr: 1, mb: 1 }}
                    />
                    <TextField
                        id="mobile_number"
                        value={user.mobileNumber || ''}
                        label="Telefon Mobil"
                        type="text"
                        name="mobile_number"
                        autoComplete="telephone mobile number"
                        size="small"
                        variant="outlined"
                        onChange={event => usersState.changeMobileNumber(event.target.value)}
                        sx={{ mr: 1, mb: 1 }}
                    />
                    <TextField
                        id="business_number"
                        value={user.businessNumber || ''}
                        label="Telefon Geschäftlich"
                        type="text"
                        name="business_number"
                        autoComplete="telephone business number"
                        size="small"
                        variant="outlined"
                        onChange={event => usersState.changeBusinessNumber(event.target.value)}
                        sx={{ mb: 1 }}
                    /><br />
                    {
                        !isNewUser ? (
                            passwordEmailSent ? (
                                <>
                                    Es wurde eine Email an {user.email} geschickt.<br /> Mit dem Link in der Email kann das Passwort geändert werden.
                                </>
                            ) : (
                                <Button variant="outlined" type="submit" onClick={() => this.changePassword()}>
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
                        size="small"
                        variant="outlined"
                        onChange={event => usersState.changeProfession(event.target.value)}
                        sx={{ mb: 1 }}
                    /><br />
                    <TextField
                        id="skills"
                        label="Fähigkeiten"
                        multiline
                        value={user.skills || ''}
                        size="small"
                        variant="outlined"
                        onChange={event => usersState.changeSkills(event.target.value)}
                        sx={{
                            width: '100%',
                        }}
                    />
                </Box>

                {usersState.roles && usersState.roles.length > 0 ? (<>
                    <Box sx={{
                        display: 'inline-block',
                        verticalAlign: 'top',
                        mr: 3,
                        mb: 3,
                    }}>
                        <Typography variant='h6'>Rollen</Typography>
                        {usersState.roles.map(role => (
                            <div key={role.role}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={!!user.roles && !!user.roles.find(userRole => userRole.role === role.role)}
                                            value="role"
                                            onChange={event => usersState.toggleRole(role.role)}
                                        />
                                    }
                                    label={getRoleName(role.role)}
                                />
                                <br />
                            </div>
                        ))}
                    </Box>
                </>) : null}
                {usersState.projects && usersState.projects.length > 0 ? (<>
                    <Box sx={{
                        display: 'inline-block',
                        verticalAlign: 'top',
                        mr: 3,
                        mb: 3,
                    }}>
                        <Typography variant='h6'>Projekte</Typography>
                        {usersState.projects.map(project => (
                            <div key={project.id}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={!!user.projects && !!user.projects.find(userProject => userProject.projectId === project.id)}
                                            value="project"
                                            onChange={event => usersState.toggleProject(project.id)}
                                        />
                                    }
                                    label={project.name}
                                />
                                <br />
                            </div>
                        ))}
                    </Box>
                </>) : null}

                <br />
                {saving ? (<CircularProgress />) : (<>
                    <Button variant="contained" disabled={disableSave} sx={{ mr: 1 }} type="submit" onClick={() => this.save()}>
                        Speichern
                    </Button>
                    {isNewUser && (
                        <Tooltip title="Benutzerdaten speichern und anschließend auf diese Seite zurückkehren">
                            <span>
                                <Button variant="contained" disabled={disableSave} sx={{ mr: 1 }} type="submit" onClick={() => this.save(NEW_ENTITY_ID_PLACEHOLDER)}>
                                    Speichern &amp; Neu erstellen
                                </Button>
                            </span>
                        </Tooltip>
                    )}
                    <Button variant="outlined" type="submit" onClick={() => this.cancel()}>
                        Abbrechen
                    </Button>
                </>)}
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
