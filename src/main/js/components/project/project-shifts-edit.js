import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { Button, IconButton, TextField, Dialog, DialogTitle, DialogContent, FormControl, InputLabel, Select, MenuItem, DialogActions, CircularProgress, Typography } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';
import DeleteIcon from '@material-ui/icons/Delete';
import ProjectsProvider, { ProjectsContext } from '../../providers/projects-provider';
import { requiresLogin } from '../../util';


const styles = theme => ({
    calendar: {
        tableLayout: 'fixed',
        width: '100%',
        borderCollapse: 'collapse',
        minWidth: '1200px',
        marginBottom: theme.spacing.unit,
    },
    calendarRow: {

    },
    calendarCell: {
        width: '20%',
        border: '1px solid ' + theme.palette.primary.light,
        padding: theme.spacing.unit,
        verticalAlign: 'top',
        textAlign: 'left'
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
    button: {
        marginTop: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    shiftContainer: {
        display: 'flex',
    },
    shift: {
        marginBottom: '3px',
        minWidth: '105px',
        flexGrow: '1',
    },
    shiftAdd: {
        width: '100%',
    },
    helperTypeContainer: {
        marginBottom: 3 * theme.spacing.unit,
    },
    dialogContent: {
        display: 'flex',
        alignItems: 'baseline',
    },
    dialogItem: {
        marginRight: theme.spacing.unit,
    },
});

@withStyles(styles)
class StatefulProjectShiftEditComponent extends React.Component {
    render() {
        const { classes, projectsState, disabled } = this.props;
        const shifts = projectsState.getCurrentShifts();

        if (!projectsState) {
            return (<CircularProgress />);
        }

        return (<>
            <Typography variant="h6">Schichten</Typography>
            <Button disabled={disabled} variant="contained" color="primary" className={classes.button} onClick={() => projectsState.addStandardShifts()}><AddIcon /> Standardschichten</Button>
            <Button disabled={disabled} variant="contained" color="primary" className={classes.button} onClick={() => projectsState.addGateKeeperShifts()}><AddIcon /> Pförtner</Button>
            <Button disabled={disabled} variant="contained" color="primary" className={classes.button} onClick={() => projectsState.addSecurityShifts()}><AddIcon /> Nachtwächter</Button>


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
                {shifts && (
                    <tbody>
                        <tr className={classes.calendarRow}>
                            {
                                [...shifts.keys()].map(weekday => (
                                    <td key={weekday} className={classes.calendarCell}>
                                        <div className={classes.day}>{weekday}</div>
                                        {projectsState.helperTypes.map(helperType =>
                                            shifts.get(weekday).get(helperType.id) &&
                                            shifts.get(weekday).get(helperType.id).length > 0 && (
                                                <div key={helperType.id} className={classes.helperTypeContainer}>
                                                    {helperType.name}<br />
                                                    {shifts.get(weekday).get(helperType.id).map(shift => (
                                                        <div
                                                            key={weekday + shift.startTime + shift.helperTypeId}
                                                            className={classes.shiftContainer}
                                                        >
                                                            <Button
                                                                // id doesn't work for new shifts
                                                                variant="outlined"
                                                                className={classes.shift}
                                                                onClick={() => shift.id && projectsState.changeShift(shift)}
                                                                disabled={disabled}
                                                            >
                                                                {shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime}
                                                            </Button>
                                                            <IconButton
                                                                disabled={disabled}
                                                                onClick={() => projectsState.removeShift(shift)}
                                                            >
                                                                <DeleteIcon />
                                                            </IconButton>
                                                        </div>
                                                    ))}
                                                </div>
                                            ))}
                                        <Button
                                            className={classes.shiftAdd}
                                            variant="contained"
                                            onClick={event => projectsState.createShift(weekday)}
                                            disabled={disabled}
                                        >
                                            Schicht hinzufügen
                                        </Button>

                                    </td>
                                ))
                            }
                        </tr>
                    </tbody>
                )}
            </table>
            {!disabled && projectsState.selectedShift && (
                <Dialog
                    open={true}>
                    <DialogTitle>Schicht bearbeiten</DialogTitle>
                    <DialogContent className={classes.dialogContent}>
                        <TextField
                            className={classes.dialogItem}
                            id="start"
                            label="Start"
                            type="time"
                            value={projectsState.selectedShift.startTime}
                            onChange={event => projectsState.changeShiftStartTime(event.target.value)}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            inputProps={{
                                step: 900, // 15 min
                            }}
                        />
                        <div className={classes.dialogItem}>-</div>
                        <TextField
                            className={classes.dialogItem}
                            id="start"
                            label="Ende"
                            type="time"
                            value={projectsState.selectedShift.endTime}
                            onChange={event => projectsState.changeShiftEndTime(event.target.value)}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            inputProps={{
                                step: 900, // 15 min
                            }}
                        />
                        {!projectsState.selectedShift.id && (
                            <FormControl>
                                <InputLabel htmlFor="type">Aufgabe</InputLabel>
                                <Select
                                    value={projectsState.selectedShift.helperTypeId}
                                    onChange={event => projectsState.changeShiftHelperTypeId(event.target.value)}
                                    inputProps={{
                                        name: 'type',
                                        id: 'type',
                                    }}
                                >
                                    {projectsState.helperTypes.map(helperType => (
                                        <MenuItem key={helperType.id} value={helperType.id}>{helperType.name}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        )}
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={event => projectsState.changeShift(null)}
                        >
                            Abbrechen
                        </Button>
                        <Button
                            onClick={() => projectsState.applySelectedShift()}
                            disabled={!projectsState.isShiftValid()}
                            variant="contained"
                            color="primary"
                        >
                            OK
                        </Button>
                    </DialogActions>
                </Dialog>
            )}
        </>);
    }
}

const ProjectShiftEditComponent = props => (
    <ProjectsContext.Consumer>
        {projectsState => (<StatefulProjectShiftEditComponent {...props} projectsState={projectsState} />)}
    </ProjectsContext.Consumer>
);
export default requiresLogin(ProjectShiftEditComponent);