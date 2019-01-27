import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import { fullPathOfHome, fullPathOfLogin, fullPathOfHeartbeat } from './paths';
import HomeComponent from './components/home';
import LoginComponent from './components/login';
import HeartbeatComponent from './components/heartbeat';
import LoginProvider, { LoginContext } from './providers/login-provider';

const LHToolApp = () => (
	<Router>
		<div>
			<LoginProvider>
				<h1>LH-Tool</h1>
				<h3>Local Helper Tool for LDC</h3>

				<Helmet>
					<title>LH-Tool</title>
				</Helmet>

				<ul>
					<li><Link to={fullPathOfHome()}>Home</Link></li>
					<li><Link to={fullPathOfLogin()}>Login</Link></li>
					<li><Link to={fullPathOfHeartbeat()}>Heartbeat</Link></li>
				</ul>

				<LoginContext.Consumer>
					{loginState => loginState.isLoggedIn() ? 'ANGEMELDET' : 'NICHT ANGEMELDET'}
				</LoginContext.Consumer>

				<Route path={fullPathOfHome()} exact component={HomeComponent} />
				<Route path={fullPathOfLogin()} component={LoginComponent} />
				<Route path={fullPathOfHeartbeat()} component={HeartbeatComponent} />
			</LoginProvider>
		</div>
	</Router>
);

export default LHToolApp;
