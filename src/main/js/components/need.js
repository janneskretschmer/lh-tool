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
import IconButton from '@material-ui/core/IconButton';
import ApplicationList from './approve-need.js'

const NeedQuantity = props => (
    <div style={{ minWidth: '200px', width: '23%', display: 'inline-block', verticalAlign: 'top', margin: '3px', marginBottom: '20px' }}>
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
                <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
                	{props.showApplications ? (<ApplicationList accessToken={props.accessToken} need={props.need} />) : null}
              	</WithPermission>
                <WithoutPermission permission="ROLE_RIGHT_NEEDS_POST">
                    {typeof props.need.quantity === 'number' ? props.need.quantity : '(kein Bedarf)'}
                </WithoutPermission>
                <br/>
            </>
            <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
            	#Beworben: {typeof props.need.appliedCount === 'number' ? props.need.appliedCount : 'n/a'}<br />
            	#Genehmigt: {typeof props.need.approvedCount === 'number' ? props.need.approvedCount : 'n/a'}<br />
            </WithPermission>
            Status: {props.need.ownState === 'APPLIED' ? 'Beworben' : (props.need.ownState === 'APPROVED' ? (<span style={{color: '#0A0'}}>Eingeteilt</span>) : 'Nicht Beworben') /*TODO get color from theme*/}
        </div>

        <SessionContext.Consumer>
            {sessionState => (
                <NeedsContext.Consumer>
                    {needsState => (
                        <button
                            disabled={!sessionState.hasPermission('ROLE_RIGHT_NEEDS_APPLY') || !props.need.id || props.need.quantity === 0}
                            onClick={() => {
                                // TODO Proper error message
                                (
                                    props.need.ownState === 'APPLIED'
                                        ? revokeApplicationForNeed({
                                            sessionState,
                                            needId: props.need.id,
                                            handleFailure: err => console.log(err)
                                        })
                                        : applyForNeed({
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
                            {props.need.ownState === 'APPLIED' ? 'Bewerbung zurücknehmen' : 'Bewerben'}
                        </button>
                    )}
                </NeedsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </div >
)

@withSnackbar
class StatefulNeedsComponent extends React.Component {
    
    handleQuantityChange(accessToken, need, needsState, sessionState) {
        createOrUpdateNeed({
            accessToken, need, needsState, sessionState, handleFailure: this.handleFailure.bind(this)
        });
    }

    handleFailure() {
        this.props.enqueueSnackbar('Fehler beim Aktualisieren des Bedarfs', {
            variant: 'error',
        });
    }

    render() {
        const { classes } = this.props;

        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <NeedsContext.Consumer>
                        {needsState => (
                        	<>
	                            {needsState.needs.map((need, i) => (
	                            	<>
		                                <div style={{ fontWeight: 'bold' }}>{need.date.format('DD.MM.YYYY')}</div>
		                                <NeedQuantity need={need.CONSTRUCTION_WORKER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} showApplications={true} classes={classes} label="Bauhelfer" />
		                                <NeedQuantity need={need.STORE_KEEPER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} showApplications={true} classes={classes} label="Magaziner" />
		                                <NeedQuantity need={need.KITCHEN_HELPER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} showApplications={true} classes={classes} label="Küche" />
		                                <NeedQuantity need={need.CLEANER} accessToken={sessionState.accessToken} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} showApplications={true} classes={classes} label="Putzen" />
		                            </>
	                            ))}
	                            <button onClick={() => {needsState.loadNeeds(30)}}>Weiteren Monat laden</button>
	                        </>
                        )}
                    </NeedsContext.Consumer>
                )}
            </SessionContext.Consumer>
        );
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

