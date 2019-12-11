import React from 'react';
import { resolve } from 'react-resolver';
import { SessionContext } from './session-provider';
import { fetchOwnProjects } from '../actions/project';
import { withContext } from '../util';

export const ProjectsContext = React.createContext();

@withContext('sessionState', SessionContext)
@resolve('initialProjectData', props => {
    return fetchOwnProjects({ accessToken: props.sessionState.accessToken });
})
export default class ProjectsProvider extends React.Component {

    state = {
        projects: this.props.initialProjectData,
    };

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

    userUpdated  = (user) => {
        this.setState(prevState => ({
            projects: prevState.projects.map(project => {
                if(project.localCoordinators){
                    project.localCoordinators = project.localCoordinators.map(tmpUser => tmpUser.id === user.id ? user : tmpUser);
                }
                if(project.publishers){
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
                if(project.publishers){
                    project.publishers = project.publishers.filter(user => user.id !== userId);
                }
                return project;
            })
        }));
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
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }
}
