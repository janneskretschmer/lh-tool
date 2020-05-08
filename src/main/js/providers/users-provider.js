import React from 'react';
import { createUser, fetchUser, updateUser } from "../actions/user";
import { withContext } from '../util';
import { SessionContext } from './session-provider';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';

export const UsersContext = React.createContext();

@withContext('sessionState', SessionContext)
export default class UsersProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            users: new Map(),
        }
    }

    createEmptyUser() {
        return { gender: 'MALE', firstName: '', lastName: '', email: '', telephoneNumber: '', mobileNumber: '', businessNumber: '', skills: '', profession: '' };
    }

    createOrUpdateUser({ id, email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, skills, profession }, handleFailure) {
        const callback = user =>
            new Promise(resolve => this.setState(prevState => {
                const users = new Map(prevState.users);
                users.set(user.id, user);
                return {
                    users,
                };
            }, resolve(user)));
        if (id) {
            return updateUser(this.props.sessionState.accessToken, { id, email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, skills, profession }, handleFailure).then(
                callback
            );
        } else {
            return createUser(this.props.sessionState.accessToken, { email, firstName, lastName, gender, telephoneNumber, mobileNumber, businessNumber, skills, profession }, handleFailure).then(
                callback
            );
        }
    }

    selectUser(userId, handleFailure) {
        if (!userId) {
            return null;
        }
        if (userId === NEW_ENTITY_ID_PLACEHOLDER) {
            return new Promise((resolve, reject) => this.setState({
                selectedUserId: null,
            }, resolve(this.createEmptyUser())));
        }
        if (this.state.users.has(userId)) {
            return new Promise((resolve, reject) => this.setState({
                selectedUserId: userId,
            }, resolve(this.getSelectedUser())));
        } else {
            return new Promise((resolve, reject) => fetchUser(this.props.sessionState.accessToken, userId, handleFailure).then(user =>
                this.setState(prevState => {
                    const users = new Map(prevState.users);
                    users.set(user.id, user);
                    return {
                        users,
                        selectedUserId: userId,
                    };
                }, resolve(user))
            ).catch(reject));
        }
    }

    getSelectedUser() {
        if (this.state.selectedUserId) {
            return this.state.users.get(this.state.selectedUserId);
        }
        return this.createEmptyUser();
    }

    render() {
        return (
            <UsersContext.Provider
                value={{
                    ...this.state,
                    selectUser: this.selectUser.bind(this),
                    getSelectedUser: this.getSelectedUser.bind(this),
                    createOrUpdateUser: this.createOrUpdateUser.bind(this),
                }}
            >
                {this.props.children}
            </UsersContext.Provider>
        );
    }
}