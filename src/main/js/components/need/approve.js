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
import { fetchOwnNeeds } from '../../actions/need';
import { getMonthOffsetWithinRange, getProjectMonth, isMonthOffsetWithinRange, requiresLogin } from '../../util';
import ProjectSelection from '../util/project-selection';
import NeedApproveEditComponent from './approve-edit';


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
        backgroundColor: grey[200],
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
        maxWidth: '1440px',
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
    <>{['Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'][props.date.format('E') - 2]}, {props.date.format('DD.MM.YYYY')}</>
)

@withStyles(styles)
class NeedApproveComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            project: null,
            data: null,
            month: 0,
            selectedDay: null,
        };
    }

    switchProject(project) {
        let month = getMonthOffsetWithinRange(this.state.month, project.startDate, project.endDate);
        this.setState({
            project,
            month,
            data: getProjectMonth(month, project.startDate, project.endDate),
            selectedDay: null,
        }, this.loadNeeds);
    }

    setMonth(month) {
        this.setState({
            month,
            data: getProjectMonth(month, this.state.project.startDate, this.state.project.endDate),
            selectedDay: null,
        }, this.loadNeeds);
    }

    loadNeeds() {
        const { classes, sessionState } = this.props
        const { data, project } = this.state
        fetchOwnNeeds({ accessToken: sessionState.accessToken, userId: sessionState.currentUser.id, projectId: project.id, startDiff: data.startDiff, endDiff: data.endDiff }).then(result => {
            var selected = null;
            var firstActive = null;
            let days = data.days.map((day, i) => {
                let needs = result[day.date];
                if (needs) {
                    if (!firstActive) {
                        firstActive = i
                    }
                    if (!selected && !this.areNeedsReady(needs)) {
                        selected = i
                    }
                    day.needs = needs
                }
                return day
            })
            this.setState({
                data: {
                    ...data,
                    days,
                },
                selectedDay: selected ? selected : firstActive,
            })
        })
    }

    selectDay(selectedDay) {
        this.setState({
            selectedDay,
        })
    }

    isDayReady(index) {
        const { data } = this.state
        if (data.days[index] && data.days[index].needs) {
            return this.areNeedsReady(data.days[index].needs)
        }
        return false
    }
    areNeedsReady(needs) {
        var ready = true
        Object.values(needs).forEach((need) => {
            ready = ready && need.approvedCount >= need.quantity
        })
        return ready
    }

    updateApprovedCount(type, approvedCount) {
        this.setState({
            data: {
                ...this.state.data,
                days: this.state.data.days.map((day, i) => {
                    if (i === this.state.selectedDay) {
                        day.needs[type].approvedCount = approvedCount
                    }
                    return day
                })
            }
        })
    }

    //{data.days[i*5+j].content ? data.days[i*5+j].content : !data.days[i*5+j].disabled ? (<CircularProgress size={15}/>) : null}
    render() {
        const { classes, sessionState } = this.props;
        const { data, selectedDay, month, project } = this.state;

        return (
            <>
                <div>
                    <ProjectSelection onChange={project => this.switchProject(project)} accessToken={sessionState.accessToken} />
                </div>
                {data ? (
                    <>
                        <table className={classes.calendar}>
                            <thead>
                                <tr>
                                    <th colSpan="5">
                                        <div className={classes.monthWrapper}>
                                            <IconButton onClick={() => this.setMonth(month - 1)} disabled={!isMonthOffsetWithinRange(month - 1, project.startDate, project.endDate)}>
                                                <NavigateBeforeIcon />
                                            </IconButton>
                                            {data.monthName}
                                            <IconButton onClick={() => this.setMonth(month + 1)} disabled={!isMonthOffsetWithinRange(month + 1, project.startDate, project.endDate)}>
                                                <NavigateNextIcon />
                                            </IconButton>
                                        </div>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {Array.from(Array(data.days.length / 5)).map((_, i) =>
                                    (<tr className={classes.calendarRow} key={i}>
                                        {Array.from(Array(5)).map((_, j) => {
                                            let index = i * 5 + j
                                            return (
                                                <td className={classNames({
                                                    [classes.calendarCell]: true,
                                                    [classes.selected]: index === selectedDay,
                                                })} key={j}>
                                                    <Button
                                                        disabled={data.days[index].disabled}
                                                        onClick={() => this.selectDay(index)}
                                                    >
                                                        {data.days[index].date.date()}
                                                    </Button><br />
                                                    {this.isDayReady(i * 5 + j) ? (<DoneIcon />) : (<>&nbsp;</>)}
                                                </td>
                                            )
                                        })}
                                    </tr>
                                    ))}
                            </tbody>
                        </table>
                        <div className={classes.legend}>
                            Bitte klicke auf einen Tag, um die Bewerber zuzuteilen.<br /> 
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

                        {
                            selectedDay ? (
                                <>
                                    <div className={classes.dayWrapper}>
                                        <div className={classes.dateWrapper}>
                                            <Date date={data.days[parseInt(selectedDay)].date} />
                                        </div>
                                        <div className={classes.needsWrapper}>
                                            <NeedApproveEditComponent label="Bauhelfer" need={data.days[parseInt(selectedDay)].needs.CONSTRUCTION_WORKER} onApprove={(approvedCount) => this.updateApprovedCount('CONSTRUCTION_WORKER', approvedCount)} />
                                            <NeedApproveEditComponent label="Magaziner" need={data.days[parseInt(selectedDay)].needs.STORE_KEEPER} onApprove={(approvedCount) => this.updateApprovedCount('STORE_KEEPER', approvedCount)} />
                                        </div>
                                        <div className={classes.needsWrapper}>
                                            <NeedApproveEditComponent label="Küche" need={data.days[parseInt(selectedDay)].needs.KITCHEN_HELPER} onApprove={(approvedCount) => this.updateApprovedCount('KITCHEN_HELPER', approvedCount)} />
                                            <NeedApproveEditComponent label="Putzen" need={data.days[parseInt(selectedDay)].needs.CLEANER} onApprove={(approvedCount) => this.updateApprovedCount('CLEANER', approvedCount)} />
                                        </div>
                                    </div>
                                </>
                            ) : null
                        }
                    </>
                ) : null
                }
            </>
        )
    }
}

export default requiresLogin(NeedApproveComponent);
