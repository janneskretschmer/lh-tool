import { withStyles } from '@material-ui/core/styles';
import React from 'react';
import { fullPathOfStoreSettings } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { StoresContext } from '../../providers/store-provider';
import { withContext } from '../../util';
import PagedTable from '../table';
import { Button } from '@material-ui/core';
import WithPermission from '../with-permission';
import SimpleDialog from '../simple-dialog';

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
                    <WithPermission permission="ROLE_RIGHT_STORES_DELETE">
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
                                disabled={storesState.actionsDisabled}
                            >
                                Löschen
                                </Button>
                        </SimpleDialog>
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
                showAddButton={true}
            />
        );
    }

    // constructor(props) {
    //     super(props);
    //     this.state = {
    //         stores: null
    //     };
    // }

    // redirect(id) {
    //     this.setState({ redirect: id });
    // }

    // componentDidMount() {
    //     fetchOwnStores(this.props.sessionState.accessToken).then(stores => this.setState({
    //         stores
    //     }));
    // }

    // render() {
    //     const { stores } = this.state;
    //     const { classes } = this.props;
    //     return (
    //         <SessionContext.Consumer>
    //             {sessionState => (
    //                 <>
    //                     <List>
    //                         {stores ? stores.map(store => (
    //                             <div key={store.id}>
    //                                 <Link to={fullPathOfStore(store.id)} className={classes.noDecoration} key={store.id}>
    //                                     <ListItem button>
    //                                         <ListItemIcon>
    //                                             {store.type === 'MOBIL' ? (<LocalShippingIcon />) : (<HomeIcon />)}
    //                                         </ListItemIcon>
    //                                         <ListItemText primary={store.name} secondary={store.address} />
    //                                     </ListItem>
    //                                 </Link>
    //                                 <Divider />
    //                             </div>
    //                         )) : (<CircularProgress />)}
    //                     </List>
    //                     <Link to={fullPathOfStore('new')} className={classes.noDecoration}>
    //                         <Button variant="contained" className={classes.new}>
    //                             Hinzufügen
    //                         </Button>
    //                     </Link>
    //                 </>
    //             )}
    //         </SessionContext.Consumer>
    //     );
    // }
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