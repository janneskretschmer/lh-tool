import React from 'react';
import Typography from '@mui/material/Typography';
import { fullPathOfProjects, fullPathOfNeedQuantities, fullPathOfNeedApply, fullPathOfLogin, fullPathOfNeedApprove } from '../paths';
import { withContext } from '../util';
import { SessionContext } from '../providers/session-provider';
import { Redirect } from 'react-router-dom';

@withContext('sessionState', SessionContext)
export default class NotFoundHandlerComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        const { sessionState } = this.props;
        if (!sessionState.isLoggedIn()) {
            return (<Redirect to={fullPathOfLogin()} />)
        }
        if (sessionState.hasPermission('ROLE_ADMIN') || sessionState.hasPermission('ROLE_CONSTRUCTION_SERVANT')) {
            return (<Redirect to={fullPathOfNeedApprove()} />);
        }
        if (sessionState.hasPermission('ROLE_LOCAL_COORDINATOR')) {
            return (<Redirect to={fullPathOfNeedQuantities()} />);
        }
        if (sessionState.hasPermission('ROLE_ATTENDANCE')) {
            return (<Redirect to={fullPathOfNeedApprove()} />);
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
