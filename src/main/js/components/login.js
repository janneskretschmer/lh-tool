import React from 'react';
import { Helmet } from 'react-helmet';

const LoginComponent = () => (
    <form>
        <Helmet titleTemplate="Login - %s" />
        <label htmlFor="username">
            Nutzername
        </label>
        <input type="text" id="username" />
        <br />
        <label htmlFor="password">Passwort</label>
        <input type="text" id="password" />
        <button type="submit">
            Anmelden
        </button>
    </form>
)

export default LoginComponent;
