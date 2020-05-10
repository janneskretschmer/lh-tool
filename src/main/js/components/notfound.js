import React from 'react';
import Typography from '@material-ui/core/Typography';
import { Redirect } from 'react-router';
import { fullPathOfProjects, fullPathOfNeedQuantities, fullPathOfNeedApply } from '../paths';
import { withContext } from '../util';
import { SessionContext } from '../providers/session-provider';

@withContext('sessionState', SessionContext)
export default class NotFoundHandlerComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    componentWillMount() {
        // FUTURE: 404 Status should be set here as soon as isomorphic rendering is employed.
    }

    render() {
        const { sessionState } = this.props;
        if (sessionState.hasPermission('ROLE_ADMIN') || sessionState.hasPermission('ROLE_CONSTRUCTION_SERVANT')) {
            return (<Redirect to={fullPathOfProjects()} />);
        }
        if (sessionState.hasPermission('ROLE_LOCAL_COORDINATOR')) {
            return (<Redirect to={fullPathOfNeedQuantities()} />);
        }
        if (sessionState.hasPermission('ROLE_PUBLISHER')) {
            return (<Redirect to={fullPathOfNeedApply()} />);
        }
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
