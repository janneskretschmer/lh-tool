import React from 'react';
import moment from 'moment';
import DateSelect from './base/DateSelect';
import { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import { createNewProject } from '../actions/project';

export default class ProjectCreatePanel extends React.Component {

    constructor(props) {
        super(props);

        const now = moment();
        this.initials = {
            startDate: {
                year: moment(now).year(),
                month: moment(now).month(),
                date: moment(now).date()
            },
            endDate: {
                year: moment(now).year(),
                month: moment(now).month(),
                date: moment(now).date()
            },
        };

        this.state = {
            name: '',
            startDate: this.initials.startDate,
            endDate: this.initials.endDate,
        };
    }

    render() {
        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <ProjectsContext.Consumer>
                        {projectsState => (
                            <form onSubmit={evt => {
                                evt.preventDefault();
                                createNewProject({
                                    accessToken: sessionState.accessToken,
                                    projectsState,
                                    startMoment: moment(this.state.startDate),
                                    endMoment: moment(this.state.endDate),
                                    name: this.state.name
                                });
                                // TODO Cleanup input fields after submit
                            }}>
                                <input
                                    type="text"
                                    placeholder="Name"
                                    onChange={evt => this.setState({ name: evt.target.value })} />
                                <DateSelect
                                    defaultYear={this.initials.startDate.year}
                                    defaultMonth={this.initials.startDate.month}
                                    defaultDate={this.initials.startDate.date}
                                    onChange={newDate => this.setState({ startDate: newDate })} />
                                {'bis'}
                                <DateSelect
                                    defaultYear={this.initials.endDate.year}
                                    defaultMonth={this.initials.endDate.month}
                                    defaultDate={this.initials.endDate.date}
                                    onChange={newDate => this.setState({ endDate: newDate })} />
                                <button type="submit">Erzeugen</button>
                            </form>
                        )}
                    </ProjectsContext.Consumer>
                )}
            </SessionContext.Consumer>
        );
    }
}