import React from 'react';
import { Helmet } from 'react-helmet';
import { withSnackbar } from 'notistack';
//import { withStyles } from '@material-ui/core/styles';
//import Grid from '@material-ui/core/Grid';
//import TextField from '@material-ui/core/TextField';
//import Button from '@material-ui/core/Button';
//import Typography from '@material-ui/core/Typography';
import NeedsProvider, { NeedsContext } from '../providers/needs-provider';
import { createOrUpdateNeed, applyForNeed, revokeApplicationForNeed, fetchNeed } from '../actions/need';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';
import WithoutPermission from './without-permission';

const NeedQuantity = props => (
    <div style={{ width: '100%' }}>
        <h4>{props.label}</h4>
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
            </>
            Beworben: {typeof props.need.appliedCount === 'number' ? props.need.appliedCount : 'n/a'}<br />
            Genehmigt: {typeof props.need.approvedCount === 'number' ? props.need.approvedCount : 'n/a'}
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

    constructor(props) {
        super(props);
        this.state = {
            openNeedId: null,
        };
    }

    handleCollapseChange(openRequested, need) {
        this.setState({
            openNeedId: openRequested ? need.id : null,
        });
    }

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
                            <ul>
                                {needsState.needs.map((need, i) => (
                                    <li key={need.date.format('x') + need.projectName}>
                                        <h3>{need.date.format('DD.MM.YYYY')}</h3>
                                        <NeedQuantity need={need.CONSTRUCTION_WORKER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Bauhelfer" />
                                        <NeedQuantity need={need.STORE_KEEPER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Magaziner" />
                                        <NeedQuantity need={need.KITCHEN_HELPER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Küche" />
                                        <NeedQuantity need={need.CLEANER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Putzen" />
                                    </li>
                                ))}
                            </ul>
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
export default NeedsComponent;
