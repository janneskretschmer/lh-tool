import React from 'react';
import { Helmet } from 'react-helmet';
import { LoginContext } from '../providers/login-provider';
import { login } from '../actions/login';

class LoginComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return (
            <LoginContext.Consumer>
                {loginState => (
                    <form onSubmit={evt => {
                        evt.preventDefault();
                        const email = this.inputUsername.value;
                        const password = this.inputPassword.value;
                        this.inputPassword.value = '';
                        login({ loginState, email, password });
                    }}>
                        <Helmet titleTemplate="Login - %s" />
                        <label htmlFor="username">
                            Nutzername
                        </label>
                        <input type="text" name="username" ref={ref => this.inputUsername = ref} />
                        <br />
                        <label htmlFor="password">Passwort</label>
                        <input type="password" name="password" ref={ref => this.inputPassword = ref} />
                        <button type="submit">
                            Anmelden
                        </button>
                    </form>
                )}
            </LoginContext.Consumer>
        );
    }
}

export default LoginComponent;
