import React from 'react';
import { resolve } from 'react-resolver';
import { SessionContext } from './session-provider';
import { fetchOwnProjects, fetchProjects } from '../actions/project';
import { withContext, getMonthOffsetWithinRange, getProjectMonth, isMonthOffsetWithinRange } from '../util';

export const ProjectsContext = React.createContext();

@withContext('sessionState', SessionContext)
@resolve('initialProjectData', props => {
    return fetchProjects(props.sessionState.accessToken);
})
export default class ProjectsProvider extends React.Component {
    constructor(props) {
        super(props);
        const projects = props.initialProjectData;
        const selectedProjectIndex = 0;
        const selectedMonthCalendarData = getProjectMonth(
            getMonthOffsetWithinRange(0, projects[selectedProjectIndex].startDate, projects[selectedProjectIndex].endDate),
            projects[selectedProjectIndex].startDate, projects[selectedProjectIndex].endDate);

        this.state = {
            projects,
            selectedProjectIndex,
            selectedMonthCalendarData,
        };
    }

    projectAdded = project => {
        this.setState(prevState => ({
            projects: prevState.projects.concat([project]),
        }));
    };

    projectRemoved = projectId => {
        this.setState(prevState => ({
            projects: prevState.projects.filter(project => project.id != projectId)
        }));
    };

    userAdded = (projectId, user, role) => {
        this.setState(prevState => ({
            projects: prevState.projects.map(project => {
                if (project.id === projectId) {
                    if (role === 'ROLE_LOCAL_COORDINATOR') {
                        project.localCoordinators = project.localCoordinators.concat([user]);
                        project.localCoordinators.sort((a, b) => {
                            if (a.lastName < b.lastName) {
                                return -1;
                            } else if (a.lastName > b.lastName) {
                                return 1;
                            } else {
                                return a.firstName < b.firstName ? -1 : 1;
                            }
                        });
                    } else if (role === 'ROLE_PUBLISHER') {
                        project.publishers = project.publishers.concat([user]);
                        project.publishers.sort((a, b) => {
                            if (a.lastName < b.lastName) {
                                return -1;
                            } else if (a.lastName > b.lastName) {
                                return 1;
                            } else {
                                return a.firstName < b.firstName ? -1 : 1;
                            }
                        });
                    }
                }
                return project;
            })
        }));
    };

    userUpdated = (user) => {
        this.setState(prevState => ({
            projects: prevState.projects.map(project => {
                if (project.localCoordinators) {
                    project.localCoordinators = project.localCoordinators.map(tmpUser => tmpUser.id === user.id ? user : tmpUser);
                }
                if (project.publishers) {
                    project.publishers = project.publishers.map(tmpUser => tmpUser.id === user.id ? user : tmpUser);
                }
                return project;
            })
        }));
    }

    userRemoved = (userId) => {
        this.setState(prevState => ({
            projects: prevState.projects.map(project => {
                if (project.localCoordinators) {
                    project.localCoordinators = project.localCoordinators.filter(user => user.id !== userId);
                }
                if (project.publishers) {
                    project.publishers = project.publishers.filter(user => user.id !== userId);
                }
                return project;
            })
        }));
    }

    selectProject(index) {
        const selectedProjectIndex = Math.min(Math.max(index, 0), this.state.projects.length);
        const project = this.state.projects[selectedProjectIndex];
        const selectedMonthCalendarData = getProjectMonth(
            getMonthOffsetWithinRange(0, project.startDate, project.endDate),
            project.startDate, project.endDate);

        this.setState(prevState => ({
            selectedProjectIndex,
            selectedMonthCalendarData,
        }));
    }

    getSelectedProject() {
        return this.state.projects[this.state.selectedProjectIndex];
    }

    setMonth(monthOffset) {
        const project = this.getSelectedProject();
        if (isMonthOffsetWithinRange(monthOffset, project.startDate, project.endDate)) {
            const selectedMonthCalendarData = getProjectMonth(monthOffset, project.startDate, project.endDate);
            this.setState({
                selectedMonthCalendarData,
            });
        }
    }



    render() {
        return (
            <ProjectsContext.Provider
                value={{
                    ...this.state,
                    projectAdded: this.projectAdded,
                    projectRemoved: this.projectRemoved,
                    userAdded: this.userAdded,
                    userRemoved: this.userRemoved,
                    userUpdated: this.userUpdated,
                    selectProject: this.selectProject.bind(this),
                    getSelectedProject: this.getSelectedProject.bind(this),
                    setMonth: this.setMonth.bind(this),
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }
}
