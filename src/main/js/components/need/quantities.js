import { CircularProgress } from '@material-ui/core';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { requiresLogin, convertToMUIFormat } from '../../util';
import NeedProjectCalendar from './need-project-calendar';
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
                const dateString = convertToMUIFormat(day.date);
                const dayData = dayMap.get(dateString);
                if (dayData) {
                    dateContentMap.set(dateString, (
                        <div key={dateString} date={dayData.date}>

                            {dayData.helperTypes && dayData.helperTypes.map(
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
