import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, TextField, Typography } from '@mui/material';
import { Box } from '@mui/system';
import React from 'react';
import { ProjectsContext } from '../../providers/projects-provider';
import { requiresLogin } from '../../util';
import IdNameSelect from '../util/id-name-select';

class StatefulProjectShiftEditComponent extends React.Component {
    render() {
        const { classes, projectsState, disabled } = this.props;
        const shifts = projectsState.getCurrentShifts();

        const sxDayName = {
            p: 1,
            color: 'secondary.dark',
            border: '1px solid',
            borderColor: 'primary.light',
            fontWeight: 'normal',
            fontSize: 'large',
        };

        if (!projectsState) {
            return (<CircularProgress />);
        }

        return <>
            <Typography variant="h6">Schichten</Typography>
            <Button disabled={disabled} variant="contained" color="primary" sx={{ mt: 1, mr: 1, mb: 1 }} onClick={() => projectsState.addStandardShifts()}><AddIcon /> Standardschichten</Button>
            <Button disabled={disabled} variant="contained" color="primary" sx={{ mt: 1, mr: 1, mb: 1 }} onClick={() => projectsState.addGateKeeperShifts()}><AddIcon /> Pförtner</Button>
            <Button disabled={disabled} variant="contained" color="primary" sx={{ mt: 1, mr: 1, mb: 1 }} onClick={() => projectsState.addSecurityShifts()}><AddIcon /> Nachtwächter</Button>


            <Box component="table" sx={{
                tableLayout: 'fixed',
                width: '100%',
                borderCollapse: 'collapse',
                minWidth: '1200px',
                mb: 1,
            }}>
                <thead>
                    <tr>
                        <Box component="th" sx={sxDayName}>Montag</Box>
                        <Box component="th" sx={sxDayName}>Dienstag</Box>
                        <Box component="th" sx={sxDayName}>Mittwoch</Box>
                        <Box component="th" sx={sxDayName}>Donnerstag</Box>
                        <Box component="th" sx={sxDayName}>Freitag</Box>
                        <Box component="th" sx={sxDayName}>Samstag</Box>
                        <Box component="th" sx={sxDayName}>Sonntag</Box>
                    </tr>
                </thead>
                {shifts ? (
                    <tbody>
                        <tr>
                            {
                                [...shifts.keys()].map(weekday => (
                                    <Box component="td" key={weekday} sx={{
                                        width: '20%',
                                        border: '1px solid',
                                        borderColor: 'primary.light',
                                        p: 1,
                                        verticalAlign: 'top',
                                        textAlign: 'left'
                                    }}>
                                        <Box sx={{
                                            width: '100%',
                                            textAlign: 'right',
                                            color: 'secondary.main',
                                            fontWeight: 'bold',
                                            fontSize: 'larger',
                                            padding: '3px',
                                        }}>
                                            {weekday}
                                        </Box>
                                        {projectsState.helperTypes.map(helperType =>
                                            shifts.get(weekday).get(helperType.id) &&
                                            shifts.get(weekday).get(helperType.id).length > 0 && (
                                                <Box key={helperType.id} sx={{ mb: 3 }}>
                                                    {helperType.name}<br />
                                                    {shifts.get(weekday).get(helperType.id).map(shift => (
                                                        <Box
                                                            key={weekday + shift.startTime + shift.helperTypeId}
                                                            sx={{ display: 'flex' }}
                                                        >
                                                            <Button
                                                                // id doesn't work for new shifts
                                                                variant="outlined"
                                                                sx={{
                                                                    marginBottom: '3px',
                                                                    minWidth: '105px',
                                                                    flexGrow: '1',
                                                                }}
                                                                onClick={() => shift.id && projectsState.changeShift(shift)}
                                                                disabled={disabled}
                                                            >
                                                                {shift.endTime ? shift.startTime + ' - ' + shift.endTime : 'ab ' + shift.startTime}
                                                            </Button>
                                                            <IconButton
                                                                disabled={disabled}
                                                                onClick={() => projectsState.removeShift(shift)}
                                                                size="large">
                                                                <DeleteIcon />
                                                            </IconButton>
                                                        </Box>
                                                    ))}
                                                </Box>
                                            ))}
                                        <Button
                                            sx={{ width: '100%' }}
                                            variant="contained"
                                            onClick={event => projectsState.createShift(weekday)}
                                            disabled={disabled}
                                        >
                                            Schicht hinzufügen
                                        </Button>

                                    </Box>
                                ))
                            }
                        </tr>
                    </tbody>
                ) : (
                    <tbody>
                        <tr colspan={7}><td><CircularProgress /></td></tr>
                    </tbody>
                )}
            </Box>
            {!disabled && projectsState.selectedShift && (
                <Dialog
                    open={true}>
                    <DialogTitle>Schicht bearbeiten</DialogTitle>
                    <DialogContent>
                        <Box sx={{
                            display: 'flex',
                            alignItems: 'baseline',
                            pt: 1
                        }}>
                            <TextField
                                sx={{ mr: 1 }}
                                id="start"
                                label="Start"
                                type="time"
                                value={projectsState.selectedShift.startTime}
                                onChange={event => projectsState.changeShiftStartTime(event.target.value)}
                                inputProps={{
                                    step: 900, // 15 min
                                }}
                                size="small"
                            />
                            <Box sx={{ mr: 1 }}>-</Box>
                            <TextField
                                sx={{ mr: 1 }}
                                id="start"
                                label="Ende"
                                type="time"
                                value={projectsState.selectedShift.endTime}
                                onChange={event => projectsState.changeShiftEndTime(event.target.value)}
                                inputProps={{
                                    step: 900, // 15 min
                                }}
                                size="small"
                            />
                            {!projectsState.selectedShift.id && (
                                <IdNameSelect
                                    label="Aufgabe"
                                    value={projectsState.selectedShift.helperTypeId}
                                    onChange={value => projectsState.changeShiftHelperTypeId(value)}
                                    data={projectsState.helperTypes}
                                />
                            )}
                        </Box>
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
        </>;
    }
}

const ProjectShiftEditComponent = props => (
    <ProjectsContext.Consumer>
        {projectsState => (<StatefulProjectShiftEditComponent {...props} projectsState={projectsState} />)}
    </ProjectsContext.Consumer>
);
export default requiresLogin(ProjectShiftEditComponent);