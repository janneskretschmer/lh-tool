import React from 'react';
import { Helmet } from 'react-helmet';
import ProjectsProvider, { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import ProjectCreatePanel from './project-create';
import { deleteProject } from '../actions/project';

export default class ProjectComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <ProjectsProvider>
                        <ProjectsContext.Consumer>
                            {projectsState => (
                                <>
                                    <Helmet titleTemplate="Projekte - %s" />
                                    <h2>Projekte</h2>
                                    <div>
                                        <h3>Neues Projekt hinzufügen</h3>
                                        <ProjectCreatePanel />
                                    </div>
                                    {projectsState.projects.length === 0 ? (
                                        <div>Keine Projekte</div>
                                    ) : (
                                            <ul>
                                                {projectsState.projects.map(project => (
                                                    <li key={project.id}>
                                                        {`Projekt: ${project.name}, ${project.startDate.format('DD.MM.YYYY')} bis ${project.endDate.format('DD.MM.YYYY')}`}
                                                        <button onClick={evt => {
                                                            deleteProject({
                                                                accessToken: sessionState.accessToken,
                                                                projectsState: projectsState,
                                                                projectId: project.id
                                                            })
                                                        }}>Löschen</button>
                                                    </li>
                                                ))}
                                            </ul>
                                        )}
                                </>
                            )}
                        </ProjectsContext.Consumer>
                    </ProjectsProvider>
                )}
            </SessionContext.Consumer>
        );
    }
}
