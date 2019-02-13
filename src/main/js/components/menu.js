import React from 'react';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import DashboardIcon from '@material-ui/icons/Dashboard';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import FlashOnIcon from '@material-ui/icons/FlashOn';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import FaceIcon from '@material-ui/icons/Face';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import VpnKeyIcon from '@material-ui/icons/VpnKey';
import { Link } from 'react-router-dom';
import { SessionContext } from '../providers/session-provider';
import { logout } from '../actions/login';
import { fullPathOfHome, fullPathOfLogin, fullPathOfHeartbeat, fullPathOfProjects } from '../paths';

const linkStyle = { textDecoration: 'none' };

const CurrentUserItem = props => (
  <ListItem button>
    <ListItemIcon>
      <FaceIcon />
    </ListItemIcon>
    <ListItemText primary={`${props.currentUser.firstName} ${props.currentUser.lastName}`} />
  </ListItem>
);

const HomeItem = () => (
  <Link to={fullPathOfHome()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <DashboardIcon />
      </ListItemIcon>
      <ListItemText primary="Übersicht" />
    </ListItem>
  </Link>
);

const ProjectsItem = () => (
  <Link to={fullPathOfProjects()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <GroupWorkIcon />
      </ListItemIcon>
      <ListItemText primary="Projekte" />
    </ListItem>
  </Link>
);

const HeartbeatItem = () => (
  <Link to={fullPathOfHeartbeat()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <FlashOnIcon />
      </ListItemIcon>
      <ListItemText primary="Heartbeat" />
    </ListItem>
  </Link>
);

const LoginItem = () => (
  <Link to={fullPathOfLogin()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <VpnKeyIcon />
      </ListItemIcon>
      <ListItemText primary="Anmelden" />
    </ListItem>
  </Link>
);

const LogoutItem = props => (
  <ListItem button onClick={() => {
    logout({ loginState: props.loginState });
  }}>
    <ListItemIcon>
      <ExitToAppIcon />
    </ListItemIcon>
    <ListItemText primary="Abmelden" />
  </ListItem>
);

// TODO Implementation missing
const LegalItem = () => (
  <ListItem button>
    <ListItemIcon>
      <FileCopyIcon />
    </ListItemIcon>
    <ListItemText primary="Impressum / Rechtliches" />
  </ListItem>
);

export default function AppMenu() {
  return (
    <SessionContext.Consumer>
      {loginState => loginState.isLoggedIn() ? (
        <>
          <List>
            <CurrentUserItem currentUser={loginState.currentUser} />
          </List>
          <Divider />
          <List>
            <HomeItem />
            <ProjectsItem />
            <HeartbeatItem />
          </List>
          <Divider />
          <List>
            <LogoutItem loginState={loginState} />
            <LegalItem />
          </List>
        </>
      ) : (
          <>
            <List>
              <HomeItem />
              <LoginItem />
            </List>
            <Divider />
            <List>
              <LegalItem />
            </List>
          </>
        )}
    </SessionContext.Consumer>
  );
}
