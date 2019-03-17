import React from 'react';
import { Redirect } from 'react-router'
import { SessionContext } from './providers/session-provider';
import { fullPathOfLogin } from './paths';

export function wrapComponent(Component, additionalProps) {
    return props => (<Component {...props} {...additionalProps} />);
}

export function withContext(propName, Context) {
    return function withContextDecorator(Component) {
        return props => (
            <Context.Consumer>
                {contextState => {
                    const WrappedContext = wrapComponent(Component, { [propName]: contextState });
                    return (<WrappedContext {...props}>{props.children}</WrappedContext>);
                }}
            </Context.Consumer>
        );
    };
}

export function requiresLogin(Component) {
    // FUTURE: As soon as isomorphic rendering is employed the redirect needs to be bubbled up
    return props => (
        <SessionContext.Consumer>
            {sessionState => sessionState.isLoggedIn()
                ? <Component {...props} />
                : <Redirect to={fullPathOfLogin()} />
            }
        </SessionContext.Consumer>
    );
}

