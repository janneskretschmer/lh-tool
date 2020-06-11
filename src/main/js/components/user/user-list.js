import React from 'react';
import { UsersContext } from '../../providers/users-provider';
import { requiresLogin, getRoleName } from '../../util';
import { CircularProgress, Button, TextField, IconButton, InputAdornment, withStyles, FormControl, InputLabel, Select, MenuItem } from '@material-ui/core';
import { Redirect } from 'react-router';
import { fullPathOfUserSettings } from '../../paths';
import PagedTable from '../table';
import { withSnackbar } from 'notistack';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import SearchIcon from '@material-ui/icons/Search';
import WithPermission from '../with-permission';

const styles = theme => ({
    button: {
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    select: {
        minWidth: '100px',
        margin: theme.spacing.unit,
    }
});

@withStyles(styles)
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
        const { expandFilters } = this.state;
        const users = usersState.users && Array.from(usersState.users.values());
        return users ? (<>
            <PagedTable
                title="Benutzer"
                SelectionHeader={props => (
                    <>
                        <WithPermission permission="ROLE_RIGHT_USERS_DELETE">
                            <Button variant="outlined" onClick={() => this.bulkDelete(props.selected).finally(() => props.resetSelection())}>
                                Löschen
                            </Button>
                        </WithPermission>
                    </>
                )}
                filter={(<>
                    <TextField
                        id="free-search"
                        value={usersState.filterFreeText}
                        onChange={event => usersState.changeFreeTextFilter(event.target.value)}
                        variant="outlined"
                        label="Freitextsuche"
                        margin="dense"
                    />
                    <IconButton className={classes.button} onClick={() => this.toggleExpandFilters()}>
                        {expandFilters ? (<ExpandLessIcon />) : (<ExpandMoreIcon />)}
                    </IconButton>
                    <IconButton className={classes.button} onClick={() => this.filter()}>
                        <SearchIcon />
                    </IconButton>
                    {(expandFilters || usersState.filterProjectId || usersState.filterRole) && (<>
                        <br />
                        <FormControl className={classes.select}>
                            <InputLabel htmlFor="project">Projekt</InputLabel>
                            <Select
                                value={usersState.filterProjectId}
                                onChange={event => usersState.changeProjectIdFilter(event.target.value)}
                                inputProps={{
                                    name: 'project',
                                    id: 'project',
                                }}
                            >
                                <MenuItem value=""></MenuItem>
                                {usersState.projects.map(project => (
                                    <MenuItem key={project.id} value={project.id}>{project.name}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <FormControl className={classes.select}>
                            <InputLabel htmlFor="role">Rolle</InputLabel>
                            <Select
                                value={usersState.filterRole}
                                onChange={event => usersState.changeRoleFilter(event.target.value)}
                                inputProps={{
                                    name: 'role',
                                    id: 'role',
                                }}
                            >
                                <MenuItem value=""></MenuItem>
                                {usersState.roles.map((role) => (
                                    <MenuItem key={role.role} value={role.role}>{getRoleName(role.role)}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </>)}
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
                showAddButton={true} />
        </>) : (<CircularProgress />)
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