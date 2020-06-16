import { Redirect, withRouter } from 'react-router';
import React from 'react';

const LenientRedirect = props => props.to && props.location.pathname !== props.to && (
    <Redirect push {...props} />
);

export default withRouter(LenientRedirect);
