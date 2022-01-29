import React from 'react';
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import { NeedsContext } from '../../providers/needs-provider';
import { Box } from '@mui/system';

const NeedMonthSelection = props => (
  <NeedsContext.Consumer>
    {needsState => {
      const data = needsState.getSelectedProject().selectedMonthData;
      return (
        <Box sx={props.sx}>
          <IconButton
            onClick={() => needsState.selectMonth(data.monthOffset - 1)}
            disabled={!data.isPreviousOffsetValid}
            size="large">
            <NavigateBeforeIcon />
          </IconButton>
          {props.onClick ? (
            <Button
              onClick={props.onClick}
            >
              {data.monthName}
            </Button>
          ) : data.monthName}
          <IconButton
            onClick={() => needsState.selectMonth(data.monthOffset + 1)}
            disabled={!data.isNextOffsetValid}
            size="large">
            <NavigateNextIcon />
          </IconButton>
        </Box>
      );
    }}
  </NeedsContext.Consumer>
);
export default NeedMonthSelection;
