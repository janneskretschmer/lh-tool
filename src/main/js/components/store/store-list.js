import { withStyles } from '@material-ui/core/styles';
import React from 'react';
import { fullPathOfStoreSettings } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { StoresContext } from '../../providers/store-provider';
import { withContext } from '../../util';
import PagedTable from '../table';
import { Button, CircularProgress } from '@material-ui/core';
import WithPermission from '../with-permission';
import SimpleDialog from '../simple-dialog';
import { RIGHT_STORES_DELETE } from '../../permissions';

const styles = theme => ({
    noDecoration: {
        textDecoration: 'none',
    },
});

@withStyles(styles)
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