import React from 'react';
import { fetchNeedsForCalendar } from '../actions/assembled';
import { changeApplicationStateForNeed, createOrUpdateNeed } from '../actions/need';
import { convertToMUIFormat, withContext } from '../util';
import { SessionContext } from './session-provider';

export const NeedsContext = React.createContext();

@withContext('sessionState', SessionContext)
export default class NeedsProvider extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            projects: new Map(),
            openQuantityUpdates: [],
            editedNeedUsers: new Map(),
        };
        this.loadedOwnStates = [];
        this.loadedUsers = [];
    }

    loadNeedsForCalendarBetweenDates(projectId, startDate, endDate, handleFailure) {
        fetchNeedsForCalendar(this.props.sessionState.accessToken, projectId, convertToMUIFormat(startDate), convertToMUIFormat(endDate), handleFailure).then(
            dateMap => this.setState(
                prevState => {
                    let projects = new Map(prevState.projects);
                    if (!projects.has(projectId)) {
                        projects.set(projectId, { id: projectId, days: new Map() });
                    }
                    projects.get(projectId).days = new Map([...dateMap, ...projects.get(projectId).days]);
                    return {
                        projects,
                    };
                }
            )
        )
    }

    getQuantityUpdate({ projectHelperTypeId, date }) {
        return this.state.openQuantityUpdates.find(update => update.projectHelperTypeId === projectHelperTypeId && update.date.isSame(date, 'day'));
    }

    updateNeedQuantity(projectId, helperTypeId, need, handleFailure) {
        let update = this.getQuantityUpdate(need);
        if (update) {
            update.quantity = Math.max(need.quantity, 0);
            return;
        }

        this.setState(prevState => ({
            openQuantityUpdates: [
                ...prevState.openQuantityUpdates,
                {
                    projectHelperTypeId: need.projectHelperTypeId,
                    date: need.date,
                    quantity: need.quantity,
                }
            ]
        }), () => this.updateNeedQuantityRecursively(projectId, helperTypeId, need, handleFailure));
    }

    updateNeedQuantityRecursively(projectId, helperTypeId, need, handleFailure) {
        createOrUpdateNeed(this.props.sessionState.accessToken, need, handleFailure).then(
            need => {
                const update = this.getQuantityUpdate(need);
                if (need.quantity !== update.quantity) {
                    need.quantity = update.quantity;
                    this.updateNeedQuantityRecursively(projectId, helperTypeId, need, handleFailure);
                    return;
                }
                this.setState(
                    prevState => {
                        let projects = new Map(prevState.projects);
                        projects.get(projectId).days.get(convertToMUIFormat(need.date)).helperTypes
                            .find(helperType => helperType.id === helperTypeId).shifts
                            .find(projectHelperType => projectHelperType.id === need.projectHelperTypeId)
                            .need = need;
                        return {
                            projects,
                            openQuantityUpdates: prevState.openQuantityUpdates.filter(update => update.projectHelperTypeId !== need.projectHelperTypeId || !update.date.isSame(need.date, 'day')),
                        };
                    }
                )
            }
        )
    }

    updateOwnNeedState(projectHelperType, needId, state, handleFailure) {
        this.updateNeedUserState(projectHelperType, { needId, state, userId: this.props.sessionState.currentUser.id }, handleFailure);

    }
    updateNeedUserState(projectHelperType, needUser, handleFailure) {
        changeApplicationStateForNeed(this.props.sessionState.accessToken, needUser, handleFailure).then(
            needUser => this.setState(
                prevState => {
                    let projects = new Map(prevState.projects);
                    projects.get(projectHelperType.projectId).days.get(convertToMUIFormat(projectHelperType.need.date)).helperTypes
                        .find(helperType => helperType.id === projectHelperType.helperTypeId).shifts
                        .find(pht => pht.id === projectHelperType.id)
                        .need = {
                        ...projectHelperType.need,
                        state: needUser.state,
                        users: needUser.state === 'NONE'
                            ? projectHelperType.need.users.filter(nu => !nu.needId === needUser.needId || !nu.userId === needUser.userId)
                            : projectHelperType.need.users.find(nu => nu.needId === needUser.needId && nu.userId === needUser.userId)
                                ? projectHelperType.need.users.map(nu => nu.needId === needUser.needId && nu.userId === needUser.userId ? needUser : nu)
                                : [...projectHelperType.need.users, needUser],
                    }
                    return {
                        projects,
                        openQuantityUpdates: prevState.openQuantityUpdates.filter(update => update.projectHelperTypeId !== need.projectHelperTypeId || !update.date.isSame(need.date, 'day')),
                    };
                }
            )
        );
    }

    editNeedUser(needUser) {
        this.setState(
            prevState => {
                let editedNeedUsers = new Map(prevState.editedNeedUsers);
                if (editedNeedUsers.has(needUser.id)) {
                    editedNeedUsers.get(needUser.id).state = needUser.state;
                } else {
                    editedNeedUsers.set(needUser.id, needUser)
                }
                return {
                    editedNeedUsers,
                };
            }
        )
    }

    saveEditedNeedUsers(projectHelperType, handleFailure) {
        Promise.all(Array.from(this.state.editedNeedUsers.values()).filter(
            needUser => needUser.needId === projectHelperType.need.id
        ).map(needUser => changeApplicationStateForNeed(this.props.sessionState.accessToken, needUser, handleFailure))).then(
            needUsers => this.setState(
                prevState => {
                    let editedNeedUsers = new Map(prevState.editedNeedUsers);
                    needUsers.forEach(needUser => editedNeedUsers.delete(needUser.id));
                    let projects = new Map(prevState.projects);
                    projects.get(projectHelperType.projectId).days.get(convertToMUIFormat(projectHelperType.need.date)).helperTypes
                        .find(helperType => helperType.id === projectHelperType.helperTypeId).shifts
                        .find(pht => pht.id === projectHelperType.id)
                        .need.users = projectHelperType.need.users.map(
                            needUser => {
                                const updated = needUsers.find(nu => nu.id === needUser.id);
                                return {
                                    ...needUser,
                                    state: updated ? updated.state : needUser.state,
                                }
                            }
                        )
                    return {
                        projects,
                        editedNeedUsers,
                    }
                }
            )
        )
    }

    hasNeedEditedUsers(needId) {
        return !!Array.from(this.state.editedNeedUsers.values()).find(needUser => needUser.needId === needId);
    }

    getApprovedCount(need) {
        return need && need.users ? need.users.filter(user => user.state === 'APPROVED').length : 0;
    }

    getAppliedCount(need) {
        return need && need.users ? need.users.filter(user => user.state === 'APPLIED').length : 0;
    }

    render() {
        return (
            <NeedsContext.Provider
                value={{
                    ...this.state,
                    updateNeedQuantity: this.updateNeedQuantity.bind(this),
                    getQuantityUpdate: this.getQuantityUpdate.bind(this),
                    getAppliedCount: this.getAppliedCount.bind(this),
                    getApprovedCount: this.getApprovedCount.bind(this),
                    updateOwnNeedState: this.updateOwnNeedState.bind(this),
                    editNeedUser: this.editNeedUser.bind(this),
                    saveEditedNeedUsers: this.saveEditedNeedUsers.bind(this),
                    hasNeedEditedUsers: this.hasNeedEditedUsers.bind(this),
                    loadNeedsForCalendarBetweenDates: this.loadNeedsForCalendarBetweenDates.bind(this),
                }}
            >
                {this.props.children}
            </NeedsContext.Provider>
        );
    }
}

