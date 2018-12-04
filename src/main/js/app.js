import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { fullPathOfHome, fullPathOfLogin, fullPathOfHeartbeat } from './paths';
import HomeComponent from './components/home';
import LoginComponent from './components/login';
import HeartbeatComponent from './components/heartbeat';

const LHToolApp = () => (
	<Router>
		<div>
			<h1>LH-Tool</h1>
			<h3>Local Helper Tool for LDC</h3>

			<ul>
				<li><Link to={fullPathOfHome()}>Home</Link></li>
				<li><Link to={fullPathOfLogin()}>Login</Link></li>
				<li><Link to={fullPathOfHeartbeat()}>Heartbeat</Link></li>
			</ul>

			<Route path={fullPathOfHome()} exact component={HomeComponent} />
			<Route path={fullPathOfLogin()} component={LoginComponent} />
			<Route path={fullPathOfHeartbeat()} component={HeartbeatComponent} />
		</div>
	</Router>
);

export default LHToolApp;
