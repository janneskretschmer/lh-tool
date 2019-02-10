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

    render() {
        return (
            <ProjectsContext.Provider
                value={{
                    ...this.state,
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }
}
