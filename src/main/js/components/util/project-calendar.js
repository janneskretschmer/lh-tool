import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import classNames from 'classnames';
import ProjectSelection from './project-selection';
import { requiresLogin, getProjectMonth, getMonthOffsetWithinRange, isMonthOffsetWithinRange, getMonthNameForOffset, withContext } from '../../util';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import IconButton from '@material-ui/core/IconButton';
import CircularProgress from '@material-ui/core/CircularProgress';
import { OldProjectsContext } from '../../providers/projects-provider.old';
import MonthSelection from './month-selection';

const styles = theme => ({
    calendar: {
        tableLayout: 'fixed',
        width: '100%',
        borderCollapse: 'collapse',
        minWidth: '920px',
    },
    calendarRow: {

    },
    calendarCell: {
        width: '20%',
        border: '1px solid ' + theme.palette.primary.light,
        padding: theme.spacing.unit,
        verticalAlign: 'top',
    },
    disabled: {
        opacity: '0.38',
    },
    dayName: {
        padding: theme.spacing.unit,
        color: theme.palette.secondary.dark,
        border: '1px solid ' + theme.palette.primary.light,
        fontWeight: 'normal',
        fontSize: 'large',
    },
    day: {
        width: '100%',
        textAlign: 'right',
        color: theme.palette.secondary.main,
        fontWeight: 'bold',
        fontSize: 'larger',
        padding: '3px',
    },

    header: {
        display: 'flex',
        position: 'relative',
        height: '48px',
        alignItems: 'center',
    },
    projectWrapper: {
        justifySelf: 'start',
    },
    month: {
        justifySelf: 'center',
        position: 'absolute',
        left: '50%',
        transform: 'translate(-50%,0)',
        alignItems: 'center',
        display: 'flex',
        fontSize: 'x-large',
    },
});

@withStyles(styles)
class StatefulProjectCalendar extends React.Component {

    setMonth(month) {
        this.props.projectsState.setMonth(month);
    }


    render() {
        const { classes, sessionState } = this.props;
        const data = this.props.projectsState.selectedMonthCalendarData;
        const project = this.props.projectsState.getSelectedProject();

        return (
            <>
                <div className={classes.header}>
                    <div className={classes.projectWrapper}>
                        <ProjectSelection />
                    </div>
                    {data ? (
                        <MonthSelection className={classes.month} />
                    ) : (
                            <CircularProgress size={15} />
                        )}

                </div>
                {data && (
                    <table className={classes.calendar}>
                        <thead>
                            <tr>
                                <th className={classes.dayName}>Montag</th>
                                <th className={classes.dayName}>Dienstag</th>
                                <th className={classes.dayName}>Mittwoch</th>
                                <th className={classes.dayName}>Donnerstag</th>
                                <th className={classes.dayName}>Freitag</th>
                                <th className={classes.dayName}>Samstag</th>
                                <th className={classes.dayName}>Sonntag</th>
                            </tr>
                        </thead>
                        <tbody>
                            {Array.from(Array(data.days.length / 7)).map((_, i) => (
                                <tr className={classes.calendarRow} key={i}>
                                    {Array.from(Array(7)).map((_, j) => {
                                        const day = data.days[i * 7 + j];
                                        const contentWrapper = this.props.children && this.props.children.find(child => child.props.date.isSame(day.date, 'day'));
                                        const content = contentWrapper && contentWrapper.props.children;
                                        return (
                                            <td className={classNames({
                                                [classes.calendarCell]: true,
                                                [classes.disabled]: day.disabled,
                                            })} key={j}>
                                                <div className={classes.day}>{day.date.date()}</div>
                                                {content ? content : !day.disabled ? (<CircularProgress size={15} />) : null}
                                            </td>
                                        );
                                    })}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </>
        );
    }
}

const ProjectCalendar = props => (
    <>
        <OldProjectsContext.Consumer>
            {projectsState => (
                (<StatefulProjectCalendar {...props} projectsState={projectsState} />)
            )}
        </OldProjectsContext.Consumer>
    </>
);
export default requiresLogin(ProjectCalendar);
