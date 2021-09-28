import Button from '@material-ui/core/Button';
import { green, red, yellow, grey } from '@material-ui/core/colors';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { requiresLogin, withContext, convertToYYYYMMDD } from '../../util';
import NeedProjectCalendar from './need-project-calendar';
import NeedApplyEditComponent from './apply-edit';
import { CircularProgress } from '@material-ui/core';
import { ProjectsContext } from '../../providers/projects-provider';

const styles = theme => ({
    helperTypeName: {
        marginTop: '10px',
    },
    applyWrapper: {
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexWrap: 'wrap',
    },
    legendItem: {
        alignItems: 'center',
        margin: theme.spacing.unit,
    },
    buttonText: {
        color: grey[700],
    },
    red: {
        color: '#f00',
    },
    applied: {
        backgroundColor: yellow[600],
    },
    approved: {
        backgroundColor: green[600],
    },
    rejected: {
        backgroundColor: red[600],
    },
});

@withStyles(styles)
@withSnackbar
class StatefulNeedApplyComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Laden des Bedarfs', {
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
                            {dayData.helperTypes && dayData.helperTypes
                                .map(
                                    helperType => helperType && (
                                        <div key={helperType.id}>
                                            <div className={classes.helperTypeName}>
                                                {helperType.name}
                                            </div>
                                            {helperType.shifts && helperType.shifts[0] && helperType.shifts[0].need ? helperType.shifts.map(shift => (
                                                <div key={shift.id} className={classes.inputWrapper}>
                                                    {shift.need && (shift.need.state || !shift.need.id) ? (
                                                        <NeedApplyEditComponent
                                                            need={shift.need}
                                                            helperTypeName={helperType.name}
                                                            projectHelperType={shift}
                                                            label={shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime}></NeedApplyEditComponent>
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
            <>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="outlined" disabled={true}>Zeitraum Bewerberanzahl/Bedarf</Button> &nbsp;Es besteht kein Bedarf an diesem Tag.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="outlined"><span className={classes.buttonText}>Zeitraum</span>&nbsp;&nbsp;Bewerberanzahl/Bedarf</Button> &nbsp;Nicht beworben. Bitte klicke auf den jeweiligen Button, um dich zu bewerben.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.applied}><span className={classes.buttonText}>Zeitraum</span>&nbsp;&nbsp; Bewerberanzahl/Bedarf</Button> &nbsp;Beworben, jedoch noch nicht zugeteilt. Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.approved}><span className={classes.buttonText}>Zeitraum</span>&nbsp;&nbsp; Bewerberanzahl/Bedarf</Button> &nbsp;Zugeteilt,<span className={classes.red}> komme bitte nur dann zur Baustelle</span>, vielen Dank! Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                Aufgabe
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.rejected}>Zeitraum</Button> &nbsp;Nicht zugeteilt, bitte bewerbe dich für ein anderes Datum.
                </div>
                <br />


                <NeedProjectCalendar dateContentMap={dateContentMap} />
            </>
        );
    }
}

const NeedApplyComponent = props => (
    <>
        <ProjectsContext.Consumer>
            {projectsState => (
                <NeedsContext.Consumer>
                    {needsState =>
                        (<StatefulNeedApplyComponent {...props} needsState={needsState} projectsState={projectsState} />)
                    }
                </NeedsContext.Consumer>
            )}
        </ProjectsContext.Consumer>
    </>
);
export default requiresLogin(NeedApplyComponent);


