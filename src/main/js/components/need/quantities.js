import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Helmet } from 'react-helmet';
import { fetchOwnNeeds } from '../../actions/need';
import { requiresLogin, setWaitingState, convertToReadableFormatWithoutYear } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import NeedQuantityEditComponent from './quantity-edit';
import NeedsProvider, { NeedsContext } from '../../providers/needs-provider';
import ProjectsProvider, { ProjectsContext } from '../../providers/projects-provider';
import { CircularProgress } from '@material-ui/core';

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
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        if (nextProps.projectsState.selectedMonthCalendarData.monthOffset !== prevState.selectedMonth
            || nextProps.projectsState.selectedProjectIndex !== prevState.selectedProject) {
            const projectId = nextProps.projectsState.getSelectedProject().id;
            nextProps.projectsState.selectedMonthCalendarData.days.filter(day => !day.disabled)
                .forEach(
                    day => nextProps.needsState.loadHelperTypesWithNeedsByProjectIdAndDate(projectId, day.date, err => console.log(err))
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
                <Helmet titleTemplate="%s › Bedarf" />
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
                                                            label={shift.startTime + ' - ' + shift.endTime} />
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
        <ProjectsProvider>
            <NeedsProvider>
                <ProjectsContext.Consumer>
                    {projectsState => (
                        <NeedsContext.Consumer>
                            {needsState =>
                                (<StatefulNeedQuantityComponent {...props} needsState={needsState} projectsState={projectsState} />)
                            }
                        </NeedsContext.Consumer>
                    )}
                </ProjectsContext.Consumer>
            </NeedsProvider>
        </ProjectsProvider>
    </>
);
export default requiresLogin(NeedQuantityComponent);
