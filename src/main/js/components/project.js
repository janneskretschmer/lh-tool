import React from 'react';
import { Helmet } from 'react-helmet';
import ProjectsProvider, { ProjectsContext } from '../providers/projects-provider';

class ProjectComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return (
            <ProjectsProvider>
                <ProjectsContext.Consumer>
                    {projectsState => (
                        <>
                            <Helmet titleTemplate="Projekte - %s" />
                            <h2>Projekte</h2>
                            {projectsState.projects.length === 0 ? (
                                <div>Keine Projekte</div>
                            ) : (
                                    <ul>
                                        {projectsState.projects.map(project => (
                                            <li key={project.id}>
                                                {`Projekt: ${project.name}, ${project.startDate.format('DD.MM.YYYY')} bis ${project.endDate.format('DD.MM.YYYY')}`}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                        </>
                    )}
                </ProjectsContext.Consumer>
            </ProjectsProvider>
        );
    }
}

export default ProjectComponent;