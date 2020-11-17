import React from 'react';
import { withContext, isStringBlank, convertFromMUIFormat, shallowEquals } from '../util';
import { fetchProjects, fetchProjectHelperTypes, fetchProject, updateProject, createProject, createProjectHelperType, updateProjectHelperType, deleteProjectHelperType } from '../actions/project';
import { SessionContext } from './session-provider';
import { fetchHelperTypes } from '../actions/helper-type';
import moment, { weekdays } from 'moment';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import _ from 'lodash';
import { RIGHT_PROJECTS_POST } from '../permissions';

export const ProjectsContext = React.createContext();

@withContext('sessionState', SessionContext)
export default class ProjectsProvider extends React.Component {

    //FUTURE clean dynamic solution
    HELPER_TYPE_CONSTRUCTION_ID = 1;
    HELPER_TYPE_KITCHEN_ID = 2;
    HELPER_TYPE_STORE_KEEPER_ID = 3;
    HELPER_TYPE_DRIVER_ID = 4;
    HELPER_TYPE_GATE_KEEPER_ID = 5;
    HELPER_TYPE_CLEANER_ID = 6;
    HELPER_TYPE_SECURITY_DAY_ID = 7;
    HELPER_TYPE_SECURITY_NIGHT_ID = 8;

    constructor(props) {
        super(props);
        this.state = {
            projects: new Map(),
            helperTypes: null,
            selectedProject: null,
            // entity of the edit dialog
            selectedShift: null,
        }
    }

    componentDidMount() {
        fetchHelperTypes(this.props.sessionState.accessToken).then(helperTypes => this.setState({ helperTypes }))
    }

    componentDidUpdate() {
        if (this.state.selectedProject && this.state.helperTypes && !this.state.selectedProject.shifts) {
            const shifts = new Map();
            // Monday: 1, ..., Sunday: 7
            [...Array(7).keys()].forEach(dayIndex => {
                const helperTypeMap = new Map();
                this.state.helperTypes.forEach(helperType => helperTypeMap.set(helperType.id, new Array()));
                shifts.set(dayIndex + 1, helperTypeMap);
            });
            this.setState(prevState => ({
                selectedProject: {
                    ...prevState.selectedProject,
                    shifts,
                }
            }), () => this.loadShiftsForSelectedProject());
        }
    }

    loadEditableProjects() {
        return fetchProjects(this.props.sessionState.accessToken).then(projects => {
            const cachedProjects = _.cloneDeep(this.state.projects);
            projects.forEach(project => {
                if (!cachedProjects.has(project.id)) {
                    cachedProjects.set(project.id, project);
                }
            });
            this.setState({ projects: cachedProjects });
        });
    }

    createEmptyProject() {
        return { name: "", startDate: null, endDate: null };
    }

    selectProject(projectId, handleFailure) {
        if (!projectId) {
            return;
        }

        if (projectId === NEW_ENTITY_ID_PLACEHOLDER) {
            this.setState({
                selectedProject: this.createEmptyProject()
            });
            return;
        }
        const parsedProjectId = parseInt(projectId);
        if (this.state.projects.has(parsedProjectId)) {
            const cachedProject = this.state.projects.get(parsedProjectId);
            if (cachedProject.shifts) {
                this.setState({
                    selectedProject: cachedProject,
                });
            }
        } else {
            fetchProject(this.props.sessionState.accessToken, parsedProjectId)
                .then(project => this.handleUpdatedAndSelectedProject(project))
                .catch(error => handleFailure(error));
        }
    }

    handleUpdatedAndSelectedProject(project, callback) {
        this.setState(prevState => {
            const projects = _.cloneDeep(prevState.projects);
            projects.set(project.id, project);
            return {
                projects,
                selectedProject: _.cloneDeep(project),
            };
        }, callback);
    }

    saveSelectedProject() {
        if (!this.isProjectValid()) {
            return Promise.reject();
        }
        const project = this.state.selectedProject;
        const accessToken = this.props.sessionState.accessToken;
        let projectPromise;
        if (project.id) {
            projectPromise = updateProject(accessToken, project)
                .then(savedProject => {
                    const oldShifts = this.flattenShiftMap(this.state.projects.get(project.id).shifts);
                    const newShifts = this.flattenShiftMap(project.shifts);

                    const addedShifts = newShifts.filter(shift => !shift.id);
                    const deletedShifts = [];
                    const updatedShifts = [];
                    oldShifts.forEach(shift => {
                        const modifiedShift = newShifts.find(newShift => newShift.id === shift.id);
                        if (!modifiedShift) {
                            deletedShifts.push(shift);
                        } else if (!_.isEqual(shift, modifiedShift)) {
                            updatedShifts.push(modifiedShift);
                        }
                    });

                    return Promise.all([
                        ...addedShifts.map(shift => createProjectHelperType(accessToken, shift)),
                        ...updatedShifts.map(shift => updateProjectHelperType(accessToken, shift)),
                        ...deletedShifts.map(shift => deleteProjectHelperType(accessToken, shift))
                    ]).then(() => savedProject);
                });
        } else {
            projectPromise = createProject(accessToken, project)
                .then(savedProject => Promise.all(
                    this.flattenShiftMap(project.shifts).map(shift => ({
                        ...shift,
                        projectId: savedProject.id,
                    })).map(shift => createProjectHelperType(accessToken, shift))
                ).then(() => savedProject));
        }
        return projectPromise.then(savedProject => new Promise((resolve, reject) => this.handleUpdatedAndSelectedProject(savedProject, resolve(savedProject))));
    }

    flattenShiftMap(shifts) {
        return [...shifts.values()].map(helperTypeMap => [...helperTypeMap.values()]).flat(2);
    }


    loadShiftsForSelectedProject() {
        const project = this.state.selectedProject;
        if (project && project.id) {
            fetchProjectHelperTypes(this.props.sessionState.accessToken, project.id).then(shifts => {
                const tmpProject = { ...project };
                shifts.forEach(shift => {
                    tmpProject.shifts.get(shift.weekday).get(shift.helperTypeId).push(shift);
                })
                this.handleUpdatedAndSelectedProject(tmpProject);
            });
        }
    }

    resetSelectedProject() {
        this.selectProject(this.state.selectedProject.id ? this.state.selectedProject.id : NEW_ENTITY_ID_PLACEHOLDER);
    }

    createShift(weekday) {
        this.changeShift({
            projectId: this.state.selectedProject.id,
            weekday,
            startTime: '07:00',
            endTime: '17:00',
            helperTypeId: this.state.helperTypes[0].id,
        });
    }

    applySelectedShift() {
        if (this.isShiftValid()) {
            this.setState(prevState => {
                const shift = prevState.selectedShift;
                const project = _.cloneDeep(prevState.selectedProject);
                const shifts = project.shifts.get(shift.weekday).get(shift.helperTypeId).filter(cachedShift => !cachedShift.id || cachedShift.id !== shift.id);
                project.shifts.get(shift.weekday).set(shift.helperTypeId, [...shifts, shift]);
                return {
                    selectedProject: project,
                    selectedShift: null,
                };
            });
        }
    }

    removeShift(shift) {
        this.setState(prevState => {
            const project = _.cloneDeep(prevState.selectedProject);
            const shifts = project.shifts.get(shift.weekday).get(shift.helperTypeId).filter(cachedShift => cachedShift.id !== shift.id || cachedShift.startTime !== shift.startTime);
            project.shifts.get(shift.weekday).set(shift.helperTypeId, shifts);
            return {
                selectedProject: project,
                selectedShift: null,
            };
        });
    }

    // FUTURE clean dynamic solution
    addStandardShifts() {
        this.setState(prevState => {
            const project = _.cloneDeep(prevState.selectedProject);
            const standardShifts = [
                // Tuesday
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_CONSTRUCTION_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_KITCHEN_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_STORE_KEEPER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_DRIVER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_CLEANER_ID, startTime: '07:00' },
                // Wednesday
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_CONSTRUCTION_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_KITCHEN_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_STORE_KEEPER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_DRIVER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_CLEANER_ID, startTime: '07:00' },
                // Thursday
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_CONSTRUCTION_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_KITCHEN_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_STORE_KEEPER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_DRIVER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_CLEANER_ID, startTime: '07:00' },
                // Friday
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_CONSTRUCTION_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_KITCHEN_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_STORE_KEEPER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_DRIVER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_CLEANER_ID, startTime: '07:00' },
                // Saturday
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_CONSTRUCTION_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_KITCHEN_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_STORE_KEEPER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_DRIVER_ID, startTime: '07:00', endTime: '17:00' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_CLEANER_ID, startTime: '07:00' },
            ];
            standardShifts.forEach(shift => {
                if (!project.shifts.get(shift.weekday).get(shift.helperTypeId).find(cachedShift => cachedShift.startTime === shift.startTime)) {
                    project.shifts.get(shift.weekday).get(shift.helperTypeId).push(shift);
                }
            });
            return { selectedProject: project }
        });
    }

    // FUTURE clean dynamic solution
    addGateKeeperShifts() {
        this.setState(prevState => {
            const project = _.cloneDeep(prevState.selectedProject);
            const gateKeeperShifts = [
                // Tuesday
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '07:00', endTime: '12:30' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '12:30', endTime: '17:00' },
                // Wednesday
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '07:00', endTime: '12:30' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '12:30', endTime: '17:00' },
                // Thursday
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '07:00', endTime: '12:30' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '12:30', endTime: '17:00' },
                // Friday
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '07:00', endTime: '12:30' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '12:30', endTime: '17:00' },
                // Saturday
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '07:00', endTime: '12:30' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_GATE_KEEPER_ID, startTime: '12:30', endTime: '17:00' },
            ];
            gateKeeperShifts.forEach(shift => {
                if (!project.shifts.get(shift.weekday).get(shift.helperTypeId).find(cachedShift => cachedShift.startTime === shift.startTime)) {
                    project.shifts.get(shift.weekday).get(shift.helperTypeId).push(shift);
                }
            })
            return { selectedProject: project };
        });
    }

    // FUTURE clean dynamic solution
    addSecurityShifts() {
        this.setState(prevState => {
            const project = _.cloneDeep(prevState.selectedProject);
            const securityShifts = [
                // Monday
                { projectId: project.id, weekday: 1, helperTypeId: this.HELPER_TYPE_SECURITY_DAY_ID, startTime: '07:00', endTime: '10:00' },
                { projectId: project.id, weekday: 1, helperTypeId: this.HELPER_TYPE_SECURITY_DAY_ID, startTime: '10:00', endTime: '14:00' },
                { projectId: project.id, weekday: 1, helperTypeId: this.HELPER_TYPE_SECURITY_DAY_ID, startTime: '14:00', endTime: '17:00' },
                { projectId: project.id, weekday: 1, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 1, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 1, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
                // Tuesday
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 2, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
                // Wednesday
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 3, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
                // Thursday
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 4, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
                // Friday
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 5, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
                // Saturday
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 6, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
                // Sunday
                { projectId: project.id, weekday: 7, helperTypeId: this.HELPER_TYPE_SECURITY_DAY_ID, startTime: '07:00', endTime: '10:00' },
                { projectId: project.id, weekday: 7, helperTypeId: this.HELPER_TYPE_SECURITY_DAY_ID, startTime: '10:00', endTime: '14:00' },
                { projectId: project.id, weekday: 7, helperTypeId: this.HELPER_TYPE_SECURITY_DAY_ID, startTime: '14:00', endTime: '17:00' },
                { projectId: project.id, weekday: 7, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '02:00', endTime: '07:00' },
                { projectId: project.id, weekday: 7, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '17:00', endTime: '22:00' },
                { projectId: project.id, weekday: 7, helperTypeId: this.HELPER_TYPE_SECURITY_NIGHT_ID, startTime: '22:00', endTime: '02:00' },
            ];
            securityShifts.forEach(shift => {
                if (!project.shifts.get(shift.weekday).get(shift.helperTypeId).find(cachedShift => cachedShift.startTime === shift.startTime)) {
                    project.shifts.get(shift.weekday).get(shift.helperTypeId).push(shift);
                }
            });
            return { selectedProject: project };
        });
    }

    isShiftValid() {
        const shift = this.state.selectedShift;
        return !isStringBlank(shift.startTime) && shift.helperTypeId && shift.weekday && (!this.state.selectedProject.id || shift.projectId);
    }

    isProjectValid() {
        const project = this.state.selectedProject;
        return project && !isStringBlank(project.name) && project.startDate && project.endDate;
    }

    getCurrentShifts() {
        return this.state.selectedProject && this.state.selectedProject.shifts;
    }

    getHelperTypeName({ helperTypeId }) {
        const helperType = this.state.helperTypes && this.state.helperTypes.find(helperType => helperType.id === helperTypeId)
        return helperType && helperType.name;
    }


    changeSelectedProjectId(selectedProjectId) {
        this.setState({ selectedProjectId },
            () => this.loadShiftsForSelectedProject());
    }

    changeProjectName(name) {
        this.setState(prevState => ({
            selectedProject: {
                ...prevState.selectedProject,
                name,
            }
        }));
    }

    changeProjectStartDate(date) {
        if (isStringBlank(date)) {
            return;
        }
        this.setState(prevState => {
            let startDate = convertFromMUIFormat(date).utc(true);
            if (prevState.selectedProject.endDate) {
                startDate = moment.max(startDate, prevState.selectedProject.endDate);
            }
            return {
                selectedProject: {
                    ...prevState.selectedProject,
                    startDate,
                }
            }
        });
    }

    changeProjectEndDate(date) {
        if (isStringBlank(date)) {
            return;
        }
        this.setState(prevState => {
            let endDate = convertFromMUIFormat(date).utc(true);
            if (prevState.selectedProject.startDate) {
                endDate = moment.max(endDate, prevState.selectedProject.startDate);
            }
            return {
                selectedProject: {
                    ...prevState.selectedProject,
                    endDate,
                }
            }
        });
    }

    changeShift(selectedShift) {
        this.setState({
            selectedShift,
        });
    }

    changeShiftStartTime(startTime) {
        this.setState(prevState => ({
            selectedShift: {
                ...prevState.selectedShift,
                startTime,
            }
        }));
    }

    changeShiftEndTime(endTime) {
        this.setState(prevState => ({
            selectedShift: {
                ...prevState.selectedShift,
                endTime,
            }
        }));
    }

    changeShiftHelperTypeId(helperTypeId) {
        this.setState(prevState => ({
            selectedShift: {
                ...prevState.selectedShift,
                helperTypeId,
            }
        }));
    }

    isAllowedToCreate() {
        return this.props.sessionState.hasPermission(RIGHT_PROJECTS_POST);
    }

    render() {
        return (
            <ProjectsContext.Provider
                value={{
                    ...this.state,
                    getCurrentShifts: this.getCurrentShifts.bind(this),
                    getHelperTypeName: this.getHelperTypeName.bind(this),

                    loadEditableProjects: this.loadEditableProjects.bind(this),
                    selectProject: this.selectProject.bind(this),
                    saveSelectedProject: this.saveSelectedProject.bind(this),
                    resetSelectedProject: this.resetSelectedProject.bind(this),
                    isProjectValid: this.isProjectValid.bind(this),

                    createShift: this.createShift.bind(this),
                    changeShift: this.changeShift.bind(this),
                    applySelectedShift: this.applySelectedShift.bind(this),
                    removeShift: this.removeShift.bind(this),
                    isShiftValid: this.isShiftValid.bind(this),
                    addStandardShifts: this.addStandardShifts.bind(this),
                    addGateKeeperShifts: this.addGateKeeperShifts.bind(this),
                    addSecurityShifts: this.addSecurityShifts.bind(this),

                    changeSelectedProjectId: this.changeSelectedProjectId.bind(this),
                    changeProjectName: this.changeProjectName.bind(this),
                    changeProjectStartDate: this.changeProjectStartDate.bind(this),
                    changeProjectEndDate: this.changeProjectEndDate.bind(this),

                    changeShiftStartTime: this.changeShiftStartTime.bind(this),
                    changeShiftEndTime: this.changeShiftEndTime.bind(this),
                    changeShiftHelperTypeId: this.changeShiftHelperTypeId.bind(this),

                    isAllowedToCreate: this.isAllowedToCreate.bind(this),
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }


}