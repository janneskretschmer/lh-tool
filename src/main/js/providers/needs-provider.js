import React from 'react';
import { fetchHelperTypes } from '../actions/helper-type';
import { createOrUpdateNeed, fetchNeedByProjectHelperTypeIdAndDate, fetchOwnNeedUser, fetchNeedUsers, changeApplicationStateForNeed } from '../actions/need';
import { fetchProjectHelperTypes } from '../actions/project';
import { convertToMUIFormat, withContext, convertFromMUIFormat } from '../util';
import { SessionContext } from './session-provider';
import { fetchUser } from '../actions/user';
import { fetchNeedsForCalendar } from '../actions/assembled';

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
                    Object.keys(dateMap).forEach(
                        dateString => projects.get(projectId).days.set(dateString, { date: convertFromMUIFormat(dateString), helperTypes: dateMap[dateString].helperTypes })
                    );
                    return {
                        projects,
                    };
                }
            )
        )
    }

    loadHelperTypesWithNeedsByProjectIdAndDate(projectId, date, handleFailure, callback) {
        if (!this.state.projects.has(projectId)) {
            this.setState(
                prevState => ({
                    projects: prevState.projects.set(projectId, { id: projectId, days: new Map() })
                }), () => {
                    this.loadHelperTypesWithNeeds(projectId, date, handleFailure, callback);
                }
            )
        } else {
            this.loadHelperTypesWithNeeds(projectId, date, handleFailure, callback);
        }
    }

    loadHelperTypesWithNeeds(projectId, date, handleFailure, callback) {
        if (!this.state.projects.get(projectId).days.has(convertToMUIFormat(date))) {
            fetchHelperTypes(this.props.sessionState.accessToken, projectId, date.isoWeekday(), handleFailure).then(
                helperTypes => Promise.all(helperTypes.map(
                    helperType =>

                        fetchProjectHelperTypes(this.props.sessionState.accessToken, projectId, helperType.id, date.isoWeekday(), handleFailure).then(
                            projectHelperTypes => Promise.all(projectHelperTypes.map(
                                projectHelperType =>

                                    fetchNeedByProjectHelperTypeIdAndDate(this.props.sessionState.accessToken, projectHelperType.id, convertToMUIFormat(date), handleFailure).then(
                                        need => ({
                                            ...projectHelperType,
                                            need,
                                        })
                                    )
                            )).then(
                                shifts => ({
                                    ...helperType,
                                    shifts,
                                })
                            )
                        )
                ))
            ).then(
                helperTypes => this.setState(
                    prevState => {
                        let projects = new Map(prevState.projects);
                        projects.get(projectId).days.set(convertToMUIFormat(date), { date, helperTypes });
                        return {
                            projects,
                        };
                    }, () => {
                        if (callback) {
                            callback();
                        }
                    })
            );
        }
    }

    loadHelperTypesWithNeedsAndCurrentUserByProjectIdAndDate(projectId, date, handleFailure) {
        if (!this.state.projects.has(projectId) || !this.state.projects.get(projectId).days.has(convertToMUIFormat(date))) {
            this.loadHelperTypesWithNeedsByProjectIdAndDate(projectId, date, handleFailure,
                () => this.loadHelperTypesWithNeedsAndCurrentUser(projectId, date, handleFailure));
        } else {
            this.loadHelperTypesWithNeedsAndCurrentUser(projectId, date, handleFailure)
        }
    }

    loadHelperTypesWithNeedsAndCurrentUser(projectId, date, handleFailure, callback) {
        if (!this.loadedOwnStates.find(projectDate => projectDate.projectId === projectId && projectDate.date.isSame(date, 'day'))) {
            Promise.all(
                this.state.projects.get(projectId).days.get(convertToMUIFormat(date)).helperTypes.map(
                    helperType => {
                        return Promise.all(helperType.shifts.map(
                            projectHelperType => {
                                if (projectHelperType.need.id) {
                                    //two requests necessary, bc it returns a new needUser object with state NONE if userId is set as default
                                    //in the second request it wouldn't be in the list
                                    return fetchOwnNeedUser(this.props.sessionState.accessToken, projectHelperType.need.id, this.props.sessionState.currentUser.id).then(
                                        needUsers => ({
                                            ...projectHelperType,
                                            need: {
                                                ...projectHelperType.need,
                                                state: needUsers.state,
                                            }
                                        })
                                    ).then(
                                        //nesting necessary to keep own state
                                        pht => fetchNeedUsers(this.props.sessionState.accessToken, pht.need.id).then(
                                            needUsers => ({
                                                ...pht,
                                                need: {
                                                    ...pht.need,
                                                    users: needUsers
                                                },
                                            })
                                        )
                                    ).catch(handleFailure);
                                }
                                return Promise.resolve(projectHelperType);
                            }
                        )).then(
                            shifts => ({
                                ...helperType,
                                shifts,
                            })
                        )
                    }
                )
            ).then(
                helperTypes => this.setState(
                    prevState => {
                        let projects = new Map(prevState.projects);
                        projects.get(projectId).days.set(convertToMUIFormat(date), { date, helperTypes });
                        return {
                            projects,
                        };
                    }, () => {
                        this.loadedOwnStates.push({ projectId, date });
                        if (callback) {
                            callback();
                        }
                    }
                )
            );
        } else if (callback) {
            callback();
        }
    }

    loadHelperTypesWithNeedsAndUsersByProjectIdAndDate(projectId, date, handleFailure) {
        if (!this.state.projects.has(projectId) || !this.state.projects.get(projectId).days.has(convertToMUIFormat(date))) {
            this.loadHelperTypesWithNeedsByProjectIdAndDate(projectId, date, handleFailure,
                () => this.loadHelperTypesWithNeedsAndCurrentUser(projectId, date, handleFailure,
                    () => this.loadHelperTypesWithNeedsAndUsers(projectId, date, handleFailure)));
        } else if (!this.loadedOwnStates.find(projectDate => projectDate.projectId === projectId && projectDate.date.isSame(date, 'day'))) {
            this.loadHelperTypesWithNeedsAndCurrentUser(projectId, date, handleFailure,
                () => this.loadHelperTypesWithNeedsAndUsers(projectId, date, handleFailure));
        } else {
            this.loadHelperTypesWithNeedsAndUsers(projectId, date, handleFailure);
        }
    }

    loadHelperTypesWithNeedsAndUsers(projectId, date, handleFailure) {
        if (!this.loadedUsers.find(projectDate => projectDate.projectId === projectId && projectDate.date.isSame(date, 'day'))) {
            Promise.all(
                this.state.projects.get(projectId).days.get(convertToMUIFormat(date)).helperTypes.map(
                    helperType => {
                        return Promise.all(helperType.shifts.map(
                            projectHelperType => {
                                if (projectHelperType.need.users) {
                                    return Promise.all(projectHelperType.need.users.map(
                                        needUser => fetchUser({ accessToken: this.props.sessionState.accessToken, userId: needUser.userId }, handleFailure).then(
                                            user => ({
                                                ...needUser,
                                                user
                                            })
                                        )
                                    )).then(
                                        needUsers => ({
                                            ...projectHelperType,
                                            need: {
                                                ...projectHelperType.need,
                                                users: needUsers,
                                            }
                                        })
                                    )
                                }
                                return Promise.resolve(projectHelperType);
                            }
                        )).then(
                            shifts => ({
                                ...helperType,
                                shifts,
                            })
                        )
                    }
                )
            ).then(
                helperTypes => this.setState(
                    prevState => {
                        let projects = new Map(prevState.projects);
                        projects.get(projectId).days.set(convertToMUIFormat(date), { date, helperTypes });
                        return {
                            projects,
                        };
                    }, () => {
                        this.loadedUsers.push({ projectId, date });
                    }
                )
            );
        }
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
                    loadHelperTypesWithNeedsByProjectIdAndDate: this.loadHelperTypesWithNeedsByProjectIdAndDate.bind(this),
                    loadHelperTypesWithNeedsAndCurrentUserByProjectIdAndDate: this.loadHelperTypesWithNeedsAndCurrentUserByProjectIdAndDate.bind(this),
                    loadHelperTypesWithNeedsAndUsersByProjectIdAndDate: this.loadHelperTypesWithNeedsAndUsersByProjectIdAndDate.bind(this),
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

