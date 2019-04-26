import React from 'react';
import moment from 'moment';
import { resolve } from 'react-resolver';
import { SessionContext } from './session-provider';
import { fetchOwnNeeds } from '../actions/need';
import { withContext } from '../util';

export const NeedsContext = React.createContext();

@withContext('sessionState', SessionContext)
@resolve('initialNeedData', props => {
    if (props.sessionState.isLoggedIn()) {
        return fetchOwnNeeds({ accessToken: props.sessionState.accessToken, userId: props.sessionState.currentUser.id, startDiff: 0, endDiff: 14 })
            .catch(e => console.log(e));
    }
})
export default class NeedsProvider extends React.Component {

    state = {
        needs: this.props.initialNeedData || [],
        startDiff: 0,
        endDiff: 14,
    };

    needsUpdated = newNeed => {
        this.setState(prevState => ({
            needs: prevState.needs.map(need => {
                if (need.date.isSame(moment(newNeed.date, 'x'), 'day') && need.projectId === newNeed.projectId) {
                    need[newNeed.helperType] = newNeed;
                }
                return need;
            })
        }));
    };
    
    loadNeeds = (startDiff,endDiff) => {
    	let self = this;
    	fetchOwnNeeds({ accessToken: this.props.sessionState.accessToken, userId: this.props.sessionState.currentUser.id, startDiff, endDiff })
    	.then(result => {
    		self.setState(prevState => ({
        		needs: result,
        		startDiff,
        		endDiff,
        	}));
    	})
    }

    render() {
        return (
            <NeedsContext.Provider
                value={{
                    ...this.state,
                    needsAdded: this.needsAdded,
                    needsUpdated: this.needsUpdated,
                    loadNeeds: this.loadNeeds,
                }}
            >
                {this.props.children}
            </NeedsContext.Provider>
        );
    }
}
