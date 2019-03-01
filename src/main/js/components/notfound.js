import React from 'react';
import Typography from '@material-ui/core/Typography';

export default class NotFoundComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    componentWillMount() {
        // FUTURE: 404 Status should be set here as soon as isomorphic rendering is employed.
    }

    render() {
        return (
            <>
                <Typography component="h2" variant="h1" gutterBottom>
                    404 - Diese Seite gibt es nicht.
                </Typography>
                <Typography variant="body1" gutterBottom>
                    Das tut uns leid. Da hast du dich leider verirrt.
                </Typography>
            </>
        );
    }
}
