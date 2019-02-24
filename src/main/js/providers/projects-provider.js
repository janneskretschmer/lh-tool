import React from 'react';
import { resolve } from 'react-resolver';
import { SessionContext } from './session-provider';
import { fetchOwnProjects } from '../actions/project';
import { withContext } from '../util';

export const ProjectsContext = React.createContext();

@withContext('sessionState', SessionContext)
@resolve('initialProjectData', props => {
    return fetchOwnProjects({ accessToken: props.sessionState.accessToken })
        .catch(() => []);
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

    userChanged = (projectId, user, role) => {
        this.setState(prevState => ({
            projects: prevState.projects.map(project => {
                if(project.id === projectId){
                    if (role === 'ROLE_LOCAL_COORDINATOR') {
                        project.localCoordinator = user;
                    }
                }
                return project;
            })
        }));
    };

    userRemoved = (userId) => {
        this.setState(prevState => ({
            //TODO: rerender of UserComponenet isn't triggered
            projects: prevState.projects.map(project => {
                if (project.localCoordinator && project.localCoordinator.id === userId) {
                    project.localCoordinator = undefined;
                }
                if(project.publishers){
                    project.publishers = project.publishers.filter(user => user.id !== userId);
                }
                return project;
            })
        }), () => {
            // Die Holzhammer-Lösung, sollte aber optimaler Weise auch ohne gehen.
            this.forceUpdate();
        });
    } 

    render() {
        return (
            <ProjectsContext.Provider
                value={{
                    ...this.state,
                    projectAdded: this.projectAdded,
                    projectRemoved: this.projectRemoved,
                    userChanged: this.userChanged,
                    userRemoved: this.userRemoved,
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }
}
