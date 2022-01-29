import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import DoneIcon from '@mui/icons-material/Done';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import { Typography } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import { grey } from '@mui/material/colors';
import IconButton from '@mui/material/IconButton';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { RIGHT_NEEDS_APPROVE } from '../../permissions';
import { NeedsContext } from '../../providers/needs-provider';
import { convertToDDMMYYYY, convertToYYYYMMDD, getWeek, requiresLogin } from '../../util';
import WithPermission from '../with-permission';
import NeedApproveEditComponent from './approve-edit';
import NeedMonthSelection from './need-month-selection';
import NeedProjectSelection from './need-project-selection';


const Date = props => (
    <>{props.date.toLocaleString('default', { weekday: 'long' })}, {convertToDDMMYYYY(props.date)}</>
);

@withSnackbar
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

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Laden der Zuteilungen', {
            variant: 'error',
        });
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        //new data needs to be loaded on every change of project or month
        const project = nextProps.needsState.getSelectedProject();
        if (project && project.selectedMonthData && project.selectedMonthData.days.length > 0
            && (project.selectedMonthData.monthOffset !== prevState.selectedMonth
                || project.id !== prevState.selectedProject)) {
            const selectedStart = 0;
            const selectedEnd = project.selectedMonthData.days.length - 1;

            return {
                selectedMonth: project.selectedMonthData.monthOffset,
                selectedProject: project.id,
                selectedStart,
                selectedEnd,
            };
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
        const project = this.props.needsState.getSelectedProject();
        const data = project.selectedMonthData;
        if (data && data.days[index] && project.days) {
            const dayData = project.days.get(convertToYYYYMMDD(data.days[index].date));
            return dayData &&
                dayData.helperTypes
                    .flat(2).map(helperType => helperType.shifts)
                    .flat(2).map(shift => shift.need)
                    .every(need => this.props.needsState.getApprovedCount(need) >= need.quantity);

        }
        return false;
    }

    render() {
        const { classes, needsState } = this.props;
        const { selectedStart, selectedEnd, month } = this.state;
        const project = needsState.getSelectedProject();
        const data = project && project.selectedMonthData;
        const dayMap = project && project.days;
        return <>
            <div>
                <NeedProjectSelection />
            </div>
            {data ? (
                <>
                    <Box component="table" sx={{
                        tableLayout: 'fixed',
                        borderCollapse: 'collapse',
                        display: 'inline-block',
                        verticalAlign: 'top',
                        mr: 1,
                    }}>
                        <thead>
                            <tr>
                                <th colSpan="8">
                                    <NeedMonthSelection onClick={() => this.selectDays(0, data.days.length - 1)} />
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {Array.from(Array(data.days.length / 7)).map((_, i) => {
                                let weekDisabled = true;
                                return (<tr key={i}>
                                    {Array.from(Array(7)).map((_, j) => {
                                        const index = i * 7 + j;
                                        weekDisabled = weekDisabled && data.days[index].disabled;
                                        return (
                                            <Box
                                                component="td"
                                                sx={{
                                                    width: '20%',
                                                    border: '1px solid ',
                                                    borderColor: 'primary.light',
                                                    textAlign: 'center',
                                                    pt: 1,
                                                    pl: 2,
                                                    pr: 2,
                                                    verticalAlign: 'top',
                                                    cursor: data.days[index].disabled ? undefined : 'pointer',
                                                    backgroundColor: !data.days[index].disabled && index >= selectedStart && index <= selectedEnd ? grey[200] : undefined,
                                                }}
                                                key={j}
                                                onClick={data.days[index].disabled ? undefined : (() => this.selectDay(index))}
                                            >
                                                <Typography variant="button" color={data.days[index].disabled ? 'textSecondary' : 'inherit'}>
                                                    {data.days[index].date.getDate()}
                                                </Typography><br />
                                                {this.isDayReady(i * 7 + j) ? (
                                                    <DoneIcon color={data.days[index].disabled ? 'disabled' : 'inherit'} />
                                                ) : (<>&nbsp;</>)}
                                            </Box>
                                        );
                                    })}
                                    <Box
                                        component="td"
                                        sx={{
                                            width: '20%',
                                            border: '1px solid ',
                                            borderColor: 'primary.light',
                                            textAlign: 'center',
                                            pt: 1,
                                            pl: 2,
                                            pr: 2,
                                            verticalAlign: 'top',
                                            cursor: weekDisabled ? undefined : 'pointer',
                                            backgroundColor: !weekDisabled && i * 7 >= selectedStart && i * 7 + 6 <= selectedEnd ? grey[200] : undefined,
                                        }}
                                        onClick={weekDisabled ? undefined : (() => this.selectDays(i * 7, i * 7 + 6))}
                                    >
                                        <Typography variant="button" color={weekDisabled ? 'textSecondary' : 'inherit'}>
                                            KW<br />{getWeek(data.days[i * 7].date)}
                                        </Typography>
                                    </Box>
                                </tr>
                                );
                            })}
                        </tbody>
                    </Box>
                    <WithPermission permission={RIGHT_NEEDS_APPROVE}>
                        <Box sx={{
                            display: 'inline-block',
                            p: 1,
                        }}>
                            Bitte klicke auf einen Tag, eine Kalenderwoche oder den Monat, um die Bewerber zuzuteilen.<br />
                            Wenn alle Positionen besetzt sind, erscheint ein Haken.<br />
                            <br />
                            Bedeutung der Klammern neben der Aufgabenbezeichnung: ( Anzahl der Genehmigungen / Bedarf )<br />
                            <IconButton size="large">
                                <CheckIcon />
                            </IconButton>
                            Nicht genehmigt, klicke hier um die Person für diese Schicht einzuteilen.
                            <br />
                            <IconButton size="large">
                                <EventAvailableIcon />
                            </IconButton>
                            Genehmigt, klicke hier um die Genehmigung zurückzuziehen.
                            <br />
                            <IconButton size="large">
                                <CloseIcon />
                            </IconButton>
                            Nicht abgelehnt, klicke hier um die Person für diese Schicht explizit abzulehnen.
                            <br />
                            <IconButton size="large">
                                <EventBusyIcon />
                            </IconButton>
                            Abgelehnt, klicke hier um die Ablehnung zurückzuziehen.
                        </Box>
                    </WithPermission>


                    {selectedStart !== null && selectedEnd !== null && Array.from(Array(selectedEnd - selectedStart + 1)).map((_, i) => {
                        const index = i + parseInt(selectedStart);
                        const day = data.days[index];
                        const dateString = convertToYYYYMMDD(day.date);
                        if (day.disabled || !dayMap || !dayMap.has(dateString)) {
                            return null;
                        }
                        const helperTypes = dayMap.get(dateString).helperTypes;
                        return (
                            <Box sx={{
                                display: 'inline-block',
                                maxWidth: '2160px',
                                width: '100%',
                                verticalAlign: 'top',
                            }} key={i}>
                                <Box sx={{
                                    textAlign: 'center',
                                    margin: '9px',
                                    fontSize: 'larger',
                                }}>
                                    <Date date={day.date} />
                                </Box>
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
                            </Box>
                        );
                    })
                    }
                </>
            ) : null
            }
        </>;
    }
}

const NeedApproveComponent = props => (
    <NeedsContext.Consumer>
        {needsState =>
            (<StatefulNeedApproveComponent {...props} needsState={needsState} />)
        }
    </NeedsContext.Consumer>
);
export default requiresLogin(NeedApproveComponent);
