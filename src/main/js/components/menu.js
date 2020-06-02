import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import AssignmentIndIcon from '@material-ui/icons/AssignmentInd';
import AssignmentTurnedInIcon from '@material-ui/icons/AssignmentTurnedIn';
import BuildIcon from '@material-ui/icons/Build';
import DateRangeIcon from '@material-ui/icons/DateRange';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import FaceIcon from '@material-ui/icons/Face';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import HomeIcon from '@material-ui/icons/Home';
import SecurityIcon from '@material-ui/icons/Security';
import VpnKeyIcon from '@material-ui/icons/VpnKey';
import SettingsIcon from '@material-ui/icons/Settings';
import React from 'react';
import { Link } from 'react-router-dom';
import { logout } from '../actions/login';
import { fullPathOfChangePw, fullPathOfDataProtection, fullPathOfImprint, fullPathOfItems, fullPathOfLogin, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfStores, fullPathOfUsersSettings } from '../paths';
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
  // TODO: pick better right
  <WithPermission permission="ROLE_RIGHT_USERS_CREATE">
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
  <WithPermission permission="ROLE_RIGHT_NEEDS_VIEW_APPROVED">
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

const StoresItem = () => (
  <WithPermission permission="ROLE_RIGHT_STORES_GET">
    <Link to={fullPathOfStores()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <HomeIcon />
        </ListItemIcon>
        <ListItemText primary="Lager" />
      </ListItem>
    </Link>
  </WithPermission>
);


const ItemsItem = () => (
  <WithPermission permission="ROLE_RIGHT_ITEMS_GET">
    <Link to={fullPathOfItems()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <BuildIcon />
        </ListItemIcon>
        <ListItemText primary="Artikel" />
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

const SettingsItem = props => (
  <Link to={fullPathOfUsersSettings()} style={linkStyle}>
    <ListItem button>
      <ListItemIcon>
        <SettingsIcon />
      </ListItemIcon>
      <ListItemText primary="Einstellungen" />
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
            <StoresItem />
            <ItemsItem />
          </List>
          <Divider />
          <List>
            <SettingsItem />
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
