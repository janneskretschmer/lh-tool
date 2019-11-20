import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin, setWaitingState } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import TextField from '@material-ui/core/TextField';
import { createOrUpdateNeed, applyForNeed, revokeApplicationForNeed, fetchNeed, fetchOwnNeeds } from '../../actions/need';
import NeedApplyEditComponent from './apply-edit';
import Button from '@material-ui/core/Button';
import { yellow, green } from '@material-ui/core/colors';

const styles = theme => ({
    applyInput: {
        minWidth: '105px',
        width: '48%',
        margin: '3px',
    },
    applyWrapper: {
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexWrap: 'wrap',
    },
    legendItem: {
        alignItems: 'center',
        margin: theme.spacing.unit,
    },
    red: {
        color: '#f00',
    },

    applied: {
        backgroundColor: yellow[600],
    },
    approved: {
        backgroundColor: green[600],
    },
});

@withStyles(styles)
@withSnackbar
class NeedApplyComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            project: null,
            months: null,
            month: 0,
            day: null,
        };
    }

    getQuantities(monthData, projectId, callback) {
        const { classes, sessionState } = this.props
        fetchOwnNeeds({ accessToken: sessionState.accessToken, userId: sessionState.currentUser.id, projectId, startDiff: monthData.startDiff, endDiff: monthData.endDiff }).then(result => {
            callback(
                {
                    ...monthData,
                    days: monthData.days.map(day => {
                        let needs = result[day.date];
                        if (needs) {
                            day.content = (
                                <>
                                    <div className={classes.applyWrapper}>
                                        <NeedApplyEditComponent need={needs.CONSTRUCTION_WORKER} label="Bauhelfer" />
                                        <NeedApplyEditComponent need={needs.STORE_KEEPER} label="Magaziner" />
                                    </div>
                                    <div className={classes.applyWrapper}>
                                        <NeedApplyEditComponent need={needs.KITCHEN_HELPER} label="Küche" />
                                        <NeedApplyEditComponent need={needs.CLEANER} label="Putzen" />
                                    </div>
                                </>
                            )
                        }
                        return day
                    })
                }
            )
        })
    }

    render() {
        const { classes, sessionState } = this.props;
        setWaitingState(false);
        return (
            <>
                <div className={classes.legendItem}>
                    <Button variant="outlined" disabled={true}>Aufgabe</Button> &nbsp;Es besteht noch kein Bedarf an diesem Tag.
                </div>
                <div className={classes.legendItem}>
                    <Button variant="outlined">Aufgabe</Button> &nbsp;Nicht beworben. Bitte klicke auf den jeweiligen Button, um dich zu bewerben.
                </div>

                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.applied}>Aufgabe</Button> &nbsp;Beworben, jedoch noch nicht zugeteilt. Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.approved}>Aufgabe</Button> &nbsp;Zugeteilt,<span className={classes.red}> komme bitte nur dann zur Baustelle</span>, vielen Dank! Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                <br />
                <ProjectCalendar loadDayContent={this.getQuantities.bind(this)} />
            </>
        )
    }
}

export default requiresLogin(NeedApplyComponent);
