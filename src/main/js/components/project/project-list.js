import React from 'react';
import { UsersContext } from '../../providers/users-provider';
import { requiresLogin, getRoleName, convertToReadableFormat } from '../../util';
import { CircularProgress, Button, TextField, IconButton, InputAdornment, withStyles, FormControl, InputLabel, Select, MenuItem } from '@material-ui/core';
import { Redirect } from 'react-router';
import { fullPathOfUserSettings, fullPathOfProjectSettings } from '../../paths';
import PagedTable from '../table';
import { withSnackbar } from 'notistack';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import SearchIcon from '@material-ui/icons/Search';
import WithPermission from '../with-permission';
import { ProjectsContext } from '../../providers/projects-provider';

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
                        converter: moment => convertToReadableFormat(moment),
                    },
                    {
                        key: 'endDate',
                        name: 'Enddatum',
                        converter: moment => convertToReadableFormat(moment),
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