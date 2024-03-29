import { Button, CircularProgress, TextField } from '@mui/material';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { EX_PROJECT_NAME_ALREADY_EXISTS } from '../../exceptions';
import { fullPathOfProjectSettings, fullPathOfProjectsSettings } from '../../paths';
import { PageContext } from '../../providers/page-provider';
import { ProjectsContext } from '../../providers/projects-provider';
import { convertToYYYYMMDD, requiresLogin } from '../../util';
import LenientRedirect from '../util/lenient-redirect';
import ProjectShiftEditComponent from './project-shifts-edit';



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
            if (error.response.key === EX_PROJECT_NAME_ALREADY_EXISTS) {
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
                size="small"
                disabled={saving}
                onChange={event => projectsState.changeProjectName(event.target.value)}
                sx={{ mb: 1, width: '228px' }}
            /><br />
            <Box sx={{
                display: 'flex',
                alignItems: 'baseline',
            }}>
                Von&nbsp;
                <TextField
                    type="date"
                    value={convertToYYYYMMDD(project.startDate) || ''}
                    variant="outlined"
                    size="small"
                    disabled={saving}
                    onChange={event => projectsState.changeProjectStartDate(event.target.value)}
                />
                &nbsp;bis&nbsp;
                <TextField
                    type="date"
                    value={convertToYYYYMMDD(project.endDate) || ''}
                    variant="outlined"
                    size="small"
                    disabled={saving}
                    onChange={event => projectsState.changeProjectEndDate(event.target.value)}
                />
            </Box>
            <br />
            <br />
            {project.shifts && (<ProjectShiftEditComponent disabled={saving} />)}
            {!saving ? (
                <>
                    <Button
                        sx={{ mr: 1 }}
                        onClick={() => this.save()}
                        variant="contained"
                        disabled={saveDisabled}
                        color="primary"
                    >
                        Speichern
                    </Button>
                    <Button
                        sx={{ mr: 1 }}
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