import { CircularProgress } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import { withStyles } from '@material-ui/core/styles';
import React from 'react';
import { createOrUpdateItem, fetchItem } from '../../actions/item';
import { SessionContext } from '../../providers/session-provider';
import { withContext, generateUniqueId } from '../../util';
import ItemDisplayComponent from './item-display';
import ItemEditComponent from './item-edit';
import ItemSlotEditComponent from './item-slot';
import ItemIdentifierEditComponent from './item-identifier';
import ItemsProvider, { ItemsContext } from '../../providers/items-provider';
import WithPermission from '../with-permission';
import SimpleDialog from '../simple-dialog';

const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },
    button: {
        marginRight: theme.spacing.unit,
        marginTop: theme.spacing.unit,
    },

});

@withStyles(styles)
class StatefulItemDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentDidMount() {
        const id = this.props.match.params.id
        this.props.itemsState.selectItem(id);
    }

    render() {
        const { classes, itemsState } = this.props;
        const { savingIfBroken } = this.state;
        const item = itemsState.getSelectedItem();
        if (!item) {
            return (<CircularProgress />);
        }
        const disabled = itemsState.actionsDisabled || !itemsState.isItemValid();
        return (
            <>
                {itemsState.edit ? (
                    <>
                        <ItemEditComponent item={item}></ItemEditComponent>
                        <Button variant="contained" disabled={disabled} className={classes.button} onClick={() => itemsState.saveSelectedItem()}>
                            Speichern
                        </Button>
                        <Button variant="outlined" disabled={itemsState.actionsDisabled} className={classes.button} onClick={() => itemsState.resetSelectedItem()}>
                            Abbrechen
                        </Button>
                    </>
                ) : (
                        <>
                            <ItemDisplayComponent item={item}></ItemDisplayComponent>
                            <WithPermission permission="ROLE_RIGHT_ITEMS_PUT">
                                <Button
                                    variant="contained"
                                    className={classes.button}
                                    onClick={() => itemsState.changeEdit(true)}
                                    disabled={itemsState.actionsDisabled}
                                >
                                    Bearbeiten
                            </Button>
                            </WithPermission>
                            <WithPermission permission="ROLE_RIGHT_ITEMS_PATCH_SLOT">
                                <SimpleDialog
                                    title="Neuer Lagerplatz"
                                    content={<ItemSlotEditComponent />}
                                    onOK={() => itemsState.saveSlot()}
                                    okText="Speichern"
                                    cancelText="Abbrechen"
                                >
                                    <Button
                                        variant="contained"
                                        className={classes.button}
                                        disabled={itemsState.actionsDisabled}
                                    >
                                        Verschieben
                                    </Button>
                                </SimpleDialog>
                            </WithPermission>
                            <WithPermission permission="ROLE_RIGHT_ITEMS_POST">
                                <SimpleDialog
                                    title={item.name + ' kopieren'}
                                    content={<ItemIdentifierEditComponent />}
                                    onOK={() => itemsState.saveSlot()}
                                    onOpen={() => itemsState.changeCopyIdentifier(generateUniqueId())}
                                    okText="Kopieren"
                                    cancelText="Abbrechen"
                                >
                                    <Button
                                        variant="contained"
                                        className={classes.button}
                                        disabled={itemsState.actionsDisabled}
                                    >
                                        Kopieren
                                </Button>
                                </SimpleDialog>
                            </WithPermission>
                            <WithPermission permission="ROLE_RIGHT_ITEMS_PATCH_BROKEN">
                                <Button
                                    variant="contained"
                                    className={classes.button}
                                    onClick={() => itemsState.saveBrokenState(!item.broken)}
                                    disabled={itemsState.actionsDisabled}
                                >
                                    {item.broken ? 'Repariert' : 'Defekt'}
                                </Button>
                            </WithPermission>
                            <Button
                                variant="contained"
                                className={classes.button}
                                onClick={() => alert('TODO: implement "Ausleihen"')}
                                disabled={itemsState.actionsDisabled}
                            >
                                Ausleihen
                            </Button>
                            <Button
                                variant="outlined"
                                className={classes.button}
                                onClick={() => alert('TODO: implement "Zerstörung"')}
                                disabled={itemsState.actionsDisabled}
                            >
                                Löschen
                            </Button>
                        </>
                    )}
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
                        <StatefulItemDetailComponent {...props} sessionState={sessionState} itemsState={itemsState} />
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemDetailComponent;