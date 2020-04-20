import React from 'react';
import CircularProgress from '@material-ui/core/CircularProgress';
import { fetchOwnProjects } from '../../actions/project';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import { withContext } from '../../util';
import { ProjectsContext } from '../../providers/projects-provider';

class StatefulProjectSelection extends React.Component {

  handleChange(event) {
    this.props.projectsState.selectProject(event.target.value);
  }

  render() {
    return (
      <>
        Projekt: {
          this.props.projectsState.projects.length > 0 ? (
            <Select
              value={this.props.projectsState.selectedProjectIndex}
              onChange={event => this.handleChange(event)}
            >
              {this.props.projectsState.projects.map((project, i) => (
                <MenuItem key={project.id} value={i}>{project.name}</MenuItem>
              ))}
            </Select>
          ) : (
              <CircularProgress size={15} />
            )
        }
      </>
    );
  }
}

const ProjectSelection = props => (
  <>
    <ProjectsContext.Consumer>
      {projectsState => (
        (<StatefulProjectSelection {...props} projectsState={projectsState} />)
      )}
    </ProjectsContext.Consumer>
  </>
);
export default ProjectSelection;
