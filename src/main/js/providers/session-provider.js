import React from 'react';
import { resolve } from 'react-resolver';
import jsonwebtoken from 'jsonwebtoken';
import { fetchCurrentUser } from '../actions/user';

export const SessionContext = React.createContext();

function getCurrentUserState(accessToken) {
    if (accessToken) {
        const decodedToken = jsonwebtoken.decode(accessToken);
        const permissions = decodedToken ? decodedToken.permissions : null;
        return fetchCurrentUser({ accessToken })
            .then(userInfo => ({ accessToken, userInfo, permissions }))
            .catch(() => ({ accessToken, permissions }));
    } else {
        return Promise.resolve({});
    }
}

@resolve('initialUserData', () => {
    const accessToken = __GLOBAL_CONFIG__.accessToken;
    return getCurrentUserState(accessToken);
})
export default class SessionProvider extends React.Component {

    state = {
        accessToken: this.props.initialUserData.accessToken,
        currentUser: this.props.initialUserData.userInfo,
        permissions: this.props.initialUserData.permissions,
    };

    accessTokenChanged = accessToken => {
        getCurrentUserState(accessToken)
            .then(newState => this.setState({
                accessToken: newState.accessToken ? newState.accessToken : null,
                currentUser: newState.userInfo,
                permissions: newState.permissions,
            }));
    };

    isLoggedIn = () => !!this.state.currentUser;

    hasPermission = permission => this.state.permissions ? this.state.permissions.includes(permission) : false;

    render() {
        return (
            <SessionContext.Provider
                value={{
                    ...this.state,
                    accessTokenChanged: this.accessTokenChanged,
                    isLoggedIn: this.isLoggedIn,
                    hasPermission: this.hasPermission,
                }}
            >
                {this.props.children}
            </SessionContext.Provider>
        );
    }
}