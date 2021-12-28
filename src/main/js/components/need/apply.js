import { CircularProgress } from '@mui/material';
import Button from '@mui/material/Button';
import { green, grey, red, yellow } from '@mui/material/colors';
import { Box } from '@mui/system';
import { withSnackbar } from 'notistack';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { ProjectsContext } from '../../providers/projects-provider';
import { convertToYYYYMMDD, requiresLogin } from '../../util';
import NeedApplyEditComponent from './apply-edit';
import NeedProjectCalendar from './need-project-calendar';

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
                                            <Box sx={{ marginTop: '10px' }}>
                                                {helperType.name}
                                            </Box>
                                            {helperType.shifts && helperType.shifts[0] && helperType.shifts[0].need ? helperType.shifts.map(shift => (
                                                <div key={shift.id}>
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
                <Box sx={{
                    alignItems: 'center',
                    m: 1,
                }}>
                    <Button variant="outlined" disabled={true} sx={{ width: '310px' }}>Zeitraum Bewerberanzahl/Bedarf</Button> &nbsp;Es besteht kein Bedarf an diesem Tag.
                </Box>
                <Box sx={{
                    alignItems: 'center',
                    m: 1,
                }}>
                    <Button variant="outlined" sx={{ width: '310px' }}><Box sx={{ color: grey[700] }}>Zeitraum</Box>&nbsp;&nbsp;Bewerberanzahl/Bedarf</Button> &nbsp;Nicht beworben. Bitte klicke auf den jeweiligen Button, um dich zu bewerben.
                </Box>
                <Box sx={{
                    alignItems: 'center',
                    m: 1,
                }}>
                    <Button variant="contained" sx={{ width: '310px', backgroundColor: yellow[600] }}><Box sx={{ color: grey[700] }}>Zeitraum</Box>&nbsp;&nbsp; Bewerberanzahl/Bedarf</Button> &nbsp;Beworben, jedoch noch nicht zugeteilt. Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </Box>
                <Box sx={{
                    alignItems: 'center',
                    m: 1,
                }}>
                    <Button variant="contained" sx={{ width: '310px', backgroundColor: green[600] }}><Box sx={{ color: grey[700] }}>Zeitraum</Box>&nbsp;&nbsp; Bewerberanzahl/Bedarf</Button> &nbsp;Zugeteilt,<Box component="span" sx={{ color: '#f00' }}> komme bitte nur dann zur Baustelle</Box>, vielen Dank! Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </Box>
                <Box sx={{
                    alignItems: 'center',
                    m: 1,
                }}>
                    <Button variant="contained" sx={{ width: '310px', backgroundColor: red[600] }}>Zeitraum</Button> &nbsp;Nicht zugeteilt, bitte bewerbe dich für ein anderes Datum.
                </Box>
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


