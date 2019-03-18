import React from 'react';
import { SessionContext } from '../providers/session-provider';


const WithoutPermission = props => (
    <SessionContext.Consumer>
        {loginState => !loginState.hasPermission(props.permission) ? props.children : null}
    </SessionContext.Consumer>
);

export default WithoutPermission;
