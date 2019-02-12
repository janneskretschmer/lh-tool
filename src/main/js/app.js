import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import { fullPathOfHome, fullPathOfLogin, fullPathOfHeartbeat, fullPathOfProjects } from './paths';
import HomeComponent from './components/home';
import LoginComponent from './components/login';
import HeartbeatComponent from './components/heartbeat';
import ProjectComponent from './components/project';
import SessionProvider, { SessionContext } from './providers/session-provider';
import { logout } from './actions/login';

const LHToolApp = () => (
	<Router>
		<div>
			<SessionProvider>
				<h1>LH-Tool</h1>
				<h3>Local Helper Tool for LDC</h3>

				<Helmet>
					<title>LH-Tool</title>
				</Helmet>

				<ul>
					<li><Link to={fullPathOfHome()}>Home</Link></li>
					<li><Link to={fullPathOfLogin()}>Login</Link></li>
					<li><Link to={fullPathOfHeartbeat()}>Heartbeat</Link></li>
					<li><Link to={fullPathOfProjects()}>Projekte</Link></li>
				</ul>

				<Route path={fullPathOfHome()} exact component={HomeComponent} />
				<Route path={fullPathOfLogin()} component={LoginComponent} />
				<Route path={fullPathOfHeartbeat()} component={HeartbeatComponent} />
				<Route path={fullPathOfProjects()} component={ProjectComponent} />

				<SessionContext.Consumer>
					{loginState => loginState.isLoggedIn() ? (
						<>
							<span>{`ANGEMELDET:  ${loginState.currentUser.firstName} ${loginState.currentUser.lastName}`}</span>
							<button onClick={() => logout({ loginState })}>Logout</button>
						</>
					) : (
							<span>NICHT ANGEMELDET</span>
						)}
				</SessionContext.Consumer>
			</SessionProvider>
		</div>
	</Router>
);

export default LHToolApp;
