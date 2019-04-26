import React from 'react';
import { Helmet } from 'react-helmet';
import { withSnackbar } from 'notistack';
import { withStyles } from '@material-ui/core/styles';
import NeedsProvider, { NeedsContext } from '../providers/needs-provider';
import { createOrUpdateNeed, applyForNeed, revokeApplicationForNeed, fetchNeed } from '../actions/need';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';
import WithoutPermission from './without-permission';
import { requiresLogin } from '../util';
import GroupAddIcon from '@material-ui/icons/GroupAdd';
import NavigateBeforeIcon from '@material-ui/icons/NavigateBefore';
import NavigateNextIcon from '@material-ui/icons/NavigateNext';
import IconButton from '@material-ui/core/IconButton';
import ApplicationList from './approve-need.js'
import { setWaitingState } from '../util';

const NeedQuantity = props => (
    <div style={{ minWidth: '200px', width: '23%', display: 'inline-block', verticalAlign: 'top', margin: '3px', marginBottom: '10px' }}>
        <div style={{ fontWeight: 'bold' }}>{props.label}</div>
        <div>
            <>
                <>Bedarf:&nbsp;</>
                <WithPermission permission="ROLE_RIGHT_NEEDS_POST">
                    <input
                        id={props.need.date + props.need.projectName + props.label}
                        value={props.need.quantity}
                        style={{ width: '80px' }}
                        type="number"
                        onChange={
                            e => props.onChange({
                                ...props.need,
                                quantity: parseInt(e.target.value, 10),
                            })
                        }
                    />
                </WithPermission>
                <WithoutPermission permission="ROLE_RIGHT_NEEDS_POST">
                    {typeof props.need.quantity === 'number' ? props.need.quantity : '(kein Bedarf)'}
                </WithoutPermission>
                <br/>
                <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
	            	#Beworben: {typeof props.need.appliedCount === 'number' ? props.need.appliedCount : 'n/a'}<br />
	            	#Genehmigt: {typeof props.need.approvedCount === 'number' ? props.need.approvedCount : 'n/a'}<br />
	            </WithPermission>
                {props.singleDayMode ? (
                	<WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
    	            	<ApplicationList accessToken={props.accessToken} need={props.need} />
    	          	</WithPermission>
                ): (
                	<>
	                	Status: {props.need.ownState === 'APPLIED' ? 'Beworben' : (props.need.ownState === 'APPROVED' ? (<span style={{color: '#673ab7'}}>Eingeteilt</span>) : 'Nicht Beworben') /*
	    						 * TODO
	    						 * get
	    						 * color
	    						 * from
	    						 * theme
	    						 */}
	                	<SessionContext.Consumer>
		                    {sessionState => (
		                        <NeedsContext.Consumer>
		                            {needsState => (
		                                <button
		                                    disabled={!sessionState.hasPermission('ROLE_RIGHT_NEEDS_APPLY') || !props.need.id || props.need.quantity === 0}
		                                    onClick={() => {
		                                        // TODO Proper error message
		                                        (
		                                            props.need.ownState === 'NONE'
		                                                ? applyForNeed({
		                                                    sessionState,
		                                                    needId: props.need.id,
		                                                    handleFailure: err => console.log(err)
		                                                })
		                                                : revokeApplicationForNeed({
		                                                    sessionState,
		                                                    needId: props.need.id,
		                                                    handleFailure: err => console.log(err)
		                                                })
		                                        )
		                                            .then(newNeedUser => {
		                                                return fetchNeed({
		                                                    accessToken: sessionState.accessToken,
		                                                    needId: newNeedUser.needId,
		                                                    userId: sessionState.currentUser.id,
		                                                });
		                                            })
		                                            .then(need => {
		                                                needsState.needsUpdated(need);
		                                            });
		                                    }}>
		                                    {props.need.ownState === 'APPLIED' || props.need.ownState === 'APPROVED' ? 'Bewerbung zurücknehmen' : 'Bewerben'}
		                                </button>
		                            )}
		                        </NeedsContext.Consumer>
		                    )}
		                </SessionContext.Consumer>
                	</>
                )}
                
            </>
            
        </div>
     </div>
)

@withSnackbar
class StatefulNeedsComponent extends React.Component {
	
    constructor(props) {
        super(props);
        this.state = {
            singleDayMode: false,
        };
    }
                		
    handleQuantityChange(accessToken, need, needsState, sessionState) {
		if (this.changeThrottleTimeout) {
			clearTimeout(this.changeThrottleTimeout);
		}

		this.changeThrottleTimeout = setTimeout(() => {
			createOrUpdateNeed({
				accessToken, need, needsState, sessionState, handleFailure: this.handleFailure.bind(this)
			});
		}, 200);
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren des Bedarfs', {
            variant: 'error',
        });
    }
    
    enterSingleDayMode(needsState, day) {
    	this.setState({
    		singleDayMode: true
    	});
    	needsState.loadNeeds(day, day);
    }

    render() {
        const { classes } = this.props;
        const { singleDayMode } = this.state;
        const tmp = (
            <SessionContext.Consumer>
                {sessionState => (
                    <NeedsContext.Consumer>
                        {needsState => (
                        	<>
	                            {needsState.needs && needsState.needs.length > 0 ? needsState.needs.map((need, i) => (
	                            	<div style={{ borderBottom: (need.date.format('E') === '6' ? '3' : '1') + 'px solid #e0e0e0', marginTop:'10px' }} key={i}>
		                                <div style={{ fontWeight: 'bold', textAlign: singleDayMode ? 'center' : 'left'}}>
		                                	{singleDayMode ? (
		                                		<IconButton onClick={() => {let diff = needsState.startDiff-(need.date.format('E') === '2'?3:1); needsState.loadNeeds(diff,diff);}}>
		                                            <NavigateBeforeIcon />
		                                        </IconButton>	
		                                	) : null}
		                                	{new Array('Dienstag','Mittwoch','Donnerstag','Freitag','Samstag')[need.date.format('E')-2]}, {need.date.format('DD.MM.YYYY')}
		                                	{singleDayMode ? (
		                                		<IconButton onClick={() => {let diff = needsState.startDiff+(need.date.format('E') === '6'?3:1); needsState.loadNeeds(diff,diff);}}>
		                                            <NavigateNextIcon />
		                                        </IconButton>	
		                                	) : (
		                                		<WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
			                                		<IconButton onClick={() => this.enterSingleDayMode(needsState, i)}>
		                                				<GroupAddIcon />
		                                			</IconButton>
		                                		</WithPermission>
		                                	)}
		                                </div>
		                                <NeedQuantity need={need.CONSTRUCTION_WORKER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} singleDayMode={singleDayMode} classes={classes} label="Bauhelfer" />
		                                <NeedQuantity need={need.STORE_KEEPER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} singleDayMode={singleDayMode} classes={classes} label="Magaziner" />
		                                <NeedQuantity need={need.KITCHEN_HELPER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} singleDayMode={singleDayMode} classes={classes} label="Küche" />
		                                <NeedQuantity need={need.CLEANER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} singleDayMode={singleDayMode} classes={classes} label="Putzen" />
		                            </div>
	                            )) : 'Es besteht kein Bedarf an Helfern in den nächsten ' + needsState.endDiff + ' Tagen. Bitte klicke auf den folgenden Button, um weitere 30 Tage zu laden:'}
	                            <br />
	                            <button onClick={() => {needsState.loadNeeds(needsState.startDiff,needsState.endDiff + 30)}}>Weiteren Monat laden</button>
	                        </>
                        )}
                    </NeedsContext.Consumer>
                )}
            </SessionContext.Consumer>
        );
        setWaitingState(false);
        return tmp;
    }
}

const NeedsComponent = props => (
    <>
        <Helmet titleTemplate="Helfer - %s" />
        <NeedsProvider>
            <StatefulNeedsComponent {...props} />
        </NeedsProvider>
    </>
);
export default requiresLogin(NeedsComponent);

