import React from 'react';
import { UsersContext } from '../../providers/users-provider';
import { requiresLogin } from '../../util';
import { CircularProgress, Button } from '@material-ui/core';
import { Redirect } from 'react-router';
import { fullPathOfUserSettings } from '../../paths';
import PagedTable from '../table';
import { withSnackbar } from 'notistack';

@withSnackbar
export class StatefulUserListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            redirectToUser: null,
        };
    }

    componentDidMount() {
        this.props.usersState.loadEditableUsers().then(users => {
            if (users.length === 1) {
                this.setState({
                    redirectToUser: users[0].id,
                });
            }
        });
    }

    bulkDelete(userIds) {
        return this.props.usersState.bulkDeleteUsers(userIds).then(
            () => this.props.enqueueSnackbar(userIds.length + ' Benutzer erfolgreich gelöscht', { variant: 'success', })
        ).catch(
            () => this.props.enqueueSnackbar('Löschen fehlgeschlagen', { variant: 'error', })
        );
    }

    render() {
        const { usersState } = this.props;
        const { redirectToUser } = this.state;
        if (redirectToUser) {
            return (<Redirect to={fullPathOfUserSettings(redirectToUser)} />);
        }
        const users = usersState.users && Array.from(usersState.users.values());
        return users ? (<>
            <PagedTable
                title="Benutzer"
                SelectionHeader={props => (
                    <>
                        <Button variant="outlined" onClick={() => this.bulkDelete(props.selected).finally(() => props.resetSelection())}>
                            Löschen
                        </Button>
                    </>
                )}
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
                redirect={fullPathOfUserSettings} />
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