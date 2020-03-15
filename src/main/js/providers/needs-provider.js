import React from 'react';
import { fetchHelperTypes } from '../actions/helper-type';
import { createOrUpdateNeed, fetchNeedByProjectHelperTypeIdAndDate } from '../actions/need';
import { fetchProjectHelperTypes } from '../actions/project';
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
        };
        // necessary to stop sending redundant http requests e.g. in quantities component
        // otherwise loadHelperTypesWithNeedsByProjectIdAndDate bc state changes and it changes the state itself => infinite loop
        // cleaner solutions welcome ;)
        this.openLoadRequests = [];
    }

    getOpenRequest(projectId, date) {
        return this.openLoadRequests.find(request => request.projectId === projectId && request.date.isSame(date, 'day'));
    }

    startLoadRequest(projectId, date) {
        if (!this.getOpenRequest(projectId, date)) {
            this.openLoadRequests.push({ projectId, date });
        }
    }

    stopLoadRequest(projectId, date) {
        this.openLoadRequests = this.openLoadRequests.filter(request => request.projectId !== projectId || !request.date.isSame(date, 'day'));
    }

    loadHelperTypesWithNeedsByProjectIdAndDate(projectId, date, handleFailure) {
        if (!this.state.projects.has(projectId)) {
            this.setState(
                prevState => ({
                    projects: prevState.projects.set(projectId, { id: projectId, days: new Map() })
                }), () => {
                    this.loadHelperTypesWithNeeds(projectId, date, handleFailure);
                }
            )
        } else {
            this.loadHelperTypesWithNeeds(projectId, date, handleFailure);
        }
    }

    loadHelperTypesWithNeeds(projectId, date, handleFailure) {
        if (this.getOpenRequest(projectId, date)) {
            return;
        }

        if (!this.state.projects.get(projectId).days.has(convertToMUIFormat(date))) {
            this.startLoadRequest(projectId, date);
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
                    }, () => this.stopLoadRequest(projectId, date)
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

    setTest(test) {
        this.setState({
            test
        })
    }
    render() {
        return (
            <NeedsContext.Provider
                value={{
                    ...this.state,
                    loadHelperTypesWithNeedsByProjectIdAndDate: this.loadHelperTypesWithNeedsByProjectIdAndDate.bind(this),
                    updateNeedQuantity: this.updateNeedQuantity.bind(this),
                    getQuantityUpdate: this.getQuantityUpdate.bind(this),
                }}
            >
                {this.props.children}
            </NeedsContext.Provider>
        );
    }
}
