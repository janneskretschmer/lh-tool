import Button from '@material-ui/core/Button';
import { green, red, yellow } from '@material-ui/core/colors';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Helmet } from 'react-helmet';
import { fetchOwnNeeds } from '../../actions/need';
import { requiresLogin, setWaitingState } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import NeedApplyEditComponent from './apply-edit';

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
    rejected: {
        backgroundColor: red[600],
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
        const { classes, sessionState } = this.props;
        fetchOwnNeeds({ accessToken: sessionState.accessToken, userId: sessionState.currentUser.id, projectId, startDiff: monthData.startDiff, endDiff: monthData.endDiff }).then(result => {
            callback(
                {
                    ...monthData,
                    days: monthData.days.map(day => {
                        let needs = result.get(day.date.valueOf());
                        if (needs) {
                            day.content = (
                                <>
                                    <div className={classes.applyWrapper}>
                                        <NeedApplyEditComponent need={needs.get('CONSTRUCTION_WORKER')} label="Bauhelfer" />
                                        <NeedApplyEditComponent need={needs.get('KITCHEN_HELPER')} label="Küche" />
                                    </div>
                                    <div className={classes.applyWrapper}>
                                        <NeedApplyEditComponent need={needs.get('STORE_KEEPER')} label="Magaziner" />
                                        <NeedApplyEditComponent need={needs.get('DRIVER')} label="Stadtfahrer" />
                                    </div>
                                    <div className={classes.applyWrapper}>
                                        <NeedApplyEditComponent need={needs.get('GATEKEEPER_MORNING')} label="Pforte Vormittag" />
                                        <NeedApplyEditComponent need={needs.get('GATEKEEPER_AFTERNOON')} label="Pforte Nachmittag" />
                                    </div>
                                    <div className={classes.applyWrapper}>
                                        <NeedApplyEditComponent need={needs.get('CLEANER')} label="Putzen" />
                                    </div>
                                </>
                            );
                        }
                        return day;
                    }),
                }
            );
        });
    }

    render() {
        const { classes, sessionState } = this.props;
        setWaitingState(false);
        return (
            <>
                <Helmet titleTemplate="%s › Bewerben" />
                <div className={classes.legendItem}>
                    <Button variant="outlined" disabled={true}>Aufgabe Bewerberanzahl/Bedarf</Button> &nbsp;Es besteht kein Bedarf an diesem Tag.
                </div>
                <div className={classes.legendItem}>
                    <Button variant="outlined">Aufgabe Bewerberanzahl/Bedarf</Button> &nbsp;Nicht beworben. Bitte klicke auf den jeweiligen Button, um dich zu bewerben.
                </div>

                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.applied}>Aufgabe Bewerberanzahl/Bedarf</Button> &nbsp;Beworben, jedoch noch nicht zugeteilt. Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.approved}>Aufgabe Bewerberanzahl/Bedarf</Button> &nbsp;Zugeteilt,<span className={classes.red}> komme bitte nur dann zur Baustelle</span>, vielen Dank! Du kannst die Bewerbung zurückziehen, indem du auf den Button klickst.
                </div>
                <div className={classes.legendItem}>
                    <Button variant="contained" className={classes.rejected}>Aufgabe Bewerberanzahl/Bedarf</Button> &nbsp;Nicht zugeteilt, bitte bewerbe dich für ein anderes Datum.
                </div>
                <br />
                <ProjectCalendar loadDayContent={this.getQuantities.bind(this)} />
            </>
        );
    }
}

export default requiresLogin(NeedApplyComponent);
