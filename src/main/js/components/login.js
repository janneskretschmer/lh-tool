import React from 'react';

const LoginComponent = () => (
    <form>
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
