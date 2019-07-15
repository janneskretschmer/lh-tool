import React from 'react';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import AssignmentIndIcon from '@material-ui/icons/AssignmentInd';
import AssignmentTurnedInIcon from '@material-ui/icons/AssignmentTurnedIn';
import DateRangeIcon from '@material-ui/icons/DateRange';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import GroupAddIcon from '@material-ui/icons/GroupAdd';
import FaceIcon from '@material-ui/icons/Face';
import HomeIcon from '@material-ui/icons/Home';
import BuildIcon from '@material-ui/icons/Build';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import SecurityIcon from '@material-ui/icons/Security';
import VpnKeyIcon from '@material-ui/icons/VpnKey';
import React from 'react';
import { Link } from 'react-router-dom';
import WithPermission from './with-permission';
import { SessionContext } from '../providers/session-provider';
import { logout } from '../actions/login';
import {
    fullPathOfLogin,
    fullPathOfChangePw,
  fullPathOfDataProtection,
  fullPathOfImprint,
    fullPathOfStores,
    fullPathOfItems,
  fullPathOfNeedApply,
  fullPathOfNeedApprove,
  fullPathOfNeedQuantities,
  fullPathOfProjects
} from '../paths';
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

const StoresItem = () => (
  <WithPermission permission="ROLE_ADMIN">
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
  <WithPermission permission="ROLE_ADMIN">
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

const StoresItem = () => (
    <WithPermission permission="ROLE_ADMIN">
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
    <WithPermission permission="ROLE_ADMIN">
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

// TODO Settings not implemented
const SettingsItem = () => (
    <a href="javascript:alert('TODO: Einstellungen implementieren u.a. zum Pflegen von Gewerken')" style={linkStyle}>
        <ListItem button>
            <ListItemIcon>
                <SettingsIcon />
            </ListItemIcon>
            <ListItemText primary="Einstellungen" />
        </ListItem>
    </a>
)

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
