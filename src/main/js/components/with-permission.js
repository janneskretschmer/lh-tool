import React from 'react';
import { SessionContext } from '../providers/session-provider';


const WithPermission = props => (
    <SessionContext.Consumer>
        {loginState => loginState.hasPermission(props.permission) ? props.children : null}
    </SessionContext.Consumer>
);

export default WithPermission;
