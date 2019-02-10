import React from 'react';
import { Helmet } from 'react-helmet';
import { apiRequest, apiEndpoints } from '../apiclient';

export default class HeartbeatComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = { gotHeartbeat: false };
        this.intervalId = null;
    }

    componentDidMount() {
        this.intervalId = setInterval(() => {
            apiRequest({ apiEndpoint: apiEndpoints.info.heartbeat })
                .then(result => {
                    this.setState({ gotHeartbeat: result.response });
                })
                .catch(err => {
                    this.setState({ gotHeartbeat: false });
                });
        }, 2000);
    }

    componentWillUnmount() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    render() {
        return (
            <div style={{
                color: this.state.gotHeartbeat ? 'green' : 'red'
            }}>
                <Helmet titleTemplate="Heartbeat - %s" />
                {this.state.gotHeartbeat ? 'OK' : 'Kein Heartbeat!'}
            </div>
        )
    }
}
