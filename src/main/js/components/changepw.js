import React from 'react';
import { Helmet } from 'react-helmet';
import URI from 'urijs';

export default class ChangePasswordComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        const { uid, token } = URI(this.props.location).query(true);

        const isTokenBased = !!token;

        return (
            <>
                <Helmet titleTemplate="Passwort ändern - %s" />
                <div>
                    Passwort ändern
                </div>
            </>
        );
    }
}
