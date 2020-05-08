import React from 'react';
import { Route, Switch } from 'react-router';
import { requiresLogin } from '../util';
import NotFoundComponent from './notfound';
import { fullPathOfUserSettings } from '../paths';
import UserEditComponent from './user/user-edit';
import UsersProvider from '../providers/users-provider';

class SettingsComponent extends React.Component {
    render() {
        return (<>
            <UsersProvider>
                <Switch>
                    <Route path={fullPathOfUserSettings()} component={UserEditComponent} />
                    <Route component={NotFoundComponent} />
                </Switch>
            </UsersProvider>
        </>);
    }
}
export default requiresLogin(SettingsComponent);