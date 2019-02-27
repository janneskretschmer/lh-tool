import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import { deleteProject } from '../actions/project';
import Typography from '@material-ui/core/Typography';
import UserComponent from './user-detail';
import { createNewUser, updateUser, deleteUser } from '../actions/user'
import SimpleDialog from './simple-dialog.js'
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

@withStyles(styles)
export default class ProjectEditPanel extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            editPublishers: false,
        };
    }

    handlePublisherEditButtonClicked() {
        this.setState({
            editPublishers: !this.state.editPublishers,
            ...this.state,
        })
    }

    render() {
        const { classes, project } = this.props;
        return (
            <div>
                <SessionContext.Consumer>
                    {sessionState => (
                        <ProjectsContext.Consumer>
                            {projectsState => (
                                <>
                                    <div><Typography variant="h6">Baudiener</Typography></div>
                                    <div><Typography variant="h6">Lokaler Koordinator</Typography></div>
                                    <UserComponent
                                        user={project.localCoordinator}
                                        role="ROLE_LOCAL_COORDINATOR"
                                        showEdit={true}
                                        onSave={user => createNewUser({ accessToken: sessionState.accessToken, ...user, projectId:project.id, projectsState })}
                                        onUpdate={user => updateUser({accessToken: sessionState.accessToken, user, projectsState})}
                                        showDelete={true}
                                        onDelete={user => deleteUser({accessToken: sessionState.accessToken, userId: user.id, projectsState})}
                                    />
                                    <Typography variant="h6">Verkündiger 
                                        <IconButton onClick={() => this.handlePublisherEditButtonClicked()}>
                                            <Icon>{this.state.editPublishers ? 'close' : 'create'}</Icon>
                                        </IconButton>
                                    </Typography>
                                    <UserComponent
                                        role="ROLE_PUBLISHER"
                                        showEdit={false}
                                        onSave={(user) => createNewUser({ accessToken: sessionState.accessToken, ...user, projectId:project.id, projectsState })}
                                        onlyNewUsers={true}
                                        showDelete={false}
                                    />
                                    {project.publishers ? project.publishers.map(user => (
                                            <UserComponent user={user}
                                                role="ROLE_PUBLISHER"
                                                showEdit={true}
                                                onUpdate={(user) => updateUser({accessToken: sessionState.accessToken, user, projectsState})}
                                                showDelete={true}
                                                onDelete={(user) => deleteUser({accessToken: sessionState.accessToken, userId: user.id, projectsState})}
                                            />
                                        ))
                                    :
                                        <Typography variant="body1">Bitte füge alle geeigneten Verkündiger hinzu.</Typography>
                                    }
                                    <SimpleDialog
                                        title={`Projekt ${project.name} löschen`}
                                        text={`Soll das Projekt ${project.name} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
                                        cancelText="Nein"
                                        okText={`Ja, Projekt ${project.name} löschen`}
                                        onOK={() => {
                                                deleteProject({
                                                    accessToken: sessionState.accessToken,
                                                    projectsState: projectsState,
                                                    projectId: this.props.project.id
                                                });
                                            }}
                                    >
                                        <Button variant="contained" color="primary" className={classes.button}>
                                            Projekt löschen
                                            <DeleteIcon className={classes.rightIcon} />
                                        </Button>
                                    </SimpleDialog>
                                </>
                            )}
                        </ProjectsContext.Consumer>

                    )}
                </SessionContext.Consumer>
            </div>
        );
    }
}