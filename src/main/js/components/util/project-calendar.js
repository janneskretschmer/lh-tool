import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import classNames from 'classnames';
import ProjectSelection from './project-selection';
import { requiresLogin, getProjectMonth, getMonthOffsetWithinRange, isMonthOffsetWithinRange, getMonthNameForOffset } from '../../util';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import IconButton from '@material-ui/core/IconButton';
import CircularProgress from '@material-ui/core/CircularProgress';

const styles = theme => ({
    calendar: {
        tableLayout: 'fixed',
        width: '100%',
        borderCollapse: 'collapse',
        minWidth: '640px',
    },
    calendarRow: {

    },
    calendarCell: {
        width: '20%',
        border: '1px solid '+theme.palette.primary.light,
        textAlign: 'center',
        padding: theme.spacing.unit,
        verticalAlign: 'top',
    },
    disabled: {
        opacity: '0.38',
    },
    dayName: {
        padding: theme.spacing.unit,
        color: theme.palette.secondary.dark,
        border: '1px solid '+theme.palette.primary.light,
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
class ProjectCalendar extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            project: null,
            data: null,
            month:0,
        };
    }

    switchProject(project) {
        let month =  getMonthOffsetWithinRange(this.state.month, project.startDate, project.endDate);
        this.setState({
            project,
            month,
            data: getProjectMonth(month, project.startDate, project.endDate),
        },this.loadDayContent);
    }

    setMonth(month) {
        this.setState({
            month,
            data:  getProjectMonth(month, this.state.project.startDate, this.state.project.endDate),
        },this.loadDayContent);
    }

    loadDayContent() {
        if(this.props.loadDayContent) {
            this.props.loadDayContent(this.state.data,this.state.project.id, this.setData.bind(this));
        }
    }

    setData(data) {
        this.setState({
            data,
        });
    }



    render() {
        const {classes, sessionState} = this.props;
        const {data, month, project} = this.state;

        if(!data){
            return (<><ProjectSelection  onChange={project => this.switchProject(project)} accessToken={sessionState.accessToken} /></>);
        }

        return (
            <>
                <div className={classes.header}>
                    <div className={classes.projectWrapper}>
                        <ProjectSelection  onChange={project => this.switchProject(project)} accessToken={sessionState.accessToken} />
                    </div>
                    <div className={classes.month}>
                        <IconButton onClick={() => this.setMonth(month - 1)} disabled={!isMonthOffsetWithinRange(month-1, project.startDate, project.endDate)}>
                            <NavigateBeforeIcon />
                        </IconButton>
                        {data.monthName}
                        <IconButton onClick={() => this.setMonth(month + 1)} disabled={!isMonthOffsetWithinRange(month+1, project.startDate, project.endDate)}>
                            <NavigateNextIcon />
                        </IconButton>
                    </div>

                </div>
                <table className={classes.calendar}>
                    <thead>
                        <tr>
                            <th className={classes.dayName}>Dienstag</th>
                            <th className={classes.dayName}>Mittwoch</th>
                            <th className={classes.dayName}>Donnerstag</th>
                            <th className={classes.dayName}>Freitag</th>
                            <th className={classes.dayName}>Samstag</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.from(Array(data.days.length/5)).map((_,i) =>
                                (<tr className={classes.calendarRow} key={i}>
                                    {Array.from(Array(5)).map((_,j) => (
                                        <td className={classNames({
                                                [classes.calendarCell]: true,
                                                [classes.disabled]: data.days[i*5+j].disabled,
                                            })} key={j}>
                                            <div className={classes.day}>{data.days[i*5+j].date.date()}</div>
                                            {data.days[i*5+j].content ? data.days[i*5+j].content : !data.days[i*5+j].disabled ? (<CircularProgress size={15}/>) : null}
                                        </td>
                                    ))}
                                </tr>
                            ))}
                    </tbody>
                </table>
            </>
        );
    }
}

export default requiresLogin(ProjectCalendar);
