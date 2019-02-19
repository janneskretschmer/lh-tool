import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Slide from '@material-ui/core/Slide';
import DeleteIcon from '@material-ui/icons/Delete';
import { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import { deleteProject } from '../actions/project';
import WithPermission from './with-permission';

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

const Transition = props => (<Slide direction="up" {...props} />);

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
                                <WithPermission permission="ROLE_RIGHT_PROJECTS_DELETE">
                                    <Button variant="contained" color="primary" className={classes.button} onClick={this.handleDeleteButtonClicked.bind(this)}>
                                        Projekt löschen
                                        <DeleteIcon className={classes.rightIcon} />
                                    </Button>
                                    <Dialog
                                        open={this.state.deleteDeleteDialogOpen}
                                        TransitionComponent={Transition}
                                        keepMounted
                                        onClose={this.handleDeleteDialogClose.bind(this)}
                                        aria-labelledby="alert-dialog-slide-title"
                                        aria-describedby="alert-dialog-slide-description"
                                    >
                                        <DialogTitle id="alert-dialog-slide-title">
                                            {`Projekt ${project.name} löschen`}
                                        </DialogTitle>
                                        <DialogContent>
                                            <DialogContentText id="alert-dialog-slide-description">
                                                {`Soll das Projekt ${project.name} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
                                            </DialogContentText>
                                        </DialogContent>
                                        <DialogActions>
                                            <Button onClick={this.handleDeleteDialogClose.bind(this)} color="secondary">
                                                {'Nein'}
                                            </Button>
                                            <Button color="primary" onClick={() => {
                                                deleteProject({
                                                    accessToken: sessionState.accessToken,
                                                    projectsState: projectsState,
                                                    projectId: this.props.project.id
                                                });
                                            }}>
                                                {`Ja, Projekt ${project.name} löschen`}
                                            </Button>
                                        </DialogActions>
                                    </Dialog>
                                </WithPermission>
                            )}
                        </ProjectsContext.Consumer>

                    )}
                </SessionContext.Consumer>
            </div>
        );
    }
}