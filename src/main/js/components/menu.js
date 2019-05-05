import React from 'react';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import GroupAddIcon from '@material-ui/icons/GroupAdd';
import AssignmentIndIcon from '@material-ui/icons/AssignmentInd';
import AssignmentTurnedInIcon from '@material-ui/icons/AssignmentTurnedIn';
import DateRangeIcon from '@material-ui/icons/DateRange';
import FaceIcon from '@material-ui/icons/Face';
import HomeIcon from '@material-ui/icons/Home';
import BuildIcon from '@material-ui/icons/Build';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import VpnKeyIcon from '@material-ui/icons/VpnKey';
import SecurityIcon from '@material-ui/icons/Security';
import { Link } from 'react-router-dom';
import WithPermission from './with-permission';
import { SessionContext } from '../providers/session-provider';
import { logout } from '../actions/login';
import {
  fullPathOfLogin,
  fullPathOfProjects,
  fullPathOfNeedQuantities,
  fullPathOfNeedApply,
  fullPathOfNeedApprove,
  fullPathOfChangePw,
  fullPathOfStores,
  fullPathOfItems,
} from '../paths';

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
            <ProjectsItem />
            <NeedQuantitiesItem />
            <NeedApplyItem />
            <NeedApproveItem />
            <StoresItem />
            <ItemsItem />
            <StoresItem />
            <ItemsItem />
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
