import { CircularProgress } from '@mui/material';
import Button from '@mui/material/Button';
import React from 'react';
import { fullPathOfItemData, fullPathOfItems } from '../../paths';
import { RIGHT_ITEMS_PATCH_BROKEN, RIGHT_ITEMS_PATCH_SLOT, RIGHT_ITEMS_POST, RIGHT_ITEMS_PUT } from '../../permissions';
import { ItemsContext } from '../../providers/items-provider';
import { PageContext } from '../../providers/page-provider';
import { SessionContext } from '../../providers/session-provider';
import { generateUniqueId, getItemBarcodeString } from '../../util';
import SimpleDialog from '../simple-dialog';
import BarcodeGenerator from '../util/barcode-generator';
import LenientRedirect from '../util/lenient-redirect';
import WithPermission from '../with-permission';
import ItemDisplayComponent from './item-display';
import ItemEditComponent from './item-edit';
import ItemIdentifierEditComponent from './item-identifier';
import ItemSlotEditComponent from './item-slot';


class StatefulItemDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            redirectUrl: null,
            currentId: null,
        };
    }

    componentDidMount() {

    }

    componentDidUpdate() {
        const id = this.props.match.params.id;
        if (id !== this.state.currentId) {
            this.setState({ currentId: id }, () => this.props.itemsState.selectItem(id));
        }

        if (this.props.itemsState.selectedItem && this.props.pageState.currentItemName !== this.props.itemsState.selectedItem.name) {
            this.props.pageState.setCurrentItemName(this.props.itemsState.selectedItem);
        }
    }

    save() {
        this.props.itemsState.saveSelectedItem().then(item => {
            if (item && item.id) {
                this.setState({ redirectUrl: fullPathOfItemData(item.id) });
            }
        });
    }

    delete() {
        this.props.itemsState.deleteSelectedItem(
            () => this.setState({ redirectUrl: fullPathOfItems() })
        );
    }

    render() {
        const { itemsState } = this.props;
        const item = itemsState.getSelectedItem();

        if (this.state.redirectUrl) {
            return (<LenientRedirect to={this.state.redirectUrl} onSamePage={() => this.setState({ redirectUrl: null })} />);
        }

        if (!item) {
            return (<CircularProgress />);
        }
        const disabled = itemsState.actionsDisabled || !itemsState.isItemValid();
        return (
            <>
                {itemsState.edit ? (
                    <>
                        <ItemEditComponent item={item}></ItemEditComponent>
                        <Button variant="contained" disabled={disabled} sx={{
                            mt: 1,
                            mr: 1
                        }} onClick={() => this.save()}>
                            Speichern
                        </Button>
                        <Button variant="outlined" disabled={itemsState.actionsDisabled} sx={{
                            mt: 1,
                            mr: 1
                        }} onClick={() => itemsState.resetSelectedItem()}>
                            Abbrechen
                        </Button>
                    </>
                ) : (
                    <>
                        <ItemDisplayComponent item={item}></ItemDisplayComponent>
                        <WithPermission permission={RIGHT_ITEMS_PUT}>
                            <Button
                                variant="contained"
                                sx={{
                                    mt: 1,
                                    mr: 1
                                }}
                                onClick={() => itemsState.changeEdit(true)}
                                disabled={itemsState.actionsDisabled}
                            >
                                Bearbeiten
                            </Button>
                        </WithPermission>
                        <WithPermission permission={RIGHT_ITEMS_PATCH_SLOT}>
                            <SimpleDialog
                                title="Neuer Lagerplatz"
                                content={<ItemSlotEditComponent />}
                                onOK={() => itemsState.saveSlot()}
                                okText="Speichern"
                                cancelText="Abbrechen"
                            >
                                <Button
                                    variant="contained"
                                    sx={{
                                        mt: 1,
                                        mr: 1
                                    }}
                                    disabled={itemsState.actionsDisabled}
                                >
                                    Verschieben
                                </Button>
                            </SimpleDialog>
                        </WithPermission>
                        <WithPermission permission={RIGHT_ITEMS_POST}>
                            <SimpleDialog
                                title={item.name + ' kopieren'}
                                content={<ItemIdentifierEditComponent />}
                                onOK={() => itemsState.copySelectedItem()}
                                onOpen={() => itemsState.changeCopyIdentifier(generateUniqueId())}
                                okText="Kopieren"
                                cancelText="Abbrechen"
                            >
                                <Button
                                    variant="contained"
                                    sx={{
                                        mt: 1,
                                        mr: 1
                                    }}
                                    disabled={itemsState.actionsDisabled}
                                >
                                    Kopieren
                                </Button>
                            </SimpleDialog>
                        </WithPermission>
                        <WithPermission permission={RIGHT_ITEMS_PATCH_BROKEN}>
                            <Button
                                variant="contained"
                                sx={{
                                    mt: 1,
                                    mr: 1
                                }}
                                onClick={() => itemsState.saveBrokenState(!item.broken)}
                                disabled={itemsState.actionsDisabled}
                            >
                                {item.broken ? 'Repariert' : 'Defekt'}
                            </Button>
                        </WithPermission>
                        <Button
                            variant="contained"
                            sx={{
                                mt: 1,
                                mr: 1
                            }}
                            onClick={() => alert('TODO: implement "Ausleihen"')}
                            disabled={itemsState.actionsDisabled || item.broken}
                        >
                            Ausleihen
                        </Button>
                        <BarcodeGenerator
                            content={getItemBarcodeString(item.identifier)}
                        >
                            <Button
                                variant="contained"
                                sx={{
                                    mt: 1,
                                    mr: 1
                                }}
                                disabled={itemsState.actionsDisabled}>
                                Barcode generieren
                            </Button>
                        </BarcodeGenerator>
                        <SimpleDialog
                            title="Löschen bestätigen"
                            okText="Ja"
                            cancelText="Nein"
                            text={`Sollen der Artikel ${item.name} wirklich gelöscht werden?`}
                            onOK={() => this.delete()}
                        >
                            <Button
                                variant="outlined"
                                sx={{
                                    mt: 1,
                                    mr: 1
                                }}
                                disabled={itemsState.actionsDisabled}
                            >
                                Löschen
                            </Button>
                        </SimpleDialog>
                    </>
                )
                }
            </>
        );
    }
}

const ItemDetailComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <ItemsContext.Consumer>
                    {itemsState => (
                        <PageContext.Consumer>
                            {pageState => (
                                <StatefulItemDetailComponent {...props} sessionState={sessionState} itemsState={itemsState} pageState={pageState} />
                            )}
                        </PageContext.Consumer>
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemDetailComponent;