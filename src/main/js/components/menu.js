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
import { fullPathOfChangePw, fullPathOfDataProtection, fullPathOfImprint, fullPathOfItems, fullPathOfLogin, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfStores, fullPathOfUsersSettings, fullPathOfUserSettings, fullPathOfSlots } from '../paths';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';
import { RIGHT_ITEMS_GET, RIGHT_NEEDS_APPLY, RIGHT_NEEDS_POST, RIGHT_NEEDS_VIEW_APPROVED, RIGHT_SLOTS_GET, RIGHT_USERS_GET } from '../permissions';


const linkStyle = { textDecoration: 'none' };

const CurrentUserItem = props => (
  <ListItem button>
    <ListItemIcon>
      <FaceIcon />
    </ListItemIcon>
    <ListItemText primary={`${props.currentUser.firstName} ${props.currentUser.lastName}`} />
  </ListItem>
);

const NeedQuantitiesItem = () => (
  <WithPermission permission={RIGHT_NEEDS_POST}>
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
  <WithPermission permission={RIGHT_NEEDS_APPLY}>
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
  <WithPermission permission={RIGHT_NEEDS_VIEW_APPROVED}>
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
  <WithPermission permission={RIGHT_SLOTS_GET}>
    <Link to={fullPathOfSlots()} style={linkStyle}>
      <ListItem button>
        <ListItemIcon>
          <HomeIcon />
        </ListItemIcon>
        <ListItemText primary="Lagerplätze" />
      </ListItem>
    </Link>
  </WithPermission>
);


const ItemsItem = () => (
  <WithPermission permission={RIGHT_ITEMS_GET}>
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
  <Link to={props.sessionState.hasPermission(RIGHT_USERS_GET) ? fullPathOfUsersSettings() : fullPathOfUserSettings(props.sessionState.currentUser.id)} style={linkStyle}>
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
            <NeedQuantitiesItem />
            <NeedApplyItem />
            <NeedApproveItem />
            <StoresItem />
            <ItemsItem />
          </List>
          <Divider />
          <List>
            <SettingsItem sessionState={loginState} />
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
