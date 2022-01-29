import CircularProgress from '@mui/material/CircularProgress';
import { Box } from '@mui/system';
import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { convertToYYYYMMDD, requiresLogin } from '../../util';
import NeedMonthSelection from './need-month-selection';
import NeedProjectSelection from './need-project-selection';

class StatefulNeedProjectCalendar extends React.Component {

    setMonth(monthOffset) {
        this.props.needsState.setMonth(monthOffset);
    }


    render() {
        const { classes, sessionState, needsState, dateContentMap } = this.props;
        const project = needsState.getSelectedProject();
        const data = project && project.selectedMonthData;

        const sxDayName = {
            p: 1,
            color: 'secondary.dark',
            border: '1px solid',
            borderColor: 'primary.light',
            fontWeight: 'normal',
            fontSize: 'large',
        };

        return (
            <>
                <Box sx={{
                    display: 'flex',
                    position: 'relative',
                    height: '48px',
                    alignItems: 'center',
                }}>
                    <Box sx={{
                        justifySelf: 'start',
                    }}>
                        <NeedProjectSelection />
                    </Box>
                    {data ? (
                        <NeedMonthSelection sx={{
                            justifySelf: 'center',
                            position: 'absolute',
                            left: '50%',
                            transform: 'translate(-50%,0)',
                            alignItems: 'center',
                            display: 'flex',
                            fontSize: 'x-large',
                        }} />
                    ) : (
                        <CircularProgress size={15} />
                    )}

                </Box>
                {data && (
                    <Box component="table" sx={{
                        tableLayout: 'fixed',
                        width: '100%',
                        borderCollapse: 'collapse',
                        minWidth: '920px',
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
                        <tbody>
                            {Array.from(Array(data.days.length / 7)).map((_, i) => (
                                <tr key={i}>
                                    {Array.from(Array(7)).map((_, j) => {
                                        const day = data.days[i * 7 + j];
                                        const content = dateContentMap && dateContentMap.get(convertToYYYYMMDD(day.date));
                                        return (
                                            <Box
                                                component="td"
                                                sx={{
                                                    width: '20%',
                                                    border: '1px solid',
                                                    borderColor: 'primary.light',
                                                    p: 1,
                                                    verticalAlign: 'top',
                                                    opacity: day.disabled ? '0.38' : undefined
                                                }} key={j}>
                                                <Box sx={{
                                                    width: '100%',
                                                    textAlign: 'right',
                                                    color: 'secondary.main',
                                                    fontWeight: 'bold',
                                                    fontSize: 'larger',
                                                    padding: '3px',
                                                }}>
                                                    {day.date.getDate()}
                                                </Box>
                                                {content ? content : !day.disabled ? (<CircularProgress size={15} />) : null}
                                            </Box>
                                        );
                                    })}
                                </tr>
                            ))}
                        </tbody>
                    </Box>
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
