import React from 'react';

const DEFAULT_STATE = {
    accessToken: null,
};

export const LoginContext = React.createContext(DEFAULT_STATE);

export default class LoginProvider extends React.Component {
    state = DEFAULT_STATE;

    accessTokenChanged = accessToken => {
        this.setState({ accessToken });
    };

    isLoggedIn = () => !!this.state.accessToken;

    render() {
        return (
            <LoginContext.Provider
                value={{
                    ...this.state,
                    accessTokenChanged: this.accessTokenChanged,
                    isLoggedIn: this.isLoggedIn,
                }}
            >
                {this.props.children}
            </LoginContext.Provider>
        );
    }
}