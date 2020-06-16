import React from 'react';
import { fetchNeedsForCalendar } from '../actions/assembled';
import { changeApplicationStateForNeed, createOrUpdateNeed } from '../actions/need';
import { convertToMUIFormat, withContext, getProjectMonth, getMonthOffsetWithinRange, getClosestProjectMonth } from '../util';
import { SessionContext } from './session-provider';
import { fetchProjects } from '../actions/project';
import _ from 'lodash';

export const NeedsContext = React.createContext();

class StatefulNeedsProvider extends React.Component {

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

    componentDidMount() {
        fetchProjects(this.props.sessionState.accessToken).then(receivedProjects => {
            this.setState(prevState => {
                const projects = _.cloneDeep(prevState.projects);
                receivedProjects.forEach(project => {
                    const selectedMonthData = getClosestProjectMonth(0, project.startDate, project.endDate);
                    if (!projects.has(project.id)) {
                        projects.set(project.id, {
                            ...project,
                            selectedMonthData,
                            loadingMonthData: false,
                        });
                    }
                });
                return {
                    projects,
                    selectedProjectId: receivedProjects.length > 0 && receivedProjects[0].id,
                };
            });
        });
    }

    componentDidUpdate() {
        const project = this.getSelectedProject();
        if (project && !project.loadingMonthData && project.selectedMonthData
            && (!project.days
                || !project.days.has(convertToMUIFormat(project.selectedMonthData.firstValidDate))
                || !project.days.has(convertToMUIFormat(project.selectedMonthData.lastValidDate))
            )
        ) {
            this.setState(prevState => {
                const projects = _.cloneDeep(prevState.projects);
                projects.get(project.id).loadingMonthData = true;
                if (!project.days) {
                    projects.get(project.id).days = new Map();
                }
                return { projects };
            }, () =>
                fetchNeedsForCalendar(this.props.sessionState.accessToken, project.id, convertToMUIFormat(project.selectedMonthData.firstValidDate), convertToMUIFormat(project.selectedMonthData.lastValidDate))
                    .then(dateMap => {
                        this.setState(prevState => {
                            const tmpProjects = _.cloneDeep(prevState.projects);
                            tmpProjects.get(project.id).days = new Map([...dateMap, ...tmpProjects.get(project.id).days]);
                            tmpProjects.get(project.id).loadingMonthData = false;
                            return {
                                projects: tmpProjects,
                            };
                        });
                    })
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
                this.setState(prevState => {
                    const projects = _.cloneDeep(prevState.projects);
                    const cachedNeed = projects.get(projectId).days.get(convertToMUIFormat(need.date)).helperTypes
                        .find(helperType => helperType.id === helperTypeId).shifts
                        .find(projectHelperType => projectHelperType.id === need.projectHelperTypeId)
                        .need;
                    cachedNeed.quantity = need.quantity;
                    if (!cachedNeed.id) {
                        cachedNeed.id = need.id;
                        cachedNeed.state = 'NONE';
                        cachedNeed.users = [];
                    }

                    const openQuantityUpdates = prevState.openQuantityUpdates.filter(update => update.projectHelperTypeId !== need.projectHelperTypeId || !update.date.isSame(need.date, 'day'));
                    return {
                        projects,
                        openQuantityUpdates,
                    };
                });
            }
        );
    }

    updateOwnNeedState(projectHelperType, needId, state, handleFailure) {
        this.updateNeedUserState(projectHelperType, { needId, state, userId: this.props.sessionState.currentUser.id }, handleFailure);

    }
    updateNeedUserState(projectHelperType, needUser, handleFailure) {
        changeApplicationStateForNeed(this.props.sessionState.accessToken, needUser, handleFailure).then(
            needUser => this.setState(
                prevState => {

                    if (needUser.userId === this.props.sessionState.currentUser.id) {
                        needUser.user = this.props.sessionState.currentUser;
                    }

                    const projects = _.cloneDeep(prevState.projects);
                    const need = projects.get(projectHelperType.projectId).days.get(convertToMUIFormat(projectHelperType.need.date)).helperTypes
                        .find(helperType => helperType.id === projectHelperType.helperTypeId).shifts
                        .find(pht => pht.id === projectHelperType.id)
                        .need;
                    need.state = needUser.state;

                    if (needUser.state === 'NONE') {
                        need.users = projectHelperType.need.users.filter(nu => nu.needId !== needUser.needId || nu.userId !== needUser.userId);
                    } else {
                        if (projectHelperType.need.users.find(nu => nu.needId === needUser.needId && nu.userId === needUser.userId)) {
                            need.users = projectHelperType.need.users.map(nu => nu.needId === needUser.needId && nu.userId === needUser.userId ? needUser : nu);
                        } else {
                            need.users = [...projectHelperType.need.users, needUser];
                        }
                    }

                    return {
                        projects,
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
                    editedNeedUsers.set(needUser.id, needUser);
                }
                return {
                    editedNeedUsers,
                };
            }
        );
    }

    saveEditedNeedUsers(projectHelperType, handleFailure) {
        Promise.all(Array.from(this.state.editedNeedUsers.values()).filter(
            needUser => needUser.needId === projectHelperType.need.id
        ).map(needUser => changeApplicationStateForNeed(this.props.sessionState.accessToken, needUser, handleFailure))).then(
            needUsers => this.setState(
                prevState => {
                    const editedNeedUsers = _.cloneDeep(prevState.editedNeedUsers);
                    needUsers.forEach(needUser => editedNeedUsers.delete(needUser.id));
                    const projects = _.cloneDeep(prevState.projects);
                    const need = projects.get(projectHelperType.projectId).days.get(convertToMUIFormat(projectHelperType.need.date)).helperTypes
                        .find(helperType => helperType.id === projectHelperType.helperTypeId).shifts
                        .find(pht => pht.id === projectHelperType.id)
                        .need;
                    need.users = projectHelperType.need.users.map(
                        needUser => {
                            const updated = needUsers.find(nu => nu.id === needUser.id);
                            return {
                                ...needUser,
                                state: updated ? updated.state : needUser.state,
                            };
                        }
                    )
                    const userState = need.users.find(needUser => needUser.userId === this.props.sessionState.currentUser.id);
                    need.state = userState ? userState.state : 'NONE';
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
        return need && need.users ? need.users.filter(needUser => (this.state.editedNeedUsers.get(needUser.id) || needUser).state === 'APPROVED').length : 0;
    }

    getAppliedCount(need) {
        return need && need.users ? need.users.filter(user => user.state === 'APPLIED').length : 0;
    }

    getSelectedProject() {
        return this.state.projects.get(this.state.selectedProjectId);
    }


    selectProject(selectedProjectId) {
        this.setState({ selectedProjectId });
    }

    selectMonth(monthOffset) {
        this.setState(prevState => {
            const projects = _.cloneDeep(prevState.projects);
            const project = projects.get(prevState.selectedProjectId);
            project.selectedMonthData = getClosestProjectMonth(monthOffset, project.startDate, project.endDate);
            return { projects };
        });
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

                    getSelectedProject: this.getSelectedProject.bind(this),
                    selectProject: this.selectProject.bind(this),
                    selectMonth: this.selectMonth.bind(this),
                }}
            >
                {this.props.children}
            </NeedsContext.Provider>
        );
    }
}

const NeedsProvider = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                (<StatefulNeedsProvider {...props} sessionState={sessionState} />)
            )}
        </SessionContext.Consumer>
    </>
);
export default NeedsProvider;