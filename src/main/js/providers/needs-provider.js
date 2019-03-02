import React from 'react';
import { resolve } from 'react-resolver';
import { SessionContext } from './session-provider';
import { fetchOwnNeeds } from '../actions/need';
import { withContext } from '../util';
import moment from 'moment';

export const NeedsContext = React.createContext();

@withContext('sessionState', SessionContext)
@resolve('initialNeedData', props => {
    return fetchOwnNeeds({ accessToken: props.sessionState.accessToken })
        .catch(e => console.log(e));
})
export default class NeedsProvider extends React.Component {

    state = {
        needs: this.props.initialNeedData,
    };

    needsUpdated = need => {
        this.setState(prevState => ({
            needs: prevState.needs.map(tmp => {
                if(tmp.date.isSame(moment(need.date,'x')) && tmp.projectId === need.projectId) {
                    tmp[need.helperType] = need;
                }
                return tmp;
            })
        }));
    };

    render() {
        return (
            <NeedsContext.Provider
                value={{
                    ...this.state,
                    needsAdded: this.needsAdded,
                    needsUpdated: this.needsUpdated,
                }}
            >
                {this.props.children}
            </NeedsContext.Provider>
        );
    }
}
