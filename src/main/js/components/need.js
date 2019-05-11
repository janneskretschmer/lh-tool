import React from 'react';
import CircularProgress from '@material-ui/core/CircularProgress';
import { createOrUpdateNeed, changeApplicationStateForNeed, fetchNeed, fetchOwnNeeds } from '../actions/need';
import Typography from '@material-ui/core/Typography';
import WithPermission from './with-permission';
import WithoutPermission from './without-permission';
import TextField from '@material-ui/core/TextField';
import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import { withSnackbar } from 'notistack';
import ApplicationList from './approve-need.js'

const styles = theme => ({
  container: {
    minWidth: '200px',
    width: '23%',
    display: 'inline-block',
    verticalAlign: 'top',
    margin: '3px',
    marginBottom: '10px'
  },
  approved: {
    color: theme.palette.primary.main
  },
  applied: {
    color: theme.palette.secondary.main
  },
  apply: {
    marginTop: '3px',
  },
  quantity: {
    display: 'inline-block',
    maxWidth: '155px',
    marginRight: '5px',
  },
});

@withStyles(styles)
@withSnackbar
class Need extends React.Component {
  constructor(props) {
        super(props);
        this.state = {
          data: this.props.need,
          updating: false,
        };
    }

  handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren des Bedarfs', {
            variant: 'error',
        });
    }

    handleQuantityChange(event) {
        this.latestQuantity = parseInt(event.target.value, 10)
        this.updateValue()
    }

    //keeps calling the api, until the quantity is up to date. is "threadsafe"
    updateValue() {
      if(!this.requestInProgress) {
        this.requestInProgress = true;
        this.setState({
            ...this.state,
            updating: true,
          }, () =>
            createOrUpdateNeed({
              need: { ...this.state.data, quantity: this.latestQuantity}, sessionState: this.props.sessionState, handleFailure: this.handleFailure.bind(this)
          }).then(need => this.setState({
              ...this.state,
              data: need,
              updating: false,
            }, function(){
              this.requestInProgress = false;
              if(this.state.data.quantity !== this.latestQuantity) {
                this.updateValue();
              }
            }.bind(this))));
      }
    }

  handleApprove(diff){
    this.setState({
      ...this.state,
      data: {
        ...this.state.data,
        approvedCount: this.state.data.approvedCount + diff,
        appliedCount: this.state.data.appliedCount - diff,
      }
    })
  }

  toggleApplicationStatus(){
    this.setState({
      ...this.state,
      updating: true,
    })
    changeApplicationStateForNeed({
      accessToken: this.props.sessionState.accessToken,
      userId: this.props.sessionState.currentUser.id,
      needId: this.state.data.id,
      state: this.state.data.ownState === 'NONE' ? 'APPLIED' : 'NONE',
      handleFailure: null,
    })
            .then(newNeedUser => {
                this.setState({
                  ...this.state,
                  data: {
                    ...this.state.data,
                    ownState: newNeedUser.state,
                  },
                  updating: false,
                })
            });
    }

  render() {
    const { classes } = this.props;
    return (
      <div className={classes.container}>
        <Typography variant="h6">{this.props.label}</Typography>
            <div>
              { this.state.data ? (
                <>
                  <WithPermission permission="ROLE_RIGHT_NEEDS_POST">
                    <TextField
                          id="need_quantity"
                          label="Bedarf"
                          defaultValue={this.state.data.quantity > 0 ? this.state.data.quantity : undefined}
                          onChange={this.handleQuantityChange.bind(this)}
                          type="number"
                          className={classes.quantity}
                          InputLabelProps={{
                            shrink: true,
                          }}
                      inputProps={{
                            'min': '0',
                            'step': '1',
                          }}
                          margin="dense"
                          variant="outlined"
                        />
                  </WithPermission>
                  <br />
                  <WithoutPermission permission="ROLE_RIGHT_NEEDS_POST">
                    Bedarf: {typeof this.state.data.quantity === 'number' ? this.state.data.quantity : '(kein Bedarf)'}
                      </WithoutPermission>
                      <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                      Genehmigt: {typeof this.state.data.approvedCount === 'number' ? this.state.data.approvedCount : '0'} / {typeof this.state.data.appliedCount === 'number' ? this.state.data.appliedCount + this.state.data.approvedCount : '0'}<br />
                    </WithPermission>
                    {this.props.singleDayMode ? (
                        <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                          <ApplicationList accessToken={this.props.sessionState.accessToken} need={this.props.need} onApprove={this.handleApprove.bind(this)}/>
                        </WithPermission>
                      ): (
                        <>
                        {this.state.data.ownState === 'APPLIED' ? (<>Status: <span className={classes.applied}>Beworben</span></>) : (this.state.data.ownState === 'APPROVED' ? (<>Status: <span className={classes.approved}>Eingeteilt</span></>) : null)}
                        {this.state.updating ? (
                            <>
                              <br />
                              <CircularProgress size={20}/>
                            </>
                        ) : (
                          <Button
                            variant={this.state.data.ownState === 'APPLIED' || this.state.data.ownState === 'APPROVED' ? 'outlined' : 'contained'}
                            disabled={!this.props.sessionState.hasPermission('ROLE_RIGHT_NEEDS_APPLY') || !this.state.data.id || this.state.data.quantity === 0}
                            className={classes.apply}
                            onClick={this.toggleApplicationStatus.bind(this)}>
                            {this.state.data.ownState === 'APPLIED' || this.state.data.ownState === 'APPROVED' ? 'Zurückgnehmen' : 'Bewerben'}
                          </Button>
                      )}
                    </>
                 )}
                  </>
              ) : null}
            </div>
        </div>
    )
  }
}

export default Need;
