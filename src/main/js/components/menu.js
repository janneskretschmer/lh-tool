import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import AssignmentIndIcon from '@material-ui/icons/AssignmentInd';
import AssignmentTurnedInIcon from '@material-ui/icons/AssignmentTurnedIn';
import DateRangeIcon from '@material-ui/icons/DateRange';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import FaceIcon from '@material-ui/icons/Face';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import SecurityIcon from '@material-ui/icons/Security';
import VpnKeyIcon from '@material-ui/icons/VpnKey';
import React from 'react';
import { Link } from 'react-router-dom';
import { logout } from '../actions/login';
import { fullPathOfChangePw, fullPathOfImprint, fullPathOfLogin, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfDataProtection } from '../paths';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';

const linkStyle = { textDecoration: 'none' };

const CurrentUserItem = props => (
  <ListItem button>
    <ListItemIcon>
      <FaceIcon />
    </ListItemIcon>
    <ListItemText primary={`${props.currentUser.firstName} ${props.currentUser.lastName}`} />
  </ListItem>
);

const ProjectsItem = () => (
  <WithPermission permission="ROLE_RIGHT_PROJECTS_POST">
    <Link to={fullPathOfProjects()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <GroupWorkIcon />
        </ListItemIcon>
        <ListItemText primary="Projekte" />
      </ListItem>
    </Link>
  </WithPermission>
);

const NeedQuantitiesItem = () => (
  <WithPermission permission="ROLE_RIGHT_NEEDS_POST">
    <Link to={fullPathOfNeedQuantities()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <DateRangeIcon />
        </ListItemIcon>
        <ListItemText primary="Bedarf" />
      </ListItem>
    </Link>
  </WithPermission>
);

const NeedApplyItem = () => (
  <WithPermission permission="ROLE_RIGHT_NEEDS_APPLY">
    <Link to={fullPathOfNeedApply()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <AssignmentIndIcon />
        </ListItemIcon>
        <ListItemText primary="Bewerben" />
      </ListItem>
    </Link>
  </WithPermission>
);

const NeedApproveItem = () => (
  <WithPermission permission="ROLE_RIGHT_NEEDS_APPROVE">
    <Link to={fullPathOfNeedApprove()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <AssignmentTurnedInIcon />
        </ListItemIcon>
        <ListItemText primary="Zuteilen" />
      </ListItem>
    </Link>
  </WithPermission>
);

const ChangePwItem = () => (
  <Link to={fullPathOfChangePw()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <SecurityIcon />
      </ListItemIcon>
      <ListItemText primary="Passwort ändern" />
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

const ImprintItem = () => (
  <Link to={fullPathOfImprint()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <FileCopyIcon />
      </ListItemIcon>
      <ListItemText primary="Impressum" />
    </ListItem>
  </Link>
);

const DataProtectionItem = () => (
  <Link to={fullPathOfDataProtection()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <FileCopyIcon />
      </ListItemIcon>
      <ListItemText primary="Datenschutz" />
    </ListItem>
  </Link>
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
            <ProjectsItem />
            <NeedQuantitiesItem />
            <NeedApplyItem />
            <NeedApproveItem />
          </List>
          <Divider />
          <List>
            <LogoutItem loginState={loginState} />
            <ImprintItem />
            <DataProtectionItem />
          </List>
        </>
      ) : (
          <>
            <List>
              <LoginItem />
            </List>
            <Divider />
            <List>
              <ImprintItem />
              <DataProtectionItem />
            </List>
          </>
        )}
    </SessionContext.Consumer>
  );
}
