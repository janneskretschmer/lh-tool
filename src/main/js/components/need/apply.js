import Button from '@material-ui/core/Button';
import { green, red, yellow, grey } from '@material-ui/core/colors';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Helmet } from 'react-helmet';
import { fetchOwnNeeds } from '../../actions/need';
import { NeedsContext } from '../../providers/needs-provider';
import { ProjectsContext } from '../../providers/projects-provider';
import { requiresLogin, setWaitingState, withContext } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import NeedApplyEditComponent from './apply-edit';
import { CircularProgress } from '@material-ui/core';

const styles = theme => ({
    helperTypeName: {
        marginTop: '10px',
    },
    applyWrapper: {
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexWrap: 'wrap',
    },
    legendItem: {
        alignItems: 'center',
        margin: theme.spacing.unit,
    },
    buttonText: {
        color: grey[700],
    },
    red: {
        color: '#f00',
    },
    applied: {
        backgroundColor: yellow[600],
    },
    approved: {
        backgroundColor: green[600],
    },
    rejected: {
        backgroundColor: red[600],
    },
});

@withStyles(styles)
@withSnackbar
class StatefulNeedApplyComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedMonth: null,
            selectedProject: null,
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        //new data needs to be loaded on every change of project or month
        if (nextProps.projectsState.selectedMonthCalendarData.monthOffset !== prevState.selectedMonth
            || nextProps.projectsState.selectedProjectIndex !== prevState.selectedProject) {
            const projectId = nextProps.projectsState.getSelectedProject().id;
            const userId = nextProps.sessionState.currentUser.id;
            nextProps.projectsState.selectedMonthCalendarData.days.filter(day => !day.disabled)
                .forEach(
                    day => nextProps.needsState.loadHelperTypesWithNeedsAndCurrentUserByProjectIdAndDate(projectId, day.date, err => console.log(err))
                );
            return {
                selectedMonth: nextProps.projectsState.selectedMonthCalendarData.monthOffset,
                selectedProject: nextProps.projectsState.selectedProjectIndex,
            }
        }
        return null;
    }

    render() {
        const { classes, needsState } = this.props;
        const projectId = this.props.projectsState.getSelectedProject().id;
        const dayMap = needsState.projects && needsState.projects.has(projectId) && needsState.projects.get(projectId).days;
        setWaitingState(false);
        return (
            <>
                <Helmet titleTemplate="%s › Bewerben" />
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="outlined" disabled={true}>Zeitraum Bewerberanzahl/Bedarf</Button> &nbsp;Es besteht kein Bedarf an diesem Tag.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="outlined"><span className={classes.buttonText}>Zeitraum</span>&nbsp;&nbsp;Bewerberanzahl/Bedarf</Button> &nbsp;Nicht beworben. Bitte klicke auf den jeweiligen Button, um dich zu bewerben.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.applied}><span className={classes.buttonText}>Zeitraum</span>&nbsp;&nbsp; Bewerberanzahl/Bedarf</Button> &nbsp;Beworben, jedoch noch nicht zugeteilt. Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.approved}><span className={classes.buttonText}>Zeitraum</span>&nbsp;&nbsp; Bewerberanzahl/Bedarf</Button> &nbsp;Zugeteilt,<span className={classes.red}> komme bitte nur dann zur Baustelle</span>, vielen Dank! Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.rejected}>Zeitraum</Button> &nbsp;Nicht zugeteilt, bitte bewerbe dich für ein anderes Datum.
                </div>
                <br />
                <ProjectCalendar>
                    {dayMap && Array.from(dayMap.keys()).map(dateString => (
                        <div key={dateString} date={dayMap.get(dateString).date}>
                            {dayMap.get(dateString).helperTypes && dayMap.get(dateString).helperTypes
                                .map(
                                    helperType => helperType && (
                                        <div key={helperType.id}>
                                            <div className={classes.helperTypeName}>
                                                {helperType.name}
                                            </div>
                                            {helperType.shifts && helperType.shifts[0] && helperType.shifts[0].need ? helperType.shifts.map(shift => (
                                                <div key={shift.id} className={classes.inputWrapper}>
                                                    {shift.need && (shift.need.state || !shift.need.id) ? (
                                                        <NeedApplyEditComponent
                                                            need={shift.need}
                                                            helperTypeName={helperType.name}
                                                            projectHelperType={shift}
                                                            label={shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime}></NeedApplyEditComponent>
                                                    ) : (<CircularProgress size={15} />)}
                                                </div>
                                            )) : (<CircularProgress size={15} />)}
                                        </div>
                                    )
                                )}
                        </div>
                    ))}
                </ProjectCalendar>
            </>
        );
    }
}

const NeedApplyComponent = props => (
    <>
        <ProjectsContext.Consumer>
            {projectsState => (
                <NeedsContext.Consumer>
                    {needsState =>
                        (<StatefulNeedApplyComponent {...props} needsState={needsState} projectsState={projectsState} />)
                    }
                </NeedsContext.Consumer>
            )}
        </ProjectsContext.Consumer>
    </>
);
export default requiresLogin(NeedApplyComponent);


