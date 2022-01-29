import Divider from '@mui/material/Divider';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';
import AssignmentTurnedInIcon from '@mui/icons-material/AssignmentTurnedIn';
import BuildIcon from '@mui/icons-material/Build';
import DateRangeIcon from '@mui/icons-material/DateRange';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import FaceIcon from '@mui/icons-material/Face';
import FileCopyIcon from '@mui/icons-material/FileCopy';
import GroupWorkIcon from '@mui/icons-material/GroupWork';
import HomeIcon from '@mui/icons-material/Home';
import SecurityIcon from '@mui/icons-material/Security';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import SettingsIcon from '@mui/icons-material/Settings';
import React from 'react';
import { Link } from 'react-router-dom';
import { logout } from '../actions/login';
import { fullPathOfChangePw, fullPathOfDataProtection, fullPathOfImprint, fullPathOfItems, fullPathOfLogin, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfStores, fullPathOfUsersSettings, fullPathOfUserSettings, fullPathOfSlots } from '../paths';
import { SessionContext } from '../providers/session-provider';
import WithPermission from './with-permission';
import { RIGHT_ITEMS_GET, RIGHT_NEEDS_APPLY, RIGHT_NEEDS_POST, RIGHT_NEEDS_VIEW_APPROVED, RIGHT_SLOTS_GET, RIGHT_USERS_GET } from '../permissions';


const Item = props => (
  <ListItem button>
    <ListItemIcon sx={{ minWidth: '40px' }}>
      {props.icon}
    </ListItemIcon>
    <ListItemText primary={props.text} sx={{ color: 'text.primary' }} />
  </ListItem>
)

const LinkItem = props => (
  <Link to={props.path} style={{ textDecoration: 'none' }}>
    <Item {...props} />
  </Link>
)


const LogoutItem = props => (
  <ListItem button onClick={() => {
    logout({ loginState: props.loginState });
  }}>
    <ListItemIcon>
      <ExitToAppIcon />
    </ListItemIcon>
    <ListItemText primary="Abmelden" sx={{ color: 'text.primary' }} />
  </ListItem>
);

export default function AppMenu() {
  return (
    <SessionContext.Consumer>
      {loginState => loginState.isLoggedIn() ? (
        <>
          <List>
            <Item icon={<FaceIcon />} text={`${loginState.currentUser.firstName} ${loginState.currentUser.lastName}`} />
          </List>
          <Divider />
          <List>

            <WithPermission permission={RIGHT_NEEDS_POST}>
              <LinkItem icon={<DateRangeIcon />} text="Bedarf" path={fullPathOfNeedQuantities()} />
            </WithPermission>

            <WithPermission permission={RIGHT_NEEDS_APPLY}>
              <LinkItem icon={<AssignmentIndIcon />} text="Bewerben" path={fullPathOfNeedApply()} />
            </WithPermission>

            <WithPermission permission={RIGHT_NEEDS_VIEW_APPROVED}>
              <LinkItem icon={<AssignmentTurnedInIcon />} text="Zuteilen" path={fullPathOfNeedApprove()} />
            </WithPermission>

            <WithPermission permission={RIGHT_SLOTS_GET}>
              <LinkItem icon={<HomeIcon />} text="Lagerplätze" path={fullPathOfSlots()} />
            </WithPermission>

            <WithPermission permission={RIGHT_ITEMS_GET}>
              <LinkItem icon={<BuildIcon />} text="Artikel" path={fullPathOfItems()} />
            </WithPermission>
          </List>
          <Divider />
          <List>
            <LinkItem icon={<SettingsIcon />} text="Einstellungen" path={loginState.hasPermission(RIGHT_USERS_GET) ? fullPathOfUsersSettings() : fullPathOfUserSettings(loginState.currentUser.id)} />

            <LogoutItem loginState={loginState} />
            <LinkItem icon={<FileCopyIcon />} text="Impressum" path={fullPathOfImprint()} />
            <LinkItem icon={<FileCopyIcon />} text="Datenschutz" path={fullPathOfDataProtection()} />
          </List>
        </>
      ) : (
        <>
          <List>
            <LinkItem icon={<VpnKeyIcon />} text="Anmelden" path={fullPathOfLogin()} />
          </List>
          <Divider />
          <List>
            <LinkItem icon={<FileCopyIcon />} text="Impressum" path={fullPathOfImprint()} />
            <LinkItem icon={<FileCopyIcon />} text="Datenschutz" path={fullPathOfDataProtection()} />
          </List>
        </>
      )}
    </SessionContext.Consumer>
  );
}
