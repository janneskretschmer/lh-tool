import { CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import React from 'react';
import { Redirect } from 'react-router-dom';
import { fullPathOfItem, fullPathOfItemData } from '../../paths';
import { RIGHT_ITEMS_PATCH_SLOT, RIGHT_ITEMS_POST } from '../../permissions';
import { ItemsContext } from '../../providers/items-provider';
import { SessionContext } from '../../providers/session-provider';
import SimpleDialog from '../simple-dialog';
import PagedTable from '../table';
import WithPermission from '../with-permission';
import ItemSlotEditComponent from './item-slot';


class StatefulItemListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            slotDialogOpen: false,
        };
    }

    componentDidMount() {
        this.loadItems();
    }

    loadItems() {
        this.props.itemsState.loadItems(this.props.keepSelectedItem);
    }

    handleToggleShowFilters = () => {
        this.setState({ showFilters: !this.state.showFilters });
    }

    handleChangeSearchTechnicalCrew = event => {
        this.setState({ searchTechnicalCrew: event.target.value });
    }

    handleChangeSearchTag = event => {
        this.setState({ searchTag: event.target.value });
    }

    handleChangeSearchStore = event => {
        this.setState({ searchStore: event.target.value });
    }

    handleChangeSearchSlot = event => {
        this.setState({ searchSlot: event.target.value });
    }

    toggleSlotDialog() {
        this.setState(prevState => ({ slotDialogOpen: !prevState.slotDialogOpen }));
    }

    render() {
        const { itemsState, sessionState, hideAdd, hideHeader, hiddenItemId, selected, onToggleSelect } = this.props;
        const { slotDialogOpen } = this.state;
        const items = itemsState.getAssembledItemList().filter(item => item.id !== hiddenItemId);
        const showAddButton = sessionState.hasPermission(RIGHT_ITEMS_POST);

        if (this.state.redirect) {
            return (<Redirect to={fullPathOfItem(this.state.redirect)} />);
        }

        if (!items) {
            return (<CircularProgress />);
        }

        const sxButton = { m: 1 };

        return <>
            <PagedTable
                selected={selected}
                onToggleSelect={onToggleSelect}
                SelectionHeader={!hideHeader && (props => (
                    <>
                        <WithPermission permission={RIGHT_ITEMS_PATCH_SLOT}>
                            <Button
                                variant="contained"
                                sx={sxButton}
                                disabled={itemsState.actionsDisabled}
                                onClick={() => this.toggleSlotDialog()}
                            >
                                Verschieben
                            </Button>
                            <Dialog
                                open={slotDialogOpen}
                                transitionDuration={0}
                                keepMounted
                                onClose={() => this.toggleSlotDialog()}
                                aria-labelledby="alert-dialog-slide-title"
                                aria-describedby="alert-dialog-slide-description"
                            >
                                <DialogTitle id="alert-dialog-slide-title">
                                    Neuer Lagerplatz
                                </DialogTitle>
                                <DialogContent>
                                    <ItemSlotEditComponent />
                                </DialogContent>
                                <DialogActions>
                                    <Button onClick={() => this.toggleSlotDialog()} color="secondary">
                                        Abbrechen
                                    </Button>
                                    <Button color="primary" onClick={() => {
                                        itemsState.bulkSaveSlot(props.selected, () => props.resetSelection());
                                        this.toggleSlotDialog();
                                    }}>
                                        Speichern
                                    </Button>
                                </DialogActions>
                            </Dialog>
                        </WithPermission>
                        <Button
                            variant="contained"
                            sx={sxButton}
                            onClick={() => itemsState.bulkUpdateBrokenState(props.selected, true, () => props.resetSelection())}
                            disabled={itemsState.actionsDisabled}
                        >
                            Defekt
                        </Button>
                        <Button
                            variant="contained"
                            sx={sxButton}
                            onClick={() => itemsState.bulkUpdateBrokenState(props.selected, false, () => props.resetSelection())}
                            disabled={itemsState.actionsDisabled}
                        >
                            Repariert
                        </Button>
                        <Button
                            disabled={itemsState.actionsDisabled}
                            variant="contained" sx={sxButton} onClick={() => alert('TODO: implement "Ausleihen"')}>
                            Ausleihen
                        </Button>
                        <Button
                            disabled={itemsState.actionsDisabled}
                            variant="contained" sx={sxButton} onClick={() => alert('TODO: implement "Zurückgeben"')}>
                            Zurückgeben
                        </Button>
                        <SimpleDialog
                            title="Löschen bestätigen"
                            okText="Ja"
                            cancelText="Nein"
                            text={`Sollen die ${props.selected.length} ausgewählten Artikel wirklich gelöscht werden?`}
                            onOK={() => { itemsState.bulkDeleteItems(props.selected); props.resetSelection(); }}
                        >
                            <Button
                                variant="outlined"
                                sx={sxButton}
                                onClick={() => { }}
                                disabled={itemsState.actionsDisabled}
                            >
                                Löschen
                            </Button>
                        </SimpleDialog>
                    </>
                ))}
                freeTextValue={itemsState.filterFreeText}
                onChangeFreeText={text => itemsState.changeFreeTextFilter(text)}
                onFilter={() => this.loadItems()}
                additionalFilters={(<>
                    TODO: Name, ID, Tag, Status (Verfügbar, Defekt, Ausgeliehen), Lager, Lagerplatz, Projekt, Menge (unter, gleich, über), Maße (unter, gleich, über), Gewerk, Verbrauchsgegenstand, Wetterbeständig
                </>)}
                headers={[
                    {
                        key: 'name',
                        name: 'Bezeichnung',
                    },
                    {
                        key: 'identifier',
                        name: 'ID',
                    },
                    {
                        key: 'state',
                        name: 'Status',
                    },
                    {
                        key: 'storeName',
                        name: 'Lager',
                        // hidden: !!storeId,
                        align: 'right',
                    },
                    {
                        key: 'slotName',
                        name: 'Platz',
                        // hidden: !!slotId,
                        align: 'right',
                    },
                    {
                        key: 'quantityWithUnit',
                        name: 'Menge',
                        align: 'right',
                    },
                    {
                        key: 'technicalCrewName',
                        name: 'Gewerk',
                        align: 'right',
                    },
                    // {
                    //     key: 'popularity',
                    //     name: 'Beliebtheit',
                    //     align: 'right',
                    // },

                ]}
                rows={items}
                redirect={!hideAdd && fullPathOfItemData}
                showAddButton={!hideAdd && showAddButton} />
        </>;
    }
}



const ItemListComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <ItemsContext.Consumer>
                    {itemsState => (
                        <StatefulItemListComponent {...props} sessionState={sessionState} itemsState={itemsState} />
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemListComponent;