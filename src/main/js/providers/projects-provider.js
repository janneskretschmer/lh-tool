import React from 'react';
import { withContext, isStringBlank, convertFromMUIFormat } from '../util';
import { fetchProjects, fetchProjectHelperTypes, fetchProject, updateProject, createProject } from '../actions/project';
import { SessionContext } from './session-provider';
import { fetchHelperTypes } from '../actions/helper-type';
import moment, { weekdays } from 'moment';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';

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

    handleUpdatedAndSelectedProject(project) {
        this.setState(prevState => {
            const projects = new Map(prevState.projects);
            projects.set(project.id, project);
            return {
                projects,
                selectedProject: project,
            };
        });
    }

    saveSelectedProject() {
        if (!this.isProjectValid()) {
            return Promise.reject();
        }
        const project = this.state.selectedProject;
        let projectPromise;
        if (project.id) {
            projectPromise = updateProject(this.props.sessionState.accessToken, project);
        } else {
            projectPromise = createProject(this.props.sessionState.accessToken, project)
            // .then(savedProject => Promise.all(
            //     this.flattenShiftMap(project.shifts).map(shift => ({
            //     ...shift,
            //     projectId: savedProject.id,
            //     }).map(shift => sa)));
        }
        return projectPromise.then(savedProject => new Promise((resolve, reject) => this.setState({ selectedProject: savedProject }, resolve(savedProject))));
    }

    flattenShiftMap(shifts) {
        return [...shifts.values()].map(helperTypeMap => [...helperTypeMap.values()]);
    }


    loadShiftsForSelectedProject() {
        if (this.state.selectedProject && this.state.selectedProject.id) {
            fetchProjectHelperTypes(this.props.sessionState.accessToken, this.state.selectedProject.id).then(shifts => {
                const project = { ...this.state.selectedProject };
                shifts.forEach(shift => {
                    project.shifts.get(shift.weekday).get(shift.helperTypeId).push(shift);
                })
                this.handleUpdatedAndSelectedProject(project);
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
            const shift = this.state.selectedShift;
            const project = { ...this.state.selectedProject };
            const shifts = project.shifts.get(shift.weekday).get(shift.helperTypeId).filter(cachedShift => !cachedShift.id || cachedShift.id !== shift.id);
            project.shifts.get(shift.weekday).set(shift.helperTypeId, [...shifts, shift]);
            this.setState({
                selectedProject: project,
                selectedShift: null,
            });
        }
    }

    removeShift(shift) {
        const project = { ...this.state.selectedProject };
        const shifts = project.shifts.get(shift.weekday).get(shift.helperTypeId).filter(cachedShift => cachedShift.id !== shift.id || cachedShift.startTime !== shift.startTime);
        project.shifts.get(shift.weekday).set(shift.helperTypeId, shifts);
        this.setState({
            selectedProject: project,
            selectedShift: null,
        });
    }

    // FUTURE clean dynamic solution
    addStandardShifts() {
        const project = { ...this.state.selectedProject };
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
        })
        this.setState({ selectedProject: project });
    }

    // FUTURE clean dynamic solution
    addGateKeeperShifts() {
        const project = { ...this.state.selectedProject };
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
        this.setState({ selectedProject: project });
    }

    // FUTURE clean dynamic solution
    addSecurityShifts() {
        const project = { ...this.state.selectedProject };
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
        })
        this.setState({ selectedProject: project });
    }

    isShiftValid() {
        const shift = this.state.selectedShift;
        return !isStringBlank(shift.startTime) && shift.helperTypeId && shift.weekday && shift.projectId;
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
        let startDate = convertFromMUIFormat(date).utc(true);
        if (this.state.selectedProject.endDate) {
            startDate = moment.min(startDate, this.state.selectedProject.endDate);
        }
        this.setState(prevState => ({
            selectedProject: {
                ...prevState.selectedProject,
                startDate,
            }
        }));
    }

    changeProjectEndDate(date) {
        if (isStringBlank(date)) {
            return;
        }
        let endDate = convertFromMUIFormat(date).utc(true);
        if (this.state.selectedProject.startDate) {
            endDate = moment.max(endDate, this.state.selectedProject.startDate);
        }
        this.setState(prevState => ({
            selectedProject: {
                ...prevState.selectedProject,
                endDate,
            }
        }));
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

    render() {
        return (
            <ProjectsContext.Provider
                value={{
                    ...this.state,
                    getCurrentShifts: this.getCurrentShifts.bind(this),
                    getHelperTypeName: this.getHelperTypeName.bind(this),

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
                }}
            >
                {this.props.children}
            </ProjectsContext.Provider>
        );
    }


}