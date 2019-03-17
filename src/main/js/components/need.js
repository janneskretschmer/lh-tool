import React from 'react';
import { Helmet } from 'react-helmet';
import { withSnackbar } from 'notistack';
import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import NeedsProvider, { NeedsContext } from '../providers/needs-provider';
import { createOrUpdateNeed, applyForNeed, revokeApplicationForNeed, fetchNeed } from '../actions/need';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';
import WithoutPermission from './without-permission';
import { requiresLogin } from '../util';

const styles = theme => ({
    root: {
        width: '100%',
        backgroundColor: theme.palette.background.paper,
    },
    stripe: {
        backgroundColor: theme.palette.grey[200],
    },
    date: {
        marginLeft: theme.spacing.unit,
    },
    quantity: {
        width: '80px',
    },
    padded: {
        padding: theme.spacing.unit,
    }
});

const NeedQuantity = props => (
    <Grid item container md={2} sm={3} xs={6}>
        <Grid item>
            <WithPermission permission="ROLE_RIGHT_NEEDS_POST">
                <TextField
                    id={props.need.date + props.need.projectName + props.label}
                    label={props.label}
                    defaultValue={props.need.quantity}
                    className={props.classes.quantity}
                    type="number"
                    margin="dense"
                    variant="outlined"
                    onChange={
                        e => props.onChange({
                            ...props.need,
                            quantity: e.target.value,
                        })
                    }
                />
            </WithPermission>
            <WithoutPermission permission="ROLE_RIGHT_NEEDS_POST">
                <Typography variant="overline" gutterBottom>
                    {props.label}
                </Typography>
            </WithoutPermission>
            <Grid item className={props.classes.padded}>
                Bedarf: {typeof props.need.quantity === 'number' ? props.need.quantity : '(kein Bedarf)'}<br />
                Beworben: {typeof props.need.appliedCount === 'number' ? props.need.appliedCount : 'n/a'}<br />
                Genehmigt: {typeof props.need.approvedCount === 'number' ? props.need.approvedCount : 'n/a'}
            </Grid>

            <SessionContext.Consumer>
                {sessionState => (
                    <NeedsContext.Consumer>
                        {needsState => (
                            <Button
                                variant="contained"
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
                            </Button>
                        )}
                    </NeedsContext.Consumer>
                )}
            </SessionContext.Consumer>
        </Grid>
    </Grid>
)

@withSnackbar
@withStyles(styles)
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
                            <Grid container>
                                {needsState.needs.map((need, i) => (
                                    <Grid item alignItems="center" className={i % 2 === 0 ? classes.stripe : null} container xs={12} key={need.date.format('x') + need.projectName}>
                                        <Grid className={classes.date} item md={2} xs={11}>{need.date.format('DD.MM.YYYY')}</Grid>
                                        <NeedQuantity need={need.CONSTRUCTION_WORKER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Bauhelfer" />
                                        <NeedQuantity need={need.STORE_KEEPER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Magaziner" />
                                        <NeedQuantity need={need.KITCHEN_HELPER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Küche" />
                                        <NeedQuantity need={need.CLEANER} onChange={need => this.handleQuantityChange(sessionState.accessToken, need, needsState, sessionState)} classes={classes} label="Putzen" />
                                        <Grid item xs={1} md={2}></Grid>
                                    </Grid>
                                ))}
                            </Grid>
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

