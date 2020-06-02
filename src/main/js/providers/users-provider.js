import React from 'react';
import { createUser, fetchUser, updateUser, fetchUserRoles, createUserRole, deleteUserRole, fetchUserProjects } from "../actions/user";
import { withContext, isAnyStringBlank } from '../util';
import { SessionContext } from './session-provider';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import { fetchRoles } from '../actions/role';
import { fetchProjects, deleteProjectUser, createProjectUser } from '../actions/project';

export const UsersContext = React.createContext();

@withContext('sessionState', SessionContext)
export default class UsersProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            users: new Map(),
            roles: null,
            projects: null,
            // copy for editing
            selectedUser: null,
        }
    }

    ///////////////////////////////////////////////////// Load & Store /////////////////////////////////////////////////////

    createEmptyUser() {
        const projects = this.state.projects && this.state.projects.length === 1 ? [{ projectId: this.state.projects[0].id }] : [];
        return { gender: 'MALE', firstName: '', lastName: '', email: '', telephoneNumber: '', mobileNumber: '', businessNumber: '', skills: '', profession: '', roles: [{ role: 'ROLE_PUBLISHER' }], projects };
    }

    selectUser(userId, handleFailure) {
        if (!userId) {
            return;
        }

        this.loadGrantableRoles();
        this.loadProjects();

        if (userId === NEW_ENTITY_ID_PLACEHOLDER) {
            this.setState({
                selectedUser: this.createEmptyUser()
            });
            return;
        }
        const parsedUserId = parseInt(userId);
        if (this.state.users.has(parsedUserId)) {
            this.setState({
                selectedUser: this.state.users.get(parsedUserId),
            });
        } else {
            fetchUser(this.props.sessionState.accessToken, parsedUserId).then(savedUser => this.loadRolesAndProjects(savedUser)).then(user =>
                this.setState(prevState => {
                    const users = new Map(prevState.users);
                    users.set(user.id, user);
                    return {
                        users,
                        selectedUser: user,
                    };
                })
            ).catch(handleFailure);
        }
    }

    resetSelectedUser() {
        if (this.state.selectedUser && this.state.selectedUser.id) {
            this.setState({ selectedUser: this.state.users.get(this.state.selectedUser.id) })
        } else {
            this.setState({ selectedUser: this.createEmptyUser() });
        }
    }

    isUserValid() {
        const user = this.state.selectedUser;
        return !!user &&
            !isAnyStringBlank([user.firstName, user.lastName, user.email]) &&
            user.roles.length > 0 &&
            (user.projects.length > 0 || user.roles.find(userRole => userRole.role === 'ROLE_ADMIN'));
    }

    saveSelectedUser() {
        const user = this.state.selectedUser;
        const accessToken = this.props.sessionState.accessToken;
        let userPromise;
        if (user.id) {
            userPromise = updateUser(accessToken, user).then(savedUser => {
                return Promise.all([
                    // roles to add (new roles - old roles)
                    ...user.roles.filter(addedRole => !this.state.users.get(user.id).roles.find(userRole => userRole.role === addedRole.role)).map(
                        userRole => createUserRole(accessToken, userRole)
                    ),
                    // roles to delete (old roles - new roles)
                    ...this.state.users.get(savedUser.id).roles.filter(userRole => !user.roles.find(addedRole => userRole.role === addedRole.role)).map(
                        userRole => deleteUserRole(accessToken, userRole)
                    ),
                    // projects to add (new projects - old projects)
                    ...user.projects.filter(addedProject => !this.state.users.get(user.id).projects.find(userProject => userProject.projectId === addedProject.projectId)).map(
                        userProject => createProjectUser(accessToken, userProject)
                    ),
                    // projects to delete (old projects - new projects)
                    ...this.state.users.get(savedUser.id).projects.filter(userProject => !user.projects.find(addedProject => userProject.projectId === addedProject.projectId)).map(
                        userProject => deleteProjectUser(accessToken, userProject)
                    )
                ])
                    .then(() => savedUser)
                    .then(savedUser => this.loadRolesAndProjects(savedUser))
            });
        } else {
            userPromise = createUser(this.props.sessionState.accessToken, user)
                // add user to project first, because otherwise the creator might not be allowed to modify the roles
                .then(savedUser => {
                    return Promise.all(
                        user.projects.map(
                            userProject => createProjectUser(accessToken, { ...userProject, userId: savedUser.id })
                        )
                    )
                        .then(projects => ({
                            ...savedUser,
                            projects,
                        }));
                })
                .then(savedUser => {
                    return Promise.all(
                        user.roles.map(
                            userRole => createUserRole(accessToken, { ...userRole, userId: savedUser.id })
                        )
                    )
                        .then(roles => ({
                            ...savedUser,
                            roles,
                        }));
                });
        }

        return userPromise
            .then(savedUser => this.setState(prevState => {
                const users = new Map(prevState.users);
                users.set(savedUser.id, savedUser);
                return {
                    users,
                    selectedUser: savedUser,
                };
            }));
    }

    loadRolesAndProjects(savedUser) {
        let user = { ...savedUser };
        return Promise.all([
            this.findRolesForUser(user).then(roles => user = { ...user, roles }),
            this.findProjectsForUser(user).then(projects => user = { ...user, projects }),
        ]).then(() => user)
    }

    ///////////////////////////////////////////////////// Roles /////////////////////////////////////////////////////

    loadGrantableRoles() {
        if (this.props.sessionState.hasPermission('ROLE_RIGHT_USERS_CHANGE_ROLES')) {
            if (!this.state.roles) {
                fetchRoles(this.props.sessionState.accessToken).then(roles => this.setState({ roles }))
                    //e.g. if the user isn't allowed to grant rights
                    .catch(error => this.setState({ roles: [] }));
            }
        }
    }

    findRolesForUser(user) {
        if (user && this.props.sessionState.hasPermission('ROLE_RIGHT_USERS_CHANGE_ROLES')) {
            return fetchUserRoles(this.props.sessionState.accessToken, user).catch(error => []);
        }
        return Promise.resolve([]);
    }

    ///////////////////////////////////////////////////// Projects /////////////////////////////////////////////////////

    loadProjects() {
        if (!this.state.projects) {
            this.setState({ projects: [] });
            fetchProjects(this.props.sessionState.accessToken).then(projects => this.setState(prevState => {
                let selectedUser = { ...prevState.selectedUser };
                if (!selectedUser.id && (!selectedUser.projects || selectedUser.projects.length === 0) && projects.length === 1) {
                    selectedUser.projects = [{ projectId: projects[0].id }];
                }
                return { projects, selectedUser };
            })).catch(error => this.setState({ projects: [] }));
        }
    }

    findProjectsForUser(user) {
        if (user && this.props.sessionState.hasPermission('ROLE_RIGHT_PROJECTS_USERS_GET')) {
            return fetchUserProjects(this.props.sessionState.accessToken, user).catch(error => []);
        }
        return Promise.resolve([]);
    }

    ///////////////////////////////////////////////////// Modification /////////////////////////////////////////////////////

    changeFirstName(firstName) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                firstName,
            },
        }));
    }

    changeLastName(lastName) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                lastName,
            },
        }));
    }

    changeEmail(email) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                email,
            },
        }));
    }

    changeGender(gender) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                gender,
            },
        }));
    }

    changeTelephoneNumber(telephoneNumber) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                telephoneNumber,
            },
        }));
    }

    changeMobileNumber(mobileNumber) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                mobileNumber,
            },
        }));
    }

    changeBusinessNumber(businessNumber) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                businessNumber,
            },
        }));
    }

    changeProfession(profession) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                profession,
            },
        }));
    }

    changeSkills(skills) {
        this.setState(prevState => ({
            selectedUser: {
                ...prevState.selectedUser,
                skills,
            },
        }));
    }

    toggleRole(role) {
        const user = this.state.selectedUser;
        if (user && user.roles) {
            const userRole = user.roles.find(userRole => userRole.role === role)
            if (userRole) {
                this.setState({
                    selectedUser: {
                        ...user,
                        roles: user.roles.filter(userRole => userRole.role !== role),
                    }
                });
            } else {
                this.setState({
                    selectedUser: {
                        ...user,
                        roles: [...user.roles, { userId: user.id, role }],
                    }
                });
            }
        }
    }

    toggleProject(projectId) {
        const user = this.state.selectedUser;
        if (user && user.projects) {
            const userProject = user.projects.find(userProject => userProject.projectId === projectId)
            if (userProject) {
                this.setState({
                    selectedUser: {
                        ...user,
                        projects: user.projects.filter(userProject => userProject.projectId !== projectId),
                    }
                });
            } else {
                this.setState({
                    selectedUser: {
                        ...user,
                        projects: [...user.projects, { userId: user.id, projectId }],
                    }
                });
            }
        }
    }



    render() {
        return (
            <UsersContext.Provider
                value={{
                    ...this.state,
                    selectUser: this.selectUser.bind(this),
                    resetSelectedUser: this.resetSelectedUser.bind(this),
                    isUserValid: this.isUserValid.bind(this),
                    saveSelectedUser: this.saveSelectedUser.bind(this),
                    loadGrantableRoles: this.loadGrantableRoles.bind(this),

                    changeBusinessNumber: this.changeBusinessNumber.bind(this),
                    changeEmail: this.changeEmail.bind(this),
                    changeFirstName: this.changeFirstName.bind(this),
                    changeGender: this.changeGender.bind(this),
                    changeLastName: this.changeLastName.bind(this),
                    changeMobileNumber: this.changeMobileNumber.bind(this),
                    changeProfession: this.changeProfession.bind(this),
                    changeSkills: this.changeSkills.bind(this),
                    changeTelephoneNumber: this.changeTelephoneNumber.bind(this),
                    toggleRole: this.toggleRole.bind(this),
                    toggleProject: this.toggleProject.bind(this),
                }}
            >
                {this.props.children}
            </UsersContext.Provider>
        );
    }
}