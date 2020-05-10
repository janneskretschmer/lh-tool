import React from 'react';
import { Route, Switch, Redirect } from 'react-router';
import { requiresLogin } from '../util';
import NotFoundHandlerComponent from './notfound';
import { fullPathOfUserSettings } from '../paths';
import UserEditComponent from './user/user-edit';
import UsersProvider from '../providers/users-provider';
import { SessionContext } from '../providers/session-provider';

class SettingsComponent extends React.Component {
    render() {
        return (<>
            <UsersProvider>
                <Switch>
                    {/* 
                        KEEP IN SYNC WITH pages.js
                        it's necessary for the generation of the title breadcrump
                    */}
                    <Route path={fullPathOfUserSettings()} component={UserEditComponent} />
                    <Route component={props => (
                        <SessionContext.Consumer>
                            {sessionsState => (
                                <Redirect to={fullPathOfUserSettings(sessionsState.currentUser.id)} />
                            )}
                        </SessionContext.Consumer>
                    )} />
                </Switch>
            </UsersProvider>
        </>);
    }
}
export default requiresLogin(SettingsComponent);