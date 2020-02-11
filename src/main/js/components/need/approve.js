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
import { getMonthOffsetWithinRange, getProjectMonth, isMonthOffsetWithinRange, requiresLogin } from '../../util';
import ProjectSelection from '../util/project-selection';
import NeedApproveEditComponent from './approve-edit';
import WithPermission from '../with-permission';


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
            selectedStart: null,
            selectedEnd: null,
        };
    }

    switchProject(project) {
        let month = getMonthOffsetWithinRange(this.state.month, project.startDate, project.endDate);
        this.setState({
            project,
            month,
            data: getProjectMonth(month, project.startDate, project.endDate),
            selectedStart: null,
            selectedEnd: null,
        }, this.loadNeeds);
    }

    setMonth(month) {
        this.setState({
            month,
            data: getProjectMonth(month, this.state.project.startDate, this.state.project.endDate),
            selectedStart: null,
            selectedEnd: null,
        }, this.loadNeeds);
    }

    loadNeeds() {
        const { classes, sessionState } = this.props
        const { data, project } = this.state
        fetchOwnNeeds({ accessToken: sessionState.accessToken, userId: sessionState.currentUser.id, projectId: project.id, startDiff: data.startDiff, endDiff: data.endDiff }).then(result => {
            let days = data.days.map((day, i) => {
                let needs = result.get(day.date.valueOf());
                if (needs) {
                    day.needs = needs
                }
                return day
            })
            this.setState({
                data: {
                    ...data,
                    days,
                },
                selectedStart: 0,
                selectedEnd: days.length - 1,
            })
        })
    }

    selectDay(selectedStart) {
        this.setState({
            selectedStart,
            selectedEnd: selectedStart,
        })
    }
    selectDays(selectedStart, selectedEnd) {
        this.setState({
            selectedStart,
            selectedEnd,
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
        needs.forEach((need) => {
            ready = ready && need.approvedCount >= need.quantity
        })
        return ready
    }

    updateApprovedCount(index, type, approvedCount) {
        this.setState({
            data: {
                ...this.state.data,
                days: this.state.data.days.map((day, i) => {
                    if (i === index) {
                        //causes "Generic Object Injection Sink”
                        day.needs.get(type).approvedCount = approvedCount
                    }
                    return day
                })
            }
        })
    }

    //{data.days[i*5+j].content ? data.days[i*5+j].content : !data.days[i*5+j].disabled ? (<CircularProgress size={15}/>) : null}
    render() {
        const { classes, sessionState } = this.props;
        const { data, selectedStart, selectedEnd, month, project } = this.state;

        return (
            <>
                <Helmet titleTemplate="%s › Zuteilen" />
                <div>
                    <ProjectSelection onChange={project => this.switchProject(project)} accessToken={sessionState.accessToken} />
                </div>
                {data ? (
                    <>
                        <table className={classes.calendar}>
                            <thead>
                                <tr>
                                    <th colSpan="6">
                                        <div className={classes.monthWrapper}>
                                            <IconButton onClick={() => this.setMonth(month - 1)} disabled={!isMonthOffsetWithinRange(month - 1, project.startDate, project.endDate)}>
                                                <NavigateBeforeIcon />
                                            </IconButton>
                                            <Button
                                                onClick={() => this.selectDays(0, data.days.length - 1)}
                                            >
                                                {data.monthName}
                                            </Button>
                                            <IconButton onClick={() => this.setMonth(month + 1)} disabled={!isMonthOffsetWithinRange(month + 1, project.startDate, project.endDate)}>
                                                <NavigateNextIcon />
                                            </IconButton>
                                        </div>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {Array.from(Array(data.days.length / 5)).map((_, i) => {
                                    let disabled = true;
                                    return (<tr className={classes.calendarRow} key={i}>
                                        {Array.from(Array(5)).map((_, j) => {
                                            let index = i * 5 + j
                                            disabled = disabled && data.days[index].disabled
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
                                                    {this.isDayReady(i * 5 + j) ? (<DoneIcon />) : (<>&nbsp;</>)}
                                                </td>
                                            )
                                        })}
                                        <td className={classNames({
                                            [classes.calendarCell]: true,
                                            [classes.selected]: !disabled && i * 5 >= selectedStart && i * 5 + 4 <= selectedEnd,
                                        })} >
                                            <Button
                                                disabled={disabled}
                                                onClick={() => this.selectDays(i * 5, i * 5 + 4)}
                                            >
                                                KW<br />{data.days[i * 5].date.isoWeek()}
                                            </Button>
                                        </td>
                                    </tr>
                                    )
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


                        {selectedStart !== null && selectedEnd && Array.from(Array(selectedEnd - selectedStart + 1)).map((_, i) => {
                            const index = i + parseInt(selectedStart)
                            const day = data.days[index]
                            if (day.disabled || !day.needs) {
                                return null
                            }
                            return (
                                <div className={classes.dayWrapper} key={i}>
                                    <div className={classes.dateWrapper}>
                                        <Date date={day.date} />
                                    </div>
                                    <div className={classes.needsWrapper}>
                                        <NeedApproveEditComponent label="Bauhelfer" need={day.needs.get('CONSTRUCTION_WORKER')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'CONSTRUCTION_WORKER', approvedCount)} />
                                        <NeedApproveEditComponent label="Küche" need={day.needs.get('KITCHEN_HELPER')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'KITCHEN_HELPER', approvedCount)} />
                                    </div>
                                    <div className={classes.needsWrapper}>
                                        <NeedApproveEditComponent label="Magaziner" need={day.needs.get('STORE_KEEPER')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'STORE_KEEPER', approvedCount)} />
                                        <NeedApproveEditComponent label="Stadtfahrer" need={day.needs.get('DRIVER')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'DRIVER', approvedCount)} />
                                    </div>
                                    <div className={classes.needsWrapper}>
                                        <NeedApproveEditComponent label="Pforte Vormittag" need={day.needs.get('GATEKEEPER_MORNING')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'GATEKEEPER_MORNING', approvedCount)} />
                                        <NeedApproveEditComponent label="Pforte Nachmittag" need={day.needs.get('GATEKEEPER_AFTERNOON')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'DRIVER', approvedCount)} />
                                    </div>
                                    <NeedApproveEditComponent label="Putzen" need={day.needs.get('CLEANER')} onApprove={(approvedCount) => this.updateApprovedCount(index, 'CLEANER', approvedCount)} />
                                    <div className={classes.needsWrapper}>
                                    </div>
                                </div>
                            )
                        })
                        }
                    </>
                ) : null
                }
            </>
        )
    }
}

export default requiresLogin(NeedApproveComponent);
