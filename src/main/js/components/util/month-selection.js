import React from 'react';
import { OldProjectsContext } from '../../providers/projects-provider.old';
import { isMonthOffsetWithinRange } from '../../util';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';

class StatefulMonthSelection extends React.Component {

  setMonth(month) {
    this.props.projectsState.setMonth(month);
  }

  render() {
    const { onClick, projectsState, className } = this.props;
    const data = projectsState.selectedMonthCalendarData;
    const project = projectsState.getSelectedProject();
    return (
      <>
        <div className={className}>
          <IconButton onClick={() => this.setMonth(data.monthOffset - 1)} disabled={!isMonthOffsetWithinRange(data.monthOffset - 1, project.startDate, project.endDate)}>
            <NavigateBeforeIcon />
          </IconButton>
          {onClick ? (
            <Button
              onClick={onClick}
            >
              {data.monthName}
            </Button>
          ) : data.monthName}
          <IconButton onClick={() => this.setMonth(data.monthOffset + 1)} disabled={!isMonthOffsetWithinRange(data.monthOffset + 1, project.startDate, project.endDate)}>
            <NavigateNextIcon />
          </IconButton>
        </div>
      </>
    );
  }
}

const MonthSelection = props => (
  <>
    <OldProjectsContext.Consumer>
      {projectsState => (
        (<StatefulMonthSelection {...props} projectsState={projectsState} />)
      )}
    </OldProjectsContext.Consumer>
  </>
);
export default MonthSelection;
