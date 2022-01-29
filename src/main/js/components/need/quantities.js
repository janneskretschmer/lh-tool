import { CircularProgress } from '@mui/material';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { convertToYYYYMMDD, requiresLogin } from '../../util';
import NeedProjectCalendar from './need-project-calendar';
import NeedQuantityEditComponent from './quantity-edit';

@withSnackbar
class StatefulNeedQuantityComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Laden der Bedarfe', {
            variant: 'error',
        });
    }

    render() {
        const { classes, needsState } = this.props;
        const project = needsState.getSelectedProject();
        const dayMap = project && project.days;
        const selectedDays = project && project.selectedMonthData.days;

        const dateContentMap = new Map();
        if (dayMap && selectedDays) {
            selectedDays.forEach(day => {
                const dateString = convertToYYYYMMDD(day.date);
                const dayData = dayMap.get(dateString);
                if (dayData) {
                    dateContentMap.set(dateString, (
                        <div key={dateString} date={dayData.date}>

                            {dayData.helperTypes && dayData.helperTypes.map(
                                helperType => (
                                    <div key={helperType.id}>
                                        <Box sx={{
                                            marginTop: '15px',
                                            marginBottom: '5px',
                                        }}>
                                            {helperType.name}
                                        </Box>
                                        {helperType.shifts && helperType.shifts[0] && helperType.shifts[0].need ? helperType.shifts.map(shift => (
                                            <Box key={shift.id} sx={{
                                                width: '100%',
                                                display: 'flex',
                                                alignItems: 'center',
                                                flexWrap: 'wrap',
                                            }}>
                                                {shift.need ? (
                                                    <NeedQuantityEditComponent
                                                        projectHelperType={shift}
                                                        label={shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime} />
                                                ) : (<CircularProgress size={15} />)}
                                            </Box>
                                        )) : (<CircularProgress size={15} />)}
                                    </div>
                                )
                            )}
                        </div>
                    ));
                }
            });
        }

        return (
            <NeedProjectCalendar dateContentMap={dateContentMap} />
        );
    }
}

const NeedQuantityComponent = props => (
    <NeedsContext.Consumer>
        {needsState =>
            (<StatefulNeedQuantityComponent {...props} needsState={needsState} />)
        }
    </NeedsContext.Consumer>
);
export default requiresLogin(NeedQuantityComponent);
