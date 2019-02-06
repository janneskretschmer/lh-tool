import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import { fullPathOfHome, fullPathOfLogin, fullPathOfHeartbeat } from './paths';
import HomeComponent from './components/home';
import LoginComponent from './components/login';
import HeartbeatComponent from './components/heartbeat';
import LoginProvider, { LoginContext } from './providers/login-provider';
import { logout } from './actions/login';

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

				<Route path={fullPathOfHome()} exact component={HomeComponent} />
				<Route path={fullPathOfLogin()} component={LoginComponent} />
				<Route path={fullPathOfHeartbeat()} component={HeartbeatComponent} />

				<LoginContext.Consumer>
					{loginState => loginState.isLoggedIn() ? (
						<>
							<span>ANGEMELDET</span>
							<button onClick={() => logout({ loginState })}>Logout</button>
						</>
					) : (
							<span>NICHT ANGEMELDET</span>
						)}
				</LoginContext.Consumer>
			</LoginProvider>
		</div>
	</Router>
);

export default LHToolApp;
