import React from 'react';
import { Helmet } from 'react-helmet';
import { LoginContext } from '../providers/login-provider';
import { apiRequest, apiEndpoints } from '../apiclient';

const LoginComponent = () => (
    <LoginContext.Consumer>
        {loginState => (
            <form onSubmit={evt => {
                evt.preventDefault();
                // TODO Actual login logic
            }}>
                <Helmet titleTemplate="Login - %s" />
                <label htmlFor="username">
                    Nutzername
                </label>
                <input type="text" name="username" />
                <br />
                <label htmlFor="password">Passwort</label>
                <input type="password" name="password" />
                <button type="submit">
                    Anmelden
                </button>
            </form>
        )}
    </LoginContext.Consumer>
)

export default LoginComponent;
