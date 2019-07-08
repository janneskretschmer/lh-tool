import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin, setWaitingState } from '../../util';
import ProjectCalendar from '../util/project-calendar';
import TextField from '@material-ui/core/TextField';
import { createOrUpdateNeed, applyForNeed, revokeApplicationForNeed, fetchNeed, fetchOwnNeeds } from '../../actions/need';
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
        const {classes, sessionState} = this.props
        fetchOwnNeeds({accessToken:sessionState.accessToken, userId:sessionState.currentUser.id, projectId, startDiff: monthData.startDiff, endDiff: monthData.endDiff}).then(result => {
            callback(
                {
                    ...monthData,
                    days: monthData.days.map(day => {
                        let needs = result[day.date];
                        if(needs){
                            day.content = (
                                <>
                                    <div className={classes.quantityWrapper}>
                                        <NeedQuantityEditComponent need={needs.CONSTRUCTION_WORKER} label="Bauhelfer"/>
                                        <NeedQuantityEditComponent need={needs.STORE_KEEPER} label="Magaziner"/>
                                    </div>
                                    <div className={classes.quantityWrapper}>
                                        <NeedQuantityEditComponent need={needs.KITCHEN_HELPER} label="Küche"/>
                                        <NeedQuantityEditComponent need={needs.CLEANER} label="Putzen"/>
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
                <ProjectCalendar loadDayContent={this.getQuantities.bind(this)}/>
            </>
        )
    }
}

export default requiresLogin(NeedQuantityComponent);
