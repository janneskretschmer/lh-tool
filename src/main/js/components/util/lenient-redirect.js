import { Redirect, withRouter } from 'react-router-dom';
import React from 'react';


export default class LenientRedirect extends React.Component {

    componentDidUpdate() {
        if (this.props.onSamePage && this.isSamePage()) {
            this.props.onSamePage();
        }
    }

    isSamePage() {
        // location from withRouter() updates too slow, so it will cause multiple Redirects as well
        return this.props.to && window.location.href.endsWith(this.props.to);
    }

    render() {
        if (!this.isSamePage()) {
            return (<Redirect push {...this.props} />);
        }
        return (<></>);
    }
};
