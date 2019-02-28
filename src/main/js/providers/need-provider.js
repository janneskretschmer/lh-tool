import React from 'react';
import { resolve } from 'react-resolver';
import { SessionContext } from './session-provider';
import { fetchOwnNeeds } from '../actions/need';
import { withContext } from '../util';

export const ProjectsContext = React.createContext();

@withContext('sessionState', SessionContext)
@resolve('initialNeedData', props => {
    return fetchOwnNeeds({ accessToken: props.sessionState.accessToken })
        .catch(() => []);
})
export default class ProjectsProvider extends React.Component {

    state = {
        projects: this.props.initialNeedData,
    };

    needsAdded = need => {
        this.setState(prevState => ({
            projects: prevState.needs.concat([need]),
        }));
    };

    needsUpdated = need => {
        /*this.setState(prevState => ({
            projects: prevState.projects.filter(project => project.id != projectId)
        }));*/
    };

    render() {
        return (
            <NeedsContext.Provider
                value={{
                    ...this.state,
                    needsAdded: this.needsAdded,
                    needsUpdated: this.needsUpdated,
                }}
            >
                {this.props.children}
            </NeedsContext.Provider>
        );
    }
}
