const React = require('react');
const ReactDOM = require('react-dom');
const rest = require('rest');
const mime = require('rest/interceptor/mime');

class HeartbeatApp extends React.Component {

	constructor(props) {
		super(props);
		this.state = { gotHeartbeat: false };
		this.client = rest.wrap(mime);
	}

	componentDidMount() {
		setInterval(() => {
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

	render() {
		return (
			<div style={{
				color: this.state.gotHeartbeat ? 'green' : 'red'
			}}>
				{this.state.gotHeartbeat ? 'OK' : 'Kein Heartbeat!'}
			</div>
		)
	}
}

ReactDOM.render(
	<HeartbeatApp />,
	document.getElementById('main-app-container')
);