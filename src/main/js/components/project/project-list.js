import { CircularProgress, withStyles } from '@material-ui/core';
import { withSnackbar } from 'notistack';
import React from 'react';
import { fullPathOfProjectSettings } from '../../paths';
import { ProjectsContext } from '../../providers/projects-provider';
import { convertToDDMMYYYY, convertToDDMMYYYY_HHMM, requiresLogin } from '../../util';
import PagedTable from '../table';

const styles = theme => ({
    button: {
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    select: {
        minWidth: '100px',
        margin: theme.spacing.unit,
    }
});

@withStyles(styles)
@withSnackbar
export class StatefulProjectListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentDidMount() {
        this.props.projectsState.loadEditableProjects();
    }

    render() {
        const { projectsState, classes } = this.props;
        const { } = this.state;
        const projects = projectsState.projects && [...projectsState.projects.values()];
        return projects ? (<>
            <PagedTable
                title="Projekte"
                headers={[
                    {
                        key: 'name',
                        name: 'Projektname',
                    },
                    {
                        key: 'startDate',
                        name: 'Startdatum',
                        converter: date => convertToDDMMYYYY(date),
                    },
                    {
                        key: 'endDate',
                        name: 'Enddatum',
                        converter: date => convertToDDMMYYYY(date),
                    },
                ]}
                fitWidth={true}
                rows={projects}
                redirect={fullPathOfProjectSettings}
                showAddButton={projectsState.isAllowedToCreate()} />
        </>) : (<CircularProgress />)
    }

}

const ProjectListComponent = props => (
    <>
        <ProjectsContext.Consumer>
            {projectsState =>
                (<StatefulProjectListComponent {...props} projectsState={projectsState} />)
            }
        </ProjectsContext.Consumer>
    </>
);
export default requiresLogin(ProjectListComponent);