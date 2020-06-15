import React from 'react';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';
import { NeedsContext } from '../../providers/needs-provider';

const NeedMonthSelection = props => (
  <NeedsContext.Consumer>
    {needsState => {
      const data = needsState.getSelectedProject().selectedMonthData;
      return (
        <div className={props.className}>
          <IconButton onClick={() => needsState.selectMonth(data.monthOffset - 1)} disabled={!data.isPreviousOffsetValid}>
            <NavigateBeforeIcon />
          </IconButton>
          {props.onClick ? (
            <Button
              onClick={props.onClick}
            >
              {data.monthName}
            </Button>
          ) : data.monthName}
          <IconButton onClick={() => needsState.selectMonth(data.monthOffset + 1)} disabled={!data.isNextOffsetValid}>
            <NavigateNextIcon />
          </IconButton>
        </div>
      )
    }}
  </NeedsContext.Consumer>
);
export default NeedMonthSelection;
