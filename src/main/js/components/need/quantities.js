import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import React from 'react';
import { Helmet } from 'react-helmet';
import { fetchOwnNeeds } from '../../actions/need';
import { requiresLogin, setWaitingState } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import NeedQuantityEditComponent from './quantity-edit';

const styles = theme => ({
    quantityInput: {
        minWidth: '105px',
        width: '48%',
        margin: '3px',
    },
    quantityWrapper: {
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        flexWrap: 'wrap',
    },
});

@withStyles(styles)
@withSnackbar
class NeedQuantityComponent extends React.Component {

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
        const {classes, sessionState} = this.props;
        fetchOwnNeeds({accessToken:sessionState.accessToken, userId:sessionState.currentUser.id, projectId, startDiff: monthData.startDiff, endDiff: monthData.endDiff}).then(result => {
            callback(
                {
                    ...monthData,
                    days: monthData.days.map(day => {
                        let needs = result.get(day.date.valueOf());
                        if(needs){
                            day.content = (
                                <>
                                    <div className={classes.quantityWrapper}>
                                        <NeedQuantityEditComponent need={needs.get('CONSTRUCTION_WORKER')} label="Bauhelfer"/>
                                        <NeedQuantityEditComponent need={needs.get('KITCHEN_HELPER')} label="Küche"/>
                                    </div>
                                    <div className={classes.quantityWrapper}>
                                        <NeedQuantityEditComponent need={needs.get('STORE_KEEPER')} label="Magaziner"/>
                                        <NeedQuantityEditComponent need={needs.get('DRIVER')} label="Stadtfahrer"/>
                                    </div>
                                    <div className={classes.quantityWrapper}>
                                        <NeedQuantityEditComponent need={needs.get('GATEKEEPER_MORNING')} label="Pforte Vormittag"/>
                                        <NeedQuantityEditComponent need={needs.get('GATEKEEPER_AFTERNOON')} label="Pforte Nachmittag"/>
                                    </div>
                                    <div className={classes.quantityWrapper}>
                                        <NeedQuantityEditComponent need={needs.get('CLEANER')} label="Putzen"/>
                                    </div>
                                </>
                            )
                        }
                        return day;
                    })
                }
            );
        });
    }

    render() {
        const { classes, sessionState } = this.props;
        setWaitingState(false);
        return (
            <>
                <Helmet titleTemplate="%s › Bedarf" />
                <ProjectCalendar loadDayContent={this.getQuantities.bind(this)}/>
            </>
        );
    }
}

export default requiresLogin(NeedQuantityComponent);
