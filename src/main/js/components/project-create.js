import React from 'react';
import moment from 'moment';
import { withSnackbar } from 'notistack';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import { withStyles } from '@material-ui/core/styles';
import { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import { createNewProject } from '../actions/project';

const MUI_DATE_FORMAT = 'YYYY-MM-DD';

const styles = theme => ({
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        width: 200,
    },
    button: {
        margin: theme.spacing.unit,
    },
});

@withSnackbar
@withStyles(styles)
export default class ProjectCreatePanel extends React.Component {

    constructor(props) {
        super(props);

        this.defaultStartDate = moment().format(MUI_DATE_FORMAT);
        this.defaultEndDate = moment().add(1, 'day').format(MUI_DATE_FORMAT);

        this.state = {
            name: '',
            startDate: this.defaultStartDate,
            endDate: this.defaultEndDate,
        };
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Erstellen des neuen Projekts', {
            variant: 'error',
        });
    }

    render() {
        const { classes } = this.props;
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
                                    startMoment: moment(this.state.startDate, MUI_DATE_FORMAT),
                                    endMoment: moment(this.state.endDate, MUI_DATE_FORMAT),
                                    name: this.state.name,
                                    handleFailure: this.handleFailure.bind(this),
                                });
                            }}>
                                <TextField
                                    label="Name"
                                    className={classes.textField}
                                    value={this.state.name}
                                    onChange={evt => this.setState({ name: evt.target.value })}
                                    margin="normal"
                                />
                                <TextField
                                    label="Beginn des Projekts"
                                    type="date"
                                    defaultValue={this.defaultStartDate}
                                    className={classes.textField}
                                    margin="normal"
                                    onChange={evt => this.setState({ startDate: evt.target.value })}
                                />
                                <TextField
                                    label="Ende des Projekts"
                                    type="date"
                                    defaultValue={this.defaultEndDate}
                                    className={classes.textField}
                                    margin="normal"
                                    onChange={evt => this.setState({ endDate: evt.target.value })}
                                />
                                <br />
                                <Button variant="contained" className={classes.button} type="submit">
                                    Erzeugen
                                </Button>
                            </form>
                        )}
                    </ProjectsContext.Consumer>
                )}
            </SessionContext.Consumer>
        );
    }
}