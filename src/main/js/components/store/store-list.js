import { Button, CircularProgress } from '@mui/material';
import React from 'react';
import { fullPathOfStoreSettings } from '../../paths';
import { RIGHT_STORES_DELETE } from '../../permissions';
import { SessionContext } from '../../providers/session-provider';
import { StoresContext } from '../../providers/store-provider';
import SimpleDialog from '../simple-dialog';
import PagedTable from '../table';
import WithPermission from '../with-permission';

class StatefulStoreListComponent extends React.Component {

    componentDidMount() {
        this.props.storesState.loadStores();
    }

    render() {
        const { storesState } = this.props;
        const stores = storesState.getAssembledStoreList();
        return (
            <PagedTable
                title="Lager"
                SelectionHeader={props => (
                    <WithPermission permission={RIGHT_STORES_DELETE}>
                        {storesState.actionInProgress ? (<CircularProgress />) : (<>
                            <SimpleDialog
                                title="Löschen bestätigen"
                                okText="Ja"
                                cancelText="Nein"
                                text={`Sollen die ${props.selected.length} ausgewählten Lager wirklich gelöscht werden?`}
                                onOK={() => { storesState.bulkDeleteStores(props.selected); props.resetSelection(); }}
                            >
                                <Button
                                    variant="outlined"
                                    onClick={() => { }}
                                    disabled={storesState.actionInProgress}
                                >
                                    Löschen
                                </Button>
                            </SimpleDialog>
                        </>)}
                    </WithPermission>
                )}
                headers={[
                    {
                        key: 'name',
                        name: 'Name',
                    },
                    {
                        key: 'typeName',
                        name: 'Typ',
                    },
                    {
                        key: 'address',
                        name: 'Adresse',
                    }
                ]}
                rows={stores}
                redirect={fullPathOfStoreSettings}
                showAddButton={!storesState.actionInProgress}
            />
        );
    }
}


const StoreListComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <StoresContext.Consumer>
                    {storesState => (<StatefulStoreListComponent {...props} sessionState={sessionState} storesState={storesState} />)}
                </StoresContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default StoreListComponent;