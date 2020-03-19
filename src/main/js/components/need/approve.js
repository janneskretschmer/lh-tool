import Button from '@material-ui/core/Button';
import grey from '@material-ui/core/colors/grey';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import CheckIcon from '@material-ui/icons/Check';
import CloseIcon from '@material-ui/icons/Close';
import DoneIcon from '@material-ui/icons/Done';
import EventAvailableIcon from '@material-ui/icons/EventAvailable';
import EventBusyIcon from '@material-ui/icons/EventBusy';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import classNames from 'classnames';
import React from 'react';
import { Helmet } from 'react-helmet';
import { fetchOwnNeeds } from '../../actions/need';
import { getMonthOffsetWithinRange, getProjectMonth, isMonthOffsetWithinRange, requiresLogin, convertToMUIFormat } from '../../util';
import ProjectSelection from '../util/project-selection';
import NeedApproveEditComponent from './approve-edit';
import WithPermission from '../with-permission';
import { ProjectsContext } from '../../providers/projects-provider';
import { NeedsContext } from '../../providers/needs-provider';
import MonthSelection from '../util/month-selection';
import CircularProgress from '@material-ui/core/CircularProgress';


const styles = theme => ({
    calendar: {
        tableLayout: 'fixed',
        borderCollapse: 'collapse',
        display: 'inline-block',
        verticalAlign: 'top',
        marginRight: theme.spacing.unit,
    },
    calendarRow: {

    },
    calendarCell: {
        width: '20%',
        border: '1px solid ' + theme.palette.primary.light,
        textAlign: 'center',
        padding: theme.spacing.unit,
        verticalAlign: 'top',
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
    selected: {
        backgroundColor: grey[300],
    },
    monthWrapper: {
        width: '100%',
        justifyContent: 'center',
        alignItems: 'center',
        display: 'flex',
        fontSize: 'large',
        fontWeight: 'normal',
    },
    dayWrapper: {
        display: 'inline-block',
        maxWidth: '2160px',
        width: '100%',
        verticalAlign: 'top',
    },
    needsWrapper: {
        display: 'inline-block',
        maxWidth: '720px',
        width: '100%',
        verticalAlign: 'top',
    },
    dateWrapper: {
        textAlign: 'center',
        margin: '9px',
        fontSize: 'larger',
    },
    legend: {
        display: 'inline-block',
        padding: theme.spacing.unit,
    },
});

const Date = props => (
    <>{['Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag', 'Sonntag'][props.date.isoWeekday() - 1]}, {props.date.format('DD.MM.YYYY')}</>
);

@withStyles(styles)
class StatefulNeedApproveComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedMonth: null,
            selectedProject: null,
            selectedStart: null,
            selectedEnd: null,
        };
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        //new data needs to be loaded on every change of project or month
        if (nextProps.projectsState.selectedMonthCalendarData
            && nextProps.projectsState.selectedMonthCalendarData.days.length > 0
            && (nextProps.projectsState.selectedMonthCalendarData.monthOffset !== prevState.selectedMonth
                || nextProps.projectsState.selectedProjectIndex !== prevState.selectedProject)) {
            const selectedStart = 0;
            const selectedEnd = nextProps.projectsState.selectedMonthCalendarData.days.length - 1;
            Array.from(Array(selectedEnd)).map((_, index) => {
                const day = nextProps.projectsState.selectedMonthCalendarData.days[index];
                if (!day.disabled) {
                    nextProps.needsState.loadHelperTypesWithNeedsAndUsersByProjectIdAndDate(nextProps.projectsState.getSelectedProject().id, day.date);
                }
            });
            return {
                selectedMonth: nextProps.projectsState.selectedMonthCalendarData.monthOffset,
                selectedProject: nextProps.projectsState.selectedProjectIndex,
                selectedStart,
                selectedEnd,
            }
        }
        return null;
    }

    selectDay(selectedStart) {
        this.setState({
            selectedStart,
            selectedEnd: selectedStart,
        });
    }
    selectDays(selectedStart, selectedEnd) {
        this.setState({
            selectedStart,
            selectedEnd,
        });
    }

    isDayReady(index) {
        const data = this.props.projectsState.selectedMonthCalendarData;
        if (data.days[index] && data.days[index].needs) {
            return this.areNeedsReady(data.days[index].needs);
        }
        return false;
    }
    areNeedsReady(needs) {
        var ready = true;
        needs.forEach((need) => {
            ready = ready && this.props.needsState.getApprovedCount(need) >= need.quantity;
        });
        return ready;
    }

    //{data.days[i*5+j].content ? data.days[i*5+j].content : !data.days[i*5+j].disabled ? (<CircularProgress size={15}/>) : null}
    render() {
        const { classes, sessionState, projectsState, needsState } = this.props;
        const { selectedStart, selectedEnd, month } = this.state;
        const data = projectsState.selectedMonthCalendarData;
        const projectId = projectsState.getSelectedProject().id;
        const dayMap = needsState.projects && needsState.projects.has(projectId) && needsState.projects.get(projectId).days;
        return (
            <>
                <Helmet titleTemplate="%s › Zuteilen" />
                <div>
                    <ProjectSelection />
                </div>
                {data ? (
                    <>
                        <table className={classes.calendar}>
                            <thead>
                                <tr>
                                    <th colSpan="8">
                                        <MonthSelection onClick={() => this.selectDays(0, data.days.length - 1)} />
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {Array.from(Array(data.days.length / 7)).map((_, i) => {
                                    let disabled = true;
                                    return (<tr className={classes.calendarRow} key={i}>
                                        {Array.from(Array(7)).map((_, j) => {
                                            const index = i * 7 + j;
                                            disabled = disabled && data.days[index].disabled;
                                            return (
                                                <td className={classNames({
                                                    [classes.calendarCell]: true,
                                                    [classes.selected]: !data.days[index].disabled && index >= selectedStart && index <= selectedEnd,
                                                })} key={j}>
                                                    <Button
                                                        disabled={data.days[index].disabled}
                                                        onClick={() => this.selectDay(index)}
                                                    >
                                                        {data.days[index].date.date()}
                                                    </Button><br />
                                                    {this.isDayReady(i * 7 + j) ? (<DoneIcon />) : (<>&nbsp;</>)}
                                                </td>
                                            );
                                        })}
                                        <td className={classNames({
                                            [classes.calendarCell]: true,
                                            [classes.selected]: !disabled && i * 7 >= selectedStart && i * 7 + 6 <= selectedEnd,
                                        })} >
                                            <Button
                                                disabled={disabled}
                                                onClick={() => this.selectDays(i * 7, i * 7 + 6)}
                                            >
                                                KW<br />{data.days[i * 7].date.isoWeek()}
                                            </Button>
                                        </td>
                                    </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                        <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                            <div className={classes.legend}>
                                Bitte klicke auf einen Tag, eine Kalenderwoche oder den Monat, um die Bewerber zuzuteilen.<br />
                                Wenn alle Positionen besetzt sind, erscheint ein Haken.<br />
                                <br />
                                Bedeutung der Klammern neben der Aufgabenbezeichnung: ( Anzahl der Genehmigungen / Bedarf )<br />
                                <IconButton>
                                    <CheckIcon />
                                </IconButton>
                                Nicht genehmigt, klicke hier um die Person für diese Schicht einzuteilen.
                            <br />
                                <IconButton>
                                    <EventAvailableIcon />
                                </IconButton>
                                Genehmigt, klicke hier um die Genehmigung zurückzuziehen.
                            <br />
                                <IconButton>
                                    <CloseIcon />
                                </IconButton>
                                Nicht abgelehnt, klicke hier um die Person für diese Schicht explizit abzulehnen.
                            <br />
                                <IconButton>
                                    <EventBusyIcon />
                                </IconButton>
                                Abgelehnt, klicke hier um die Ablehnung zurückzuziehen.
                        </div>
                        </WithPermission>


                        {selectedStart !== null && selectedEnd !== null && Array.from(Array(selectedEnd - selectedStart + 1)).map((_, i) => {
                            const index = i + parseInt(selectedStart);
                            const day = data.days[index];
                            const dateString = convertToMUIFormat(day.date);
                            if (day.disabled || !dayMap || !dayMap.has(dateString)) {
                                return null;
                            }
                            const helperTypes = dayMap.get(dateString).helperTypes;
                            return (
                                <div className={classes.dayWrapper} key={i}>
                                    <div className={classes.dateWrapper}>
                                        <Date date={day.date} />
                                    </div>
                                    {helperTypes ? helperTypes.map(
                                        helperType => helperType.shifts ? helperType.shifts.map(
                                            shift => shift.need && shift.need.users ? (
                                                <NeedApproveEditComponent
                                                    key={shift.id + dateString}
                                                    label={helperType.name + ' ' + (shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime)}
                                                    projectHelperType={shift} />
                                            ) : !shift.need || shift.need.id ? (
                                                <CircularProgress key={shift.id + dateString} size={15} />
                                            ) : null
                                        ) : (<CircularProgress key={helperType.id + dateString} size={15} />)
                                    ) : (<CircularProgress size={15} />)}
                                </div>
                            );
                        })
                        }
                    </>
                ) : null
                }
            </>
        );
    }
}

const NeedApproveComponent = props => (
    <>
        <ProjectsContext.Consumer>
            {projectsState => (
                <NeedsContext.Consumer>
                    {needsState =>
                        (<StatefulNeedApproveComponent {...props} needsState={needsState} projectsState={projectsState} />)
                    }
                </NeedsContext.Consumer>
            )}
        </ProjectsContext.Consumer>
    </>
);
export default requiresLogin(NeedApproveComponent);
