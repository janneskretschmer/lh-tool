import React from 'react';
import { withSnackbar } from 'notistack';
import { withStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import { ProjectsContext } from '../../providers/projects-provider';
import { SessionContext } from '../../providers/session-provider';
import { deleteProject } from '../../actions/project';
import WithPermission from '../with-permission';
import Typography from '@material-ui/core/Typography';
import Checkbox from '@material-ui/core/Checkbox';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import UserComponent from '../user-detail';
import { createNewUser, updateUser, deleteUser } from '../../actions/user'
import SimpleDialog from '../simple-dialog.js'
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';

const styles = theme => ({
    root: {
    },
    button: {
        margin: theme.spacing.unit,
    },
    leftIcon: {
        marginRight: theme.spacing.unit,
    },
    rightIcon: {
        marginLeft: theme.spacing.unit,
    },
});

@withSnackbar
@withStyles(styles)
export default class ProjectEditPanel extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            editLocalCoordinators: props.project.localCoordinators.length === 0,
            editPublishers: props.project.localCoordinators.length > 0 && props.project.publishers.length === 0,
        };
    }

    handlePublisherEditButtonClicked() {
        this.setState(prevState => ({
            editPublishers: !prevState.editPublishers,
        }))
    }

    handleLocalCoordinatorEditButtonClicked() {
        this.setState(prevState => ({
            editLocalCoordinators: !prevState.editLocalCoordinators,
        }))
    }

    handleDeleteFailure() {
        this.props.enqueueSnackbar('Fehler beim Löschens des Projekts', {
            variant: 'error',
        });
    }

    handleCreateFailure(errorKey) {
        const message = errorKey === 'EX_USER_EMAIL_ALREADY_IN_USE'
            ? 'Die angegebene E-Mail-Adresse wird schon für einen anderen Nutzer verwendet. Wähle bitte eine andere Adresse.'
            : 'Fehler beim Erstellen des neuen Nutzers.';

        this.props.enqueueSnackbar(message, { variant: 'error' });
    }

    render() {
        const { classes, project } = this.props;
        const { editPublishers, editLocalCoordinators } = this.state;
        return (
            <div>
                <SessionContext.Consumer>
                    {sessionState => (
                        <ProjectsContext.Consumer>
                            {projectsState => (
                                <>
                                    {/* <div><Typography variant="h6">Baudiener</Typography></div>
                                    TODO: Auswahlliste der Baudiener<br />
                                    <br /> */}

                                    <Typography variant="h6">
                                    Helferkoordinator 
                                        <IconButton onClick={() => this.handleLocalCoordinatorEditButtonClicked()}>
                                            <Icon>{editLocalCoordinators ? 'close' : 'create'}</Icon>
                                        </IconButton>
                                        {!editLocalCoordinators ? (
                                            <IconButton onClick={() => this.handleLocalCoordinatorEditButtonClicked()}>
                                                <Icon>group_add</Icon>
                                            </IconButton>
                                        ) : null}
                                    </Typography>
                                    {sessionState.hasPermission('ROLE_RIGHT_USERS_CREATE') && sessionState.hasPermission('ROLE_RIGHT_USERS_GRANT_ROLE_LOCAL_COORDINATOR') && editLocalCoordinators  ? (<UserComponent
                                        role="ROLE_LOCAL_COORDINATOR"
                                        showEdit={false}
                                        onSave={(user) => createNewUser({ accessToken: sessionState.accessToken, ...user, projectId:project.id, projectsState, handleFailure: this.handleCreateFailure.bind(this), })}
                                        onlyNewUsers={true}
                                        showDelete={false}
                                    />) : null }
                                    {project.localCoordinators ? project.localCoordinators.map(user => (
                                            <UserComponent user={user}
                                                key={user.email}
                                                role="ROLE_LOCAL_COORDINATOR"
                                                showEdit={sessionState.hasPermission('ROLE_RIGHT_USERS_PUT') && sessionState.hasPermission('ROLE_RIGHT_USERS_GRANT_ROLE_LOCAL_COORDINATOR') && editLocalCoordinators}
                                                onUpdate={(user) => updateUser({accessToken: sessionState.accessToken, user, projectsState})}
                                                showDelete={sessionState.hasPermission('ROLE_RIGHT_USERS_DELETE') && sessionState.hasPermission('ROLE_RIGHT_USERS_GRANT_ROLE_LOCAL_COORDINATOR') && editLocalCoordinators}
                                                onDelete={(user) => deleteUser({accessToken: sessionState.accessToken, userId: user.id, projectsState})}
                                            />
                                        ))
                                    :
                                        <Typography variant="body1">Bitte füge alle geeigneten Verkündiger hinzu.</Typography>
                                    }

                                    <br />
                                    <br />
                                    <Typography variant="h6">
                                        Verkündiger 
                                        <IconButton onClick={() => this.handlePublisherEditButtonClicked()}>
                                            <Icon>{editPublishers ? 'close' : 'create'}</Icon>
                                        </IconButton>
                                        {!editPublishers ? (
                                            <IconButton onClick={() => this.handlePublisherEditButtonClicked()}>
                                                <Icon>group_add</Icon>
                                            </IconButton>
                                        ) : null}
                                    </Typography>
                                    {sessionState.hasPermission('ROLE_RIGHT_USERS_CREATE') && sessionState.hasPermission('ROLE_RIGHT_USERS_GRANT_ROLE_PUBLISHER') && editPublishers ? (<UserComponent
                                        role="ROLE_PUBLISHER"
                                        showEdit={false}
                                        onSave={(user) => createNewUser({ accessToken: sessionState.accessToken, ...user, projectId:project.id, projectsState, handleFailure: this.handleCreateFailure.bind(this), })}
                                        onlyNewUsers={true}
                                        showDelete={false}
                                    />) : null }
                                    {project.publishers ? project.publishers.map(user => (
                                            <UserComponent user={user}
                                                key={user.email}
                                                role="ROLE_PUBLISHER"
                                                showEdit={sessionState.hasPermission('ROLE_RIGHT_USERS_PUT') && sessionState.hasPermission('ROLE_RIGHT_USERS_GRANT_ROLE_PUBLISHER') && editPublishers}
                                                onUpdate={(user) => updateUser({accessToken: sessionState.accessToken, user, projectsState})}
                                                showDelete={sessionState.hasPermission('ROLE_RIGHT_USERS_DELETE') && sessionState.hasPermission('ROLE_RIGHT_USERS_GRANT_ROLE_PUBLISHER') && editPublishers}
                                                onDelete={(user) => deleteUser({accessToken: sessionState.accessToken, userId: user.id, projectsState})}
                                            />
                                        ))
                                    :
                                        <Typography variant="body1">Bitte füge alle geeigneten Verkündiger hinzu.</Typography>
                                    }
                                    <WithPermission permission="ROLE_RIGHT_PROJECTS_DELETE">
                                        <SimpleDialog
                                            title={`Projekt ${project.name} löschen`}
                                            text={`Soll das Projekt ${project.name} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
                                            cancelText="Nein"
                                            okText={`Ja, Projekt ${project.name} löschen`}
                                            onOK={() => {
                                                    deleteProject({
                                                        accessToken: sessionState.accessToken,
                                                        projectsState: projectsState,
                                                        projectId: this.props.project.id,
                                                        handleFailure: this.handleDeleteFailure.bind(this),
                                                    });
                                                }
                                        }>

                                            <Button variant="contained" color="primary" className={classes.button}>
                                                Projekt löschen
                                                <DeleteIcon className={classes.rightIcon} />
                                            </Button>
                                        </SimpleDialog>
                                    </WithPermission>
                                </>
                            )}
                        </ProjectsContext.Consumer>

                    )}
                </SessionContext.Consumer>
            </div>
        );
    }
}