import React from 'react';
import { Route, Switch, Redirect } from 'react-router';
import { requiresLogin } from '../util';
import NotFoundHandlerComponent from './notfound';
import { fullPathOfUserSettings, fullPathOfUsersSettings, fullPathOfShiftsSettings, fullPathOfProjectSettings, fullPathOfSettings, fullPathOfProjectsSettings } from '../paths';
import UserEditComponent from './user/user-edit';
import UsersProvider from '../providers/users-provider';
import { SessionContext } from '../providers/session-provider';
import UserListComponent from './user/user-list';
import ProjectsProvider from '../providers/projects-provider';
import ProjectEditComponent from './project/project-edit';
import ProjectListComponent from './project/project-list';
import WithPermission from './with-permission';
import WithoutPermission from './without-permission';
import LenientRedirect from './util/lenient-redirect';

class SettingsComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (<>
            <ProjectsProvider>
                <Switch>
                    <Route path={fullPathOfProjectSettings()} component={ProjectEditComponent} />
                    <Route path={fullPathOfProjectsSettings()} component={ProjectListComponent} />
                </Switch>
            </ProjectsProvider>
            <UsersProvider>
                <Switch>
                    {/* 
                        KEEP IN SYNC WITH pages.js
                        it's necessary for the generation of the title breadcrump
                    */}
                    <Route path={fullPathOfUserSettings()} component={UserEditComponent} />
                    <SessionContext.Consumer>
                        {sessionsState => sessionsState.hasPermission('ROLE_RIGHT_USERS_GET') ? (
                            <Route path={fullPathOfUsersSettings()} component={UserListComponent} />
                        ) : (
                                <Route path={fullPathOfUsersSettings()} component={props => (<LenientRedirect to={fullPathOfUserSettings(sessionsState.currentUser.id)} />)} />
                            )}
                    </SessionContext.Consumer>
                </Switch>
            </UsersProvider>
        </>);
    }
}
export default requiresLogin(SettingsComponent);