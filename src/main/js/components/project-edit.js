import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import { deleteProject } from '../actions/project';
import Typography from '@material-ui/core/Typography';
import UserComponent from './user-detail';
import { createNewUser, deleteUser } from '../actions/user'
import SimpleDialog from './simple-dialog.js'
import Button from '@material-ui/core/Button';

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
            deleteDeleteDialogOpen: false,
        };
    }

    handleDeleteButtonClicked() {
        this.setState({ deleteDeleteDialogOpen: true });
    }

    handleDeleteDialogClose() {
        this.setState({ deleteDeleteDialogOpen: false });
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
                                    <UserComponent user={project.localCoordinator}
                                        role="ROLE_LOCAL_COORDINATOR"
                                        showEdit={true}
                                        saveHandler={(user) => createNewUser({ accessToken: sessionState.accessToken, ...user, projectId:project.id, projectsState })}
                                        showDelete={true}
                                        deleteHandler={(user) => deleteUser({accessToken: sessionState.accessToken, userId: user.id, projectsState})}
                                    />
                                    <div><Typography variant="h6">Verkündiger</Typography></div>
                                    <SimpleDialog
                                        title= {`Projekt ${project.name} löschen`}
                                        text= {`Soll das Projekt ${project.name} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
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