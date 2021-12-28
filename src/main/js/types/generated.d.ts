/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.32.889 on 2021-09-28 21:17:20.

export interface ExceptionDto {
    key: string;
    message: string;
    httpCode: number;
}

export interface HelperTypeDto extends Identifiable<number> {
    id: number;
    name: string;
}

export interface HelperTypeDtoBuilder {
}

export interface ItemDto extends Patchable, Identifiable<number> {
    id: number;
    slotId: number;
    identifier: string;
    hasBarcode: boolean;
    name: string;
    description: string;
    quantity: number;
    unit: string;
    outsideQualified: boolean;
    consumable: boolean;
    broken: boolean;
    technicalCrewId: number;
}

export interface ItemDtoBuilder {
}

export interface ItemHistoryDto extends Identifiable<number> {
    id: number;
    itemId: number;
    userId: number;
    timestamp: Date;
    type: HistoryType;
    data: string;
}

export interface ItemHistoryDtoBuilder {
}

export interface ItemImageDto extends Identifiable<number> {
    id: number;
    itemId: number;
    image: any;
    mediaType: string;
}

export interface ItemImageDtoBuilder {
}

export interface ItemItemDto extends Identifiable<number> {
    id: number;
    item1Id: number;
    item2Id: number;
}

export interface ItemItemDtoBuilder {
}

export interface ItemItemTagDto extends Identifiable<number> {
    id: number;
    itemId: number;
    itemTagId: number;
}

export interface ItemItemTagDtoBuilder {
}

export interface ItemNoteDto extends Identifiable<number> {
    id: number;
    itemId: number;
    userId: number;
    note: string;
    timestamp: Date;
}

export interface ItemNoteDtoBuilder {
}

export interface ItemTagDto extends Identifiable<number> {
    id: number;
    name: string;
}

export interface ItemTagDtoBuilder {
}

export interface JwtAuthenticationDto {
    accessToken: string;
    tokenType: string;
}

export interface LoginDto {
    email: string;
    password: string;
}

export interface NeedDto extends Identifiable<number> {
    id: number;
    projectHelperTypeId: number;
    date: Date;
    quantity: number;
}

export interface NeedDtoBuilder {
}

export interface NeedUserDto extends Identifiable<number> {
    id: number;
    needId: number;
    userId: number;
    state: NeedUserState;
}

export interface NeedUserDtoBuilder {
}

export interface PasswordChangeDto {
    userId: number;
    token: string;
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
}

export interface PasswordChangeDtoBuilder {
}

export interface PasswordResetDto {
    email: string;
}

export interface PasswordResetDtoBuilder {
}

export interface ProjectDto extends Identifiable<number> {
    id: number;
    name: string;
    startDate: Date;
    endDate: Date;
}

export interface ProjectDtoBuilder {
}

export interface ProjectHelperTypeDto extends Identifiable<number> {
    id: number;
    projectId: number;
    helperTypeId: number;
    weekday: number;
    startTime: string;
    endTime: string;
}

export interface ProjectHelperTypeDtoBuilder {
}

export interface ProjectUserDto extends Identifiable<number> {
    id: number;
    projectId: number;
    userId: number;
}

export interface ProjectUserDtoBuilder {
}

export interface RentalDto extends Identifiable<number> {
    id: number;
    itemId: number;
    userId: number;
    storeKeeperId: number;
    start: Date;
    end: Date;
}

export interface RentalDtoBuilder {
}

export interface RoleDto {
    role: string;
}

export interface SlotDto extends Identifiable<number> {
    id: number;
    storeId: number;
    name: string;
    description: string;
    outside: boolean;
}

export interface SlotDtoBuilder {
}

export interface StoreDto extends Identifiable<number> {
    id: number;
    type: StoreType;
    name: string;
    address: string;
}

export interface StoreDtoBuilder {
}

export interface StoreProjectDto extends Identifiable<number> {
    id: number;
    storeId: number;
    projectId: number;
    start: Date;
    end: Date;
}

export interface StoreProjectDtoBuilder {
}

export interface TechnicalCrewDto extends Identifiable<number> {
    id: number;
    name: string;
}

export interface TechnicalCrewDtoBuilder {
}

export interface UserDto extends Identifiable<number> {
    id: number;
    firstName: string;
    lastName: string;
    gender: string;
    email: string;
    telephoneNumber: string;
    mobileNumber: string;
    businessNumber: string;
    profession: string;
    skills: string;
}

export interface UserDtoBuilder {
}

export interface UserRoleDto extends Identifiable<number> {
    id: number;
    userId: number;
    role: string;
}

export interface AssembledHelperTypeDto {
    id: number;
    name: string;
    shifts: AssembledProjectHelperTypeDto[];
}

export interface AssembledHelperTypeDtoBuilder {
}

export interface AssembledHelperTypeWrapperDto {
    helperTypes: AssembledHelperTypeDto[];
}

export interface AssembledHelperTypeWrapperDtoBuilder {
}

export interface AssembledNeedDto {
    id: number;
    projectHelperTypeId: number;
    date: Date;
    quantity: number;
    state: NeedUserState;
    users: AssembledNeedUserDto[];
}

export interface AssembledNeedDtoBuilder {
}

export interface AssembledNeedUserDto {
    id: number;
    needId: number;
    userId: number;
    state: NeedUserState;
    user: UserDto;
}

export interface AssembledProjectHelperTypeDto {
    id: number;
    projectId: number;
    helperTypeId: number;
    weekday: number;
    startTime: string;
    endTime: string;
    need: AssembledNeedDto;
}

export interface AssembledProjectHelperTypeDtoBuilder {
}

export interface HelperType extends Identifiable<number> {
    id: number;
    name: string;
}

export interface HelperTypeBuilder {
}

export interface Item extends Identifiable<number> {
    id: number;
    slot: Slot;
    identifier: string;
    hasBarcode: boolean;
    name: string;
    description: string;
    quantity: number;
    unit: string;
    outsideQualified: boolean;
    consumable: boolean;
    broken: boolean;
    technicalCrew: TechnicalCrew;
    itemNotes: ItemNote[];
    history: ItemHistory[];
}

export interface ItemBuilder {
}

export interface ItemHistory extends Identifiable<number> {
    id: number;
    item: Item;
    user: User;
    timestamp: Date;
    type: HistoryType;
    data: string;
}

export interface ItemHistoryBuilder {
}

export interface ItemImage extends Identifiable<number> {
    id: number;
    item: Item;
    image: any;
    mediaType: string;
}

export interface ItemImageBuilder {
}

export interface ItemItem extends Identifiable<number> {
    id: number;
    item1: Item;
    item2: Item;
}

export interface ItemItemBuilder {
}

export interface ItemItemTag extends Identifiable<number> {
    id: number;
    item: Item;
    itemTag: ItemTag;
}

export interface ItemItemTagBuilder {
}

export interface ItemNote extends Comparable<ItemNote>, Identifiable<number> {
    id: number;
    item: Item;
    user: User;
    note: string;
    timestamp: Date;
}

export interface ItemNoteBuilder {
}

export interface ItemTag extends Identifiable<number> {
    id: number;
    name: string;
}

export interface ItemTagBuilder {
}

export interface Need extends Identifiable<number> {
    id: number;
    date: Date;
    quantity: number;
    projectHelperType: ProjectHelperType;
}

export interface NeedBuilder {
}

export interface NeedUser extends Identifiable<number> {
    id: number;
    need: Need;
    user: User;
    state: NeedUserState;
}

export interface NeedUserBuilder {
}

export interface PasswordChangeToken extends Identifiable<number> {
    id: number;
    user: User;
    token: string;
    updated: Date;
}

export interface PasswordChangeTokenBuilder {
}

export interface Project extends Identifiable<number> {
    id: number;
    name: string;
    startDate: Date;
    endDate: Date;
}

export interface ProjectBuilder {
}

export interface ProjectHelperType extends Identifiable<number> {
    id: number;
    project: Project;
    helperType: HelperType;
    weekday: number;
    startTime: Date;
    endTime: Date;
}

export interface ProjectHelperTypeBuilder {
}

export interface ProjectUser extends Identifiable<number> {
    id: number;
    project: Project;
    user: User;
}

export interface ProjectUserBuilder {
}

export interface Rental extends Identifiable<number> {
    id: number;
    item: Item;
    user: User;
    storeKeeper: User;
    start: Date;
    end: Date;
}

export interface RentalBuilder {
}

export interface Slot extends Identifiable<number> {
    id: number;
    store: Store;
    name: string;
    description: string;
    outside: boolean;
    items: Item[];
}

export interface SlotBuilder {
}

export interface Store extends Identifiable<number> {
    id: number;
    type: StoreType;
    name: string;
    address: string;
    storeProjects: StoreProject[];
    slots: Slot[];
}

export interface StoreBuilder {
}

export interface StoreProject extends Identifiable<number> {
    id: number;
    store: Store;
    project: Project;
    start: Date;
    end: Date;
}

export interface StoreProjectBuilder {
}

export interface TechnicalCrew extends Identifiable<number> {
    id: number;
    name: string;
}

export interface TechnicalCrewBuilder {
}

export interface User extends UserDetails, Identifiable<number> {
    id: number;
    firstName: string;
    lastName: string;
    gender: Gender;
    passwordHash: string;
    email: string;
    telephoneNumber: string;
    mobileNumber: string;
    businessNumber: string;
    profession: string;
    skills: string;
    passwordChangeToken: PasswordChangeToken;
    roles: UserRole[];
}

export interface UserBuilder {
}

export interface UserRole extends GrantedAuthority, Identifiable<number> {
    id: number;
    user: User;
    role: string;
    roleWithRights: GrantedAuthority[];
}

export interface RoleRightManager {
}

export interface Patchable {
}

export interface GrantedAuthority extends Serializable {
    authority: string;
}

export interface UserDetails extends Serializable {
    authorities: GrantedAuthority[];
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    credentialsNonExpired: boolean;
    enabled: boolean;
    password: string;
    username: string;
}

export interface Identifiable<I> {
    id: I;
}

export interface Comparable<T> {
}

export interface Serializable {
}

export type HistoryType = "CREATED" | "UPDATED" | "MOVED" | "BROKEN" | "FIXED" | "QUANTITY_CHANGED";

export type NeedUserState = "NONE" | "APPLIED" | "APPROVED" | "REJECTED";

export type StoreType = "MAIN" | "STANDARD" | "MOBILE";

export type Gender = "MALE" | "FEMALE";
