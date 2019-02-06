import React from 'react';
import { resolve } from 'react-resolver';
import { fetchCurrentUser } from '../actions/user';

const ACCTOKEN_KEY = 'acctoken';

export const SessionContext = React.createContext();

function getCurrentUserState(accessToken) {
    if (accessToken) {
        return fetchCurrentUser({ accessToken })
            .then(userInfo => ({ accessToken, userInfo }))
            .catch(() => ({ accessToken }));
    } else {
        return Promise.resolve({});
    }
}

@resolve('initialUserData', () => {
    // For the time being, a naive sessionStorage implementation is sufficient
    // FUTURE: needs to change as soon as isomorphice rendering is employed
    const accessToken = sessionStorage.getItem(ACCTOKEN_KEY);
    return getCurrentUserState(accessToken);
})
export default class SessionProvider extends React.Component {

    state = {
        accessToken: this.props.initialUserData.accessToken,
        currentUser: this.props.initialUserData.userInfo,
    };

    accessTokenChanged = accessToken => {
        if (accessToken) {
            sessionStorage.setItem(ACCTOKEN_KEY, accessToken);
        } else {
            sessionStorage.removeItem(ACCTOKEN_KEY);
        }
        getCurrentUserState(accessToken)
            .then(newState => this.setState({
                accessToken: newState.accessToken ? newState.accessToken : null,
                currentUser: newState.userInfo,
            }));
    };

    isLoggedIn = () => !!this.state.currentUser;

    render() {
        return (
            <SessionContext.Provider
                value={{
                    ...this.state,
                    accessTokenChanged: this.accessTokenChanged,
                    isLoggedIn: this.isLoggedIn,
                }}
            >
                {this.props.children}
            </SessionContext.Provider>
        );
    }
}