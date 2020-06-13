import React from 'react';
import { Route, Switch, Redirect } from 'react-router';
import { requiresLogin } from '../util';
import NotFoundHandlerComponent from './notfound';
import { fullPathOfUserSettings, fullPathOfUsersSettings, fullPathOfShiftsSettings, fullPathOfProjectSettings, fullPathOfSettings } from '../paths';
import UserEditComponent from './user/user-edit';
import UsersProvider from '../providers/users-provider';
import { SessionContext } from '../providers/session-provider';
import UserListComponent from './user/user-list';
import ProjectsProvider from '../providers/projects-provider';
import ProjectEditComponent from './project/project-edit';

class SettingsComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (<>
            <ProjectsProvider>
                <Switch>
                    <Route path={fullPathOfProjectSettings()} component={ProjectEditComponent} />
                </Switch>
            </ProjectsProvider>
            <UsersProvider>
                <Switch>
                    {/* 
                        KEEP IN SYNC WITH pages.js
                        it's necessary for the generation of the title breadcrump
                    */}
                    <Route path={fullPathOfUserSettings()} component={UserEditComponent} />
                    <Route path={fullPathOfSettings()} exact={true} component={props => (
                        <SessionContext.Consumer>
                            {sessionsState => (
                                <Redirect to={sessionsState.hasPermission('ROLE_RIGHT_USERS_GET') ? fullPathOfUsersSettings() : fullPathOfUserSettings(sessionsState.currentUser.id)} />
                            )}
                        </SessionContext.Consumer>
                    )} />
                </Switch>
            </UsersProvider>
        </>);
    }
}
export default requiresLogin(SettingsComponent);