import React from 'react';
import { Helmet } from 'react-helmet';
import { withSnackbar } from 'notistack';
import { withStyles } from '@material-ui/core/styles';
import NeedsProvider, { NeedsContext } from '../providers/needs-provider';
import { createOrUpdateNeed, applyForNeed, revokeApplicationForNeed, fetchNeed, fetchOwnNeeds } from '../actions/need';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';
import WithoutPermission from './without-permission';
import { requiresLogin } from '../util';
import GroupAddIcon from '@material-ui/icons/GroupAdd';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import ClearIcon from '@material-ui/icons/Clear';
import IconButton from '@material-ui/core/IconButton';
import { setWaitingState, getMonthArrayWithOffsets } from '../util';
import ProjectSelection from './project-selection';
import CircularProgress from '@material-ui/core/CircularProgress';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Need from './need';
import Typography from '@material-ui/core/Typography';
import moment from 'moment';

const Date = props => (
  <>{['Dienstag','Mittwoch','Donnerstag','Freitag','Samstag'][props.date.format('E')-2]}, {props.date.format('DD.MM.YYYY')}</>
)

const styles = theme => ({
  projectWrapper: {
  marginRight: '50px',
  display: 'inline',
  },
});

@withStyles(styles)
@withSnackbar
class StatefulNeedsComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            singleDayMode: false,
            project: null,
            months: null,
            month: 0,
            needData: null,
            day: null,
        };
    }

    enterSingleDayMode(day) {
      this.setState({
        ...this.state,
        singleDayMode: true,
        day,
        needData: null,
      },this.loadNeedData);
    }

    leaveSingleDayMode() {
      this.setState({
        ...this.state,
        singleDayMode: false,
        needData: null,
      },this.loadNeedData);
    }

    loadNeedData() {
      const start = this.state.singleDayMode ? this.state.day.diff(moment().startOf('day'),'days') : this.state.months[this.state.month].startOffset;
      const end = this.state.singleDayMode ? this.state.day.diff(moment().startOf('day'),'days') : this.state.months[this.state.month].endOffset;
      fetchOwnNeeds({accessToken:this.props.sessionState.accessToken, userId:this.props.sessionState.currentUser.id, projectId: this.state.project.id, startDiff: start, endDiff: end}).then(result => this.setState({
        ...this.state,
        needData: result,
      }))
    }

    switchProject(project) {
      this.setState({
        ...this.state,
        project,
        months: getMonthArrayWithOffsets(project.startDate,project.endDate),
        month: 0,
        needData: null,
      },this.loadNeedData);
    }

    handleMonthSelect(event) {
      this.setState({
        ...this.state,
        month:event.target.value,
        needData: null,
      },this.loadNeedData)
    }

    canGoLeft() {
      return this.state.singleDayMode ? this.state.day > this.state.project.startDate : this.state.month > 0;
    }

    canGoRight() {
      return this.state.singleDayMode ? this.state.day < this.state.project.endDate : this.state.months && this.state.month < this.state.months.length - 1;
    }

    handleLeft() {
      this.setState({
        ...this.state,
        needData: null,
        month: this.state.month - (this.state.singleDayMode ? 0 : 1),
        day: this.state.singleDayMode ? this.state.day.add(this.state.day.format('E') === '2'?-3:-1, 'days') : null,
      },this.loadNeedData)
    }

    handleRight() {
      this.setState({
        ...this.state,
        needData: null,
        month: this.state.month + (this.state.singleDayMode ? 0 : 1),
        day: this.state.singleDayMode ? this.state.day.add(this.state.day.format('E') === '6'?3:1, 'days') : null,
      },this.loadNeedData)
    }

    render() {
        const { classes, sessionState } = this.props;
        const { singleDayMode } = this.state;
        const tmp = (
          <>
            <div className={classes.projectWrapper}>
                <ProjectSelection  onChange={project => this.switchProject(project)} accessToken={sessionState.accessToken} />
              </div>
              {this.state.project ? (
                  <>
                    {this.canGoLeft() ? (
                      <IconButton onClick={this.handleLeft.bind(this)}>
                                <NavigateBeforeIcon />
                            </IconButton>
                    ) : null}
                    {this.state.singleDayMode ? (
                      <>
                        <Date date={this.state.day}/>
                        <IconButton onClick={this.leaveSingleDayMode.bind(this)}>
                              <ClearIcon />
                          </IconButton>
                          </>
                    ) : (
                      <Select value={this.state.month} onChange={this.handleMonthSelect.bind(this)}>
                        {this.state.months.map((month,i) => (
                            <MenuItem key={i} value={i}>{
                              ["Januar","Februar","Maerz","April","Mai","Juni","Juli","August","September","Oktober","November","Dezember"][month.month - 1]
                            }</MenuItem>
                        ))}
                      </Select>
                    )}
                    {this.canGoRight() ? (
                      <IconButton onClick={this.handleRight.bind(this)}>
                                <NavigateNextIcon />
                            </IconButton>
                    ) : null}
                    {this.state.needData ? this.state.needData.map((need, i) => (
                              <div style={{ borderBottom: (need.date.format('E') === '6' ? '3' : '1') + 'px solid #e0e0e0', marginTop:'10px' }} key={i}>
                                  {!singleDayMode ? (
                                    <Typography variant="h5">
                                      <Date date={need.date}/>
                                      <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                                      <IconButton onClick={() => this.enterSingleDayMode(need.date)}>
                                          <GroupAddIcon />
                                        </IconButton>
                                      </WithPermission>
                                    </Typography>
                                  ) : null}
                                  <Need sessionState={sessionState} need={need.CONSTRUCTION_WORKER} singleDayMode={this.state.singleDayMode} label="Bauhelfer" />
                                  <Need sessionState={sessionState} need={need.STORE_KEEPER} singleDayMode={this.state.singleDayMode} label="Magaziner" />
                                  <Need sessionState={sessionState} need={need.KITCHEN_HELPER} singleDayMode={this.state.singleDayMode} label="Küche" />
                                  <Need sessionState={sessionState} need={need.CLEANER} singleDayMode={this.state.singleDayMode} label="Putzen" />
                              </div>
                      )) : (
                        <CircularProgress/>
                      )}

                  </>
                ):(
                  <>
                    <CircularProgress size={15}/>
                  </>
                )}
             </>
        );
        setWaitingState(false);
        return tmp;
    }
}

const NeedsComponent = props => (
    <>
        <Helmet titleTemplate="Helfer - %s" />
        <NeedsProvider>
          <SessionContext.Consumer>
            {sessionState => (
                <StatefulNeedsComponent sessionState={sessionState} {...props} />
                )}
            </SessionContext.Consumer>
        </NeedsProvider>
    </>
);
export default requiresLogin(NeedsComponent);
