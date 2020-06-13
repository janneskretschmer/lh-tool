import { Typography } from '@material-ui/core';
import Collapse from '@material-ui/core/Collapse';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import { withStyles } from '@material-ui/core/styles';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import React from 'react';
import OldProjectsProvider, { OldProjectsContext } from '../../providers/projects-provider.old';
import { SessionContext } from '../../providers/session-provider';
import { requiresLogin, setWaitingState } from '../../util';
import WithPermission from '../with-permission';
import ProjectCreatePanel from './project-create';
import ProjectEditPanel from './project-edit.old';

const styles = theme => ({
    root: {
        width: '100%',
        backgroundColor: theme.palette.background.paper,
    },
});

const ProjectEntry = props => (
    <>
        <ListItem button onClick={() => props.onCollapseChange && props.onCollapseChange(!props.open, props.project)}>
            <ListItemIcon>
                <GroupWorkIcon />
            </ListItemIcon>
            <ListItemText
                inset
                primary={props.project.name}
                secondary={`${props.project.startDate.format('DD.MM.YYYY')} bis ${props.project.endDate.format('DD.MM.YYYY')}`} />
            {props.open ? <ExpandLess /> : <ExpandMore />}
        </ListItem>
        <Collapse in={props.open} timeout="auto" unmountOnExit>
            <ProjectEditPanel project={props.project} />
        </Collapse>
    </>
);

@withStyles(styles)
class StatefulProjectsComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            openProjectId: null,
        };
    }

    handleCollapseChange(openRequested, project) {
        this.setState({
            openProjectId: openRequested ? project.id : null,
        });
    }

    render() {
        const { classes } = this.props;

        const tmp = (
            <SessionContext.Consumer>
                {sessionState => (
                    <OldProjectsContext.Consumer>
                        {projectsState => (
                            <div className={classes.root}>
                                <WithPermission permission="ROLE_RIGHT_PROJECTS_POST">
                                    <List
                                        subheader={<ListSubheader component="div">Neues Projekt erstellen</ListSubheader>}
                                    >
                                        <ProjectCreatePanel />
                                    </List>
                                </WithPermission>
                                <List
                                    subheader={<ListSubheader component="div">Projekte</ListSubheader>}
                                >
                                    {projectsState.projects.length === 0 ? (
                                        <Typography>
                                            (Keine Projekte anzuzeigen)
                                        </Typography>
                                    ) : projectsState.projects.map(project => (
                                        <ProjectEntry
                                            key={project.id}
                                            project={project}
                                            open={project.id === this.state.openProjectId || projectsState.projects.length === 1}
                                            onCollapseChange={this.handleCollapseChange.bind(this)}
                                        />
                                    ))}
                                </List>
                            </div>
                        )}
                    </OldProjectsContext.Consumer>

                )}
            </SessionContext.Consumer>
        );
        setWaitingState(false);
        return tmp;
    }
}

const ProjectsComponent = props => (
    <>
        <OldProjectsProvider>
            <StatefulProjectsComponent {...props} />
        </OldProjectsProvider>
    </>
);
export default requiresLogin(ProjectsComponent);
