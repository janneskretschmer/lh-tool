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

    render() {
        return (
            <ProjectsContext.Provider
                value={{
                    ...this.state,
                    projectAdded: this.projectAdded,
                    projectRemoved: this.projectRemoved,
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }
}