import { Button, CircularProgress, TextField } from '@material-ui/core';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { fullPathOfProjectSettings, fullPathOfProjectsSettings } from '../../paths';
import { ProjectsContext } from '../../providers/projects-provider';
import { convertToMUIFormat, requiresLogin } from '../../util';
import LenientRedirect from '../util/lenient-redirect';
import ProjectShiftEditComponent from './project-shifts-edit';
import { PageContext } from '../../providers/page-provider';


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
            redirectToUrl: null,
        };
    }

    componentDidMount() {
        this.props.projectsState.selectProject(this.props.match.params.projectId,
            error => this.handleFailure(error));
    }

    componentDidUpdate() {
        const project = this.props.projectsState.selectedProject;
        if (!this.state.redirectToUrl && project && project.id && parseInt(this.props.match.params.projectId, 10) !== project.id) {
            this.setState({ redirectToUrl: fullPathOfProjectSettings(project.id) });
        }
        if (project && this.props.pageState.currentItemName !== project.name) {
            this.props.pageState.setCurrentItemName(project);
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
            .then(() => this.setState({ redirectToUrl: fullPathOfProjectsSettings() }))
            .catch(error => this.handleFailure(error)).finally(() => this.setState({ saving: false }));
    }

    cancel() {
        this.props.projectsState.resetSelectedProject();
        this.setState({ redirectToUrl: fullPathOfProjectsSettings() });
    }

    render() {
        const { classes, projectsState } = this.props;
        const { saving, redirectToUrl } = this.state;
        const project = projectsState.selectedProject;
        const saveDisabled = !projectsState.isProjectValid();

        if (redirectToUrl) {
            return (<LenientRedirect to={redirectToUrl} />);
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
                    value={convertToMUIFormat(project.startDate) || ''}
                    variant="outlined"
                    margin="dense"
                    disabled={saving}
                    onChange={event => projectsState.changeProjectStartDate(event.target.value)}
                />
                &nbsp;bis&nbsp;
                <TextField
                    type="date"
                    value={convertToMUIFormat(project.endDate) || ''}
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
                        onClick={() => this.cancel()}
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
        {projectsState => (
            <PageContext.Consumer>
                {pageState => (
                    <StatefulProjectEditComponent {...props} projectsState={projectsState} pageState={pageState} />
                )}
            </PageContext.Consumer>
        )}
    </ProjectsContext.Consumer>
);
export default requiresLogin(ProjectEditComponent);