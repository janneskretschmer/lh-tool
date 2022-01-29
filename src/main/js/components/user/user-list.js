import {
    Button, CircularProgress
} from '@mui/material';
import { withSnackbar } from 'notistack';
import React from 'react';
import { fullPathOfUserSettings } from '../../paths';
import { RIGHT_USERS_DELETE } from '../../permissions';
import { UsersContext } from '../../providers/users-provider';
import { getRoleName, requiresLogin } from '../../util';
import PagedTable from '../table';
import IdNameSelect from '../util/id-name-select';
import WithPermission from '../with-permission';

@withSnackbar
export class StatefulUserListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentDidMount() {
        this.props.usersState.loadEditableUsers();
    }

    bulkDelete(userIds) {
        return this.props.usersState.bulkDeleteUsers(userIds).then(
            () => this.props.enqueueSnackbar(userIds.length + ' Benutzer erfolgreich gelöscht', { variant: 'success', })
        ).catch(
            () => this.props.enqueueSnackbar('Löschen fehlgeschlagen', { variant: 'error', })
        );
    }

    toggleExpandFilters() {
        this.setState(prevState => ({
            expandFilters: !prevState.expandFilters,
        }));
        this.props.usersState.changeProjectIdFilter("");
        this.props.usersState.changeRoleFilter("");
    }

    filter() {
        this.props.usersState.loadEditableUsers();
    }

    render() {
        const { usersState, classes } = this.props;
        const users = usersState.users && Array.from(usersState.users.values());
        return users ? (<>
            <PagedTable
                title="Benutzer"
                SelectionHeader={props => (
                    <>
                        <WithPermission permission={RIGHT_USERS_DELETE}>
                            <Button variant="outlined" onClick={() => this.bulkDelete(props.selected).finally(() => props.resetSelection())}>
                                Löschen
                            </Button>
                        </WithPermission>
                    </>
                )}
                freeTextValue={usersState.filterFreeText}
                onChangeFreeText={text => usersState.changeFreeTextFilter(text)}
                onFilter={() => this.filter()}
                keepFiltersExpanded={usersState.filterProjectId || usersState.filterRole}
                additionalFilters={(<>
                    <IdNameSelect
                        sx={{ ml: 1, width: '111px' }}
                        label="Projekt"
                        value={usersState.filterProjectId}
                        onChange={value => usersState.changeProjectIdFilter(value)}
                        data={usersState.projects}
                        nullable
                    />
                    <IdNameSelect
                        sx={{ ml: 1, width: '111px' }}
                        label="Rolle"
                        value={usersState.filterRole}
                        onChange={value => usersState.changeRoleFilter(value)}
                        data={usersState.roles?.map(role => ({
                            id: role.role,
                            name: getRoleName(role.role),
                        }))}
                        nullable
                    />
                </>)}
                headers={[
                    {
                        key: 'firstName',
                        name: 'Vorname',
                    },
                    {
                        key: 'lastName',
                        name: 'Nachname',
                    },
                    {
                        key: 'email',
                        name: 'E-Mail',
                        semiImportant: true,
                    },
                    {
                        key: 'telephoneNumber',
                        name: 'Festnetz',
                        unimportant: true,
                    },
                    {
                        key: 'mobileNumber',
                        name: 'Mobil',
                        unimportant: true,
                    },
                    {
                        key: 'businessNumber',
                        name: 'Geschäftlich',
                        unimportant: true,
                    },
                ]}
                rows={users}
                redirect={fullPathOfUserSettings}
                showAddButton={usersState.isAllowedToCreate()} />
        </>) : (<CircularProgress />);
    }

}

const UserListComponent = props => (
    <>
        <UsersContext.Consumer>
            {usersState =>
                (<StatefulUserListComponent {...props} usersState={usersState} />)
            }
        </UsersContext.Consumer>
    </>
);
export default requiresLogin(UserListComponent);