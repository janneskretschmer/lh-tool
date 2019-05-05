import React from 'react';
import CircularProgress from '@material-ui/core/CircularProgress';
import { fetchOwnProjects } from '../actions/project';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';

class ProjectSelection extends React.Component {

  constructor(props) {
        super(props);
        this.state = {
            projects: [],
            selected: 0,
        };
    }

  setProjects(projects) {
    this.setState({
      ...this.state,
      projects,
    })
    if (this.props.onChange) {
      this.props.onChange(projects[this.state.selected])
    }
  }

  handleChange(event) {
    if (this.props.onChange) {
      this.props.onChange(this.state.projects[event.target.value])
    }
    this.setState({
      ...this.state,
      selected: event.target.value,
    })
  }

  componentDidMount() {
    const self = this;
    fetchOwnProjects({ accessToken:this.props.accessToken }).then(result => self.setProjects(result))
  }

  render() {
    return (
      <>
        Projekt: {
          this.state.projects.length > 0 ? (
                <Select
                    value={this.state.selected}
                    onChange={this.handleChange.bind(this)}
                 >
              {this.state.projects.map((project,i) => (
                      <MenuItem key={project.id} value={i}>{project.name}</MenuItem>
              ))}
            </Select>
          ):(
            <CircularProgress size={15}/>
          )
        }
      </>
    )
  }
}

export default ProjectSelection
