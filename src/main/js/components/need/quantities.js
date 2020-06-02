import { CircularProgress } from '@material-ui/core';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { ProjectsContext } from '../../providers/projects-provider';
import { requiresLogin, setWaitingState } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import NeedQuantityEditComponent from './quantity-edit';

const styles = theme => ({
    helperTypeName: {
        marginTop: '15px',
        marginBottom: '5px',
    },
    inputWrapper: {
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexWrap: 'wrap',
    },
});

@withStyles(styles)
@withSnackbar
class StatefulNeedQuantityComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedMonth: null,
            selectedProject: null,
        };
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Laden der Bedarfe', {
            variant: 'error',
        });
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        //new data needs to be loaded on every change of project or month
        if (nextProps.projectsState.selectedMonthCalendarData.monthOffset !== prevState.selectedMonth
            || nextProps.projectsState.selectedProjectIndex !== prevState.selectedProject) {
            const projectId = nextProps.projectsState.getSelectedProject().id;
            const days = nextProps.projectsState.selectedMonthCalendarData.days.filter(day => !day.disabled);
            nextProps.needsState.loadNeedsForCalendarBetweenDates(projectId,
                days[0].date,
                days[days.length - 1].date,
                error => nextProps.enqueueSnackbar('Fehler beim Laden der Bedarfe', {
                    variant: 'error',
                }));
            // nextProps.projectsState.selectedMonthCalendarData.days.filter(day => !day.disabled)
            //     .forEach(
            //         day => nextProps.needsState.loadHelperTypesWithNeedsByProjectIdAndDate(projectId, day.date, err => this.handleFailure())
            //     );
            return {
                selectedMonth: nextProps.projectsState.selectedMonthCalendarData.monthOffset,
                selectedProject: nextProps.projectsState.selectedProjectIndex,
            };
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
                <ProjectCalendar>
                    {dayMap && Array.from(dayMap.keys()).map(dateString => (
                        <div key={dateString} date={dayMap.get(dateString).date}>
                            {dayMap.get(dateString).helperTypes && dayMap.get(dateString).helperTypes
                                .map(
                                    helperType => (
                                        <div key={helperType.id}>
                                            <div className={classes.helperTypeName}>
                                                {helperType.name}
                                            </div>
                                            {helperType.shifts && helperType.shifts[0] && helperType.shifts[0].need ? helperType.shifts.map(shift => (
                                                <div key={shift.id} className={classes.inputWrapper}>
                                                    {shift.need ? (
                                                        <NeedQuantityEditComponent
                                                            projectHelperType={shift}
                                                            label={shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime} />
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

const NeedQuantityComponent = props => (
    <>
        <ProjectsContext.Consumer>
            {projectsState => (
                <NeedsContext.Consumer>
                    {needsState =>
                        (<StatefulNeedQuantityComponent {...props} needsState={needsState} projectsState={projectsState} />)
                    }
                </NeedsContext.Consumer>
            )}
        </ProjectsContext.Consumer>
    </>
);
export default requiresLogin(NeedQuantityComponent);
