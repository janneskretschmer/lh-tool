import React from 'react';
import { Helmet } from 'react-helmet';
import rest from 'rest';
import mime from 'rest/interceptor/mime';

class HeartbeatComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = { gotHeartbeat: false };
        this.client = rest.wrap(mime);
        this.intervalId = null;
    }

    componentDidMount() {
        this.intervalId = setInterval(() => {
            const hbUrl = '/lh-tool/rest/info/heartbeat';
            this.client(hbUrl)
                .then(response => {
                    this.setState({ gotHeartbeat: response.entity });
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

export default HeartbeatComponent;
