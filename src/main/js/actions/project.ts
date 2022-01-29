import { apiEndpoints, apiRequest, apiRequestRaw, fetchEntity } from '../apiclient';
import { ProjectDto, ProjectHelperTypeDto, ProjectUserDto } from '../types/generated';
import { HELPER_TYPE_ID_VARIABLE, ID_VARIABLE, PROJECT_ID_VARIABLE, USER_ID_VARIABLE, WEEKDAY_VARIABLE } from '../urlmappings';
import { convertToYYYYMMDD } from '../util';


function parseProjectDates(project: ProjectDto): ProjectDto {
    return {
        ...project,
        startDate: new Date(project.startDate),
        endDate: new Date(project.endDate),
    };
}

function serializeProjectDates(project: ProjectDto): any {
    return {
        ...project,
        startDate: convertToYYYYMMDD(project.startDate),
        endDate: convertToYYYYMMDD(project.endDate),
    };
}

export function fetchProjects(accessToken: string): Promise<ProjectDto[]> {
    return apiRequest<ProjectDto[]>({
        apiEndpoint: apiEndpoints.project.getOwn,
        authToken: accessToken,
    }).then(projects => projects.map(parseProjectDates));
}

export function fetchProject(accessToken: string, projectId: number): Promise<ProjectDto> {
    return fetchEntity<ProjectDto>(apiEndpoints.project.getById, accessToken, projectId).then(parseProjectDates);
}

export function createProject(accessToken: string, project: ProjectDto): Promise<ProjectDto> {
    return apiRequest<ProjectDto>({
        apiEndpoint: apiEndpoints.project.create,
        authToken: accessToken,
        data: serializeProjectDates(project),
    }).then(parseProjectDates);
}

export function updateProject(accessToken: string, project: ProjectDto): Promise<ProjectDto> {
    return apiRequest<ProjectDto>({
        apiEndpoint: apiEndpoints.project.update,
        authToken: accessToken,
        data: serializeProjectDates(project),
        parameters: {
            [ID_VARIABLE]: project.id.toString(),
        },
    }).then(parseProjectDates);
}

export function deleteProject(accessToken: string, { id }: ProjectDto): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.project.delete,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: id.toString(),
        },
    }).then(result => undefined);
}

export function createProjectUser(accessToken: string, { projectId, userId }: ProjectUserDto): Promise<ProjectUserDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.addUser,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId.toString(),
            [USER_ID_VARIABLE]: userId.toString(),
        },
    });
}

export function deleteProjectUser(accessToken: string, { projectId, userId }: ProjectUserDto): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.project.deleteUser,
        authToken: accessToken,
        parameters: {
            [ID_VARIABLE]: projectId.toString(),
            [USER_ID_VARIABLE]: userId.toString(),
        },
    }).then(result => undefined);
}

export function fetchProjectHelperTypes(accessToken: string, projectId: number, helperTypeId?: number, weekday?: number): Promise<ProjectHelperTypeDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.getHelperTypes,
        authToken: accessToken,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectId.toString(),
        },
        queries: {
            [HELPER_TYPE_ID_VARIABLE]: helperTypeId?.toString(),
            [WEEKDAY_VARIABLE]: weekday?.toString(),
        }
    });
}

export function createProjectHelperType(accessToken: string, projectHelperType: ProjectHelperTypeDto): Promise<ProjectHelperTypeDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.addHelperType,
        authToken: accessToken,
        data: projectHelperType,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectHelperType.projectId.toString(),
        },
    });
}

export function updateProjectHelperType(accessToken: string, projectHelperType: ProjectHelperTypeDto): Promise<ProjectHelperTypeDto> {
    return apiRequest({
        apiEndpoint: apiEndpoints.project.updateHelperType,
        authToken: accessToken,
        data: projectHelperType,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectHelperType.projectId.toString(),
            [ID_VARIABLE]: projectHelperType.id.toString(),
        },
    });
}

export function deleteProjectHelperType(accessToken: string, { id, projectId }): Promise<void> {
    return apiRequestRaw({
        apiEndpoint: apiEndpoints.project.deleteHelperType,
        authToken: accessToken,
        parameters: {
            [PROJECT_ID_VARIABLE]: projectId,
            [ID_VARIABLE]: id,
        },
    }).then(result => undefined);
}