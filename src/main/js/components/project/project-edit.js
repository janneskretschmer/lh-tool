import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { Button, IconButton, TextField, Dialog, DialogTitle, DialogContent, FormControl, InputLabel, Select, MenuItem, DialogActions, CircularProgress } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';
import ProjectsProvider, { ProjectsContext } from '../../providers/projects-provider';
import { requiresLogin, convertToMUIFormat } from '../../util';
import ProjectShiftEditComponent from './project-shifts-edit';
import { withSnackbar } from 'notistack';
import { Redirect } from 'react-router';
import { fullPathOfProjectSettings } from '../../paths';


const styles = theme => ({
    dateContainer: {
        display: 'flex',
        alignItems: 'baseline',
    },
    button: {
        marginRight: theme.spacing.unit,
    }
});

@withStyles(styles)
@withSnackbar
class StatefulProjectEditComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            saving: false,
            redirectToProject: null,
        };
    }

    componentDidMount() {
        this.props.projectsState.selectProject(this.props.match.params.projectId,
            error => this.handleFailure(error));
    }

    componentDidUpdate() {
        const projectId = this.props.projectsState.selectedProject.id;
        if (!this.state.redirectToProject && projectId && parseInt(this.props.match.params.projectId) !== projectId) {
            this.setState({ redirectToProject: projectId })
        }
    }

    handleFailure(error) {
        let message;
        if (error && error.response && error.response.key) {
            if (error.response.key === 'EX_PROJECT_NAME_ALREADY_EXISTS') {
                message = 'Dieser Projektname wird bereits verwendet.';
            }
        } else {
            message = 'Fehler beim Aktualisieren der Projektdaten';
        }
        this.props.enqueueSnackbar(message, {
            variant: 'error',
        });
    }

    save() {
        this.setState({ saving: true });
        this.props.projectsState.saveSelectedProject()
            .then(() => this.props.enqueueSnackbar('Projekt gespeichert', { variant: 'success', }))
            .catch(error => this.handleFailure(error)).finally(() => this.setState({ saving: false }));
    }

    render() {
        const { classes, projectsState } = this.props;
        const { saving, redirectToProject } = this.state;
        const project = projectsState.selectedProject;
        const saveDisabled = !projectsState.isProjectValid();

        if (redirectToProject) {
            return (<Redirect to={fullPathOfProjectSettings(redirectToProject)} />);
        }

        if (!project) {
            return (<CircularProgress />);
        }

        return (<>
            <TextField
                label="Name"
                value={project.name}
                variant="outlined"
                margin="dense"
                disabled={saving}
                onChange={event => projectsState.changeProjectName(event.target.value)}
            /><br />
            <div className={classes.dateContainer}>
                Von&nbsp;
                <TextField
                    type="date"
                    value={convertToMUIFormat(project.startDate) || ""}
                    variant="outlined"
                    margin="dense"
                    disabled={saving}
                    onChange={event => projectsState.changeProjectStartDate(event.target.value)}
                />
                &nbsp;bis&nbsp;
                <TextField
                    type="date"
                    value={convertToMUIFormat(project.endDate) || ""}
                    variant="outlined"
                    margin="dense"
                    disabled={saving}
                    onChange={event => projectsState.changeProjectEndDate(event.target.value)}
                />
            </div>
            <br />
            <br />
            {project.shifts && (<ProjectShiftEditComponent disabled={saving} />)}
            {!saving ? (
                <>
                    <Button
                        className={classes.button}
                        onClick={() => this.save()}
                        variant="contained"
                        disabled={saveDisabled}
                        color="primary"
                    >
                        Speichern
                    </Button>
                    <Button
                        className={classes.button}
                        onClick={() => projectsState.resetSelectedProject()}
                        variant="outlined"
                    >
                        Abbrechen
                    </Button>
                </>
            ) : (<CircularProgress />)}
        </>);
    }
}

const ProjectEditComponent = props => (
    <ProjectsContext.Consumer>
        {projectsState => (<StatefulProjectEditComponent {...props} projectsState={projectsState} />)}
    </ProjectsContext.Consumer>
);
export default requiresLogin(ProjectEditComponent);