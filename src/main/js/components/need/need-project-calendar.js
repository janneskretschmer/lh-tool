import CircularProgress from '@material-ui/core/CircularProgress';
import { withStyles } from '@material-ui/core/styles';
import classNames from 'classnames';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { convertToMUIFormat, requiresLogin } from '../../util';
import NeedMonthSelection from './need-month-selection';
import NeedProjectSelection from './need-project-selection';

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
class StatefulNeedProjectCalendar extends React.Component {

    setMonth(monthOffset) {
        this.props.needsState.setMonth(monthOffset);
    }


    render() {
        const { classes, sessionState, needsState, dateContentMap } = this.props;
        const project = needsState.getSelectedProject();
        const data = project && project.selectedMonthData;

        return (
            <>
                <div className={classes.header}>
                    <div className={classes.projectWrapper}>
                        <NeedProjectSelection />
                    </div>
                    {data ? (
                        <NeedMonthSelection className={classes.month} />
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
                                        const content = dateContentMap && dateContentMap.get(convertToMUIFormat(day.date));
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

const NeedProjectCalendar = props => (
    <>
        <NeedsContext.Consumer>
            {needsState => (
                (<StatefulNeedProjectCalendar {...props} needsState={needsState} />)
            )}
        </NeedsContext.Consumer>
    </>
);
export default requiresLogin(NeedProjectCalendar);
