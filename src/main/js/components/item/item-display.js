import { Button, CircularProgress, Divider, IconButton, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography, Link as A } from '@material-ui/core';
import Select, { components } from 'react-select';
import Chip from '@material-ui/core/Chip';
import { withStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';
import CheckIcon from '@material-ui/icons/Check';
import CloseIcon from '@material-ui/icons/Close';
import React from 'react';
import { createItemNote } from '../../actions/item';
import { ItemsContext } from '../../providers/items-provider';
import { SessionContext } from '../../providers/session-provider';
import { convertToDDMMYYYY, convertToDDMMYYYY_HHMM } from '../../util';
import SimpleDialog from '../simple-dialog';
import WithPermission from '../with-permission';
import ItemTagsComponent from './item-tags';
import { fullPathOfItemData } from '../../paths';
import { Link } from 'react-router-dom';
import { RIGHT_ITEMS_NOTES_DELETE, RIGHT_ITEMS_NOTES_DELETE_FOREIGN, RIGHT_ITEMS_NOTES_POST, RIGHT_ITEMS_PATCH_QUANTITY } from '../../permissions';


const styles = theme => ({
    bold: {
        fontWeight: '500',
    },
    title: {
        fontSize: '30px',
        marginBottom: '10px',
        textAlign: 'center',
    },
    image: {
        float: 'right',
        maxWidth: '100%',
        display: 'inline-block',
        verticalAlign: 'top',
    },
    longText: {
        maxWidth: '450px',
        textAlign: 'justify',
    },
    container: {
        display: 'inline-block',
        verticalAlign: 'top',
        marginRight: theme.spacing.unit * 3,
        marginBottom: theme.spacing.unit * 3,
    },
    fullSizeContainer: {
        marginRight: theme.spacing.unit * 3,
        marginBottom: theme.spacing.unit * 3,
    },
    chip: {
        marginRight: theme.spacing.unit,
        marginTop: theme.spacing.unit,
    },
    divider: {
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    noteHeader: {
        fontWeight: '500',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    editQuantity: {
        width: '115px',
    },
    editQuantityContainer: {
        display: 'flex',
        alignItems: 'center',
    },
    link: {
        color: theme.palette.primary.main,
        textDecoration: 'none',
        cursor: 'pointer',
    },
});

@withStyles(styles)
class StatefulItemDisplayComponent extends React.Component {

    constructor(props) {
        super(props);
        const { currentUser } = props.sessionState;
        this.state = {
        };
    }

    getFirstAndLastName({ userId, user }) {
        if (!userId) {
            return '-';
        }
        if (!user) {
            return (<CircularProgress size={12} />);
        }
        return user.firstName + ' ' + user.lastName;
    }

    showNoteDeleteButton({ userId }) {
        const { sessionState } = this.props;
        return sessionState.hasPermission(RIGHT_ITEMS_NOTES_DELETE)
            && (userId === sessionState.currentUser.id || sessionState.hasPermission(RIGHT_ITEMS_NOTES_DELETE_FOREIGN));
    }

    render() {
        const { classes, itemsState } = this.props;
        const item = itemsState.getSelectedItem();

        return (
            <div>
                <div className={classes.title}>{item.name}</div>
                {item.imageUrl && <img className={classes.image} src={item.imageUrl} />}
                <div className={classes.container}>
                    <Typography variant="h6">Daten</Typography>
                    <div className={classes.container}>
                        <div className={classes.bold}>
                            Eindeutiger Bezeichner{item.hasBarcode ? ' (Barcode)' : ''}
                        </div>
                        {item.identifier}<br />
                        <br />
                        <div className={classes.bold}>
                            Lagerplatz
                        </div>
                        {item.storeName}: {item.slotName}
                        <br />
                        <br />
                        <div className={classes.bold}>
                            Menge
                        </div>
                        <div className={classes.editQuantityContainer}>
                            {itemsState.modifiedQuantity ? (<>
                                <TextField
                                    className={classes.editQuantity}
                                    value={itemsState.modifiedQuantity}
                                    onChange={event => itemsState.changeModifiedQuantity(event.target.value)}
                                    margin="dense"
                                    variant="outlined"
                                    type="number"
                                    inputProps={{ min: '1' }}
                                />
                                <IconButton
                                    disabled={itemsState.actionsDisabled}
                                    onClick={event => itemsState.saveQuantity()}
                                >
                                    <CheckIcon />
                                </IconButton>
                                <IconButton
                                    disabled={itemsState.actionsDisabled}
                                    onClick={event => itemsState.resetQuantity()}
                                >
                                    <CloseIcon />
                                </IconButton>
                            </>) : (<>
                                {item.quantity} {item.unit}
                                <WithPermission permission={RIGHT_ITEMS_PATCH_QUANTITY}>
                                    <IconButton
                                        disabled={itemsState.actionsDisabled}
                                        onClick={event => itemsState.editQuantity()}
                                    >
                                        <EditIcon />
                                    </IconButton>
                                </WithPermission>
                            </>)}
                        </div>
                        <div className={classes.bold}>
                            Gewerk
                        </div>
                        {item.technicalCrewName}<br />
                        <br />
                    </div>
                    <div className={classes.container}>
                        <div className={classes.bold}>
                            Verbrauchsgegenstand
                        </div>
                        {item.consumable ? 'Ja' : 'Nein'}<br />
                        <br />
                        <div className={classes.bold}>
                            Wetterbeständig
                        </div>
                        {item.outsideQualified ? 'Ja' : 'Nein'}<br />
                        <br />
                        <div className={classes.bold}>
                            Beschreibung
                        </div>
                        <div className={classes.longText}>
                            {item.description}
                        </div>
                        <br />
                        <div className={classes.bold}>
                            Status
                        </div>
                        {item.state}
                    </div>
                </div>
                <div className={classes.container}>
                    <Typography variant="h6">Notizen</Typography>
                    {item.notes ? item.notes.map(note => (
                        <div key={note.id}>
                            <div className={classes.noteHeader}>
                                <div>
                                    <div className={classes.bold}>
                                        {this.getFirstAndLastName(note)}
                                    </div>
                                    <Typography variant="caption">{convertToDDMMYYYY_HHMM(note.timestamp)}</Typography>
                                </div>
                                {this.showNoteDeleteButton(note) &&
                                    <IconButton
                                        onClick={() => itemsState.deleteNote(note)}
                                        disabled={itemsState.actionsDisabled}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                }
                            </div>
                            <div className={classes.longText}>
                                {note.note}
                            </div>
                            <Divider className={classes.divider} />
                        </div>
                    )) : (<CircularProgress />)}
                    <WithPermission permission={RIGHT_ITEMS_NOTES_POST}>
                        <SimpleDialog
                            title={'Notiz hinzufügen'}
                            content={(<TextField
                                variant="outlined"
                                multiline
                                value={itemsState.note}
                                onChange={event => itemsState.changeNote(event.target.value)}
                            />)}
                            cancelText="Abbrechen"
                            okText={'Speichern'}
                            onOK={() => itemsState.saveNote()}
                        >
                            <Button variant="contained">
                                Notiz hinzufügen
                            </Button>
                        </SimpleDialog>
                    </WithPermission>
                </div>
                <br />
                <div className={classes.fullSizeContainer}>
                    <Typography variant="h6">Schlagwörter</Typography>
                    <ItemTagsComponent />
                </div>
                <br />
                <div className={classes.container}>
                    <Typography variant="h6">Zugehörige Artikel</Typography>
                    {item.items ? item.items.map(relatedItem => (
                        <div key={relatedItem.id}>
                            <Link className={classes.link} to={fullPathOfItemData(relatedItem.id)}>{relatedItem.name} ({relatedItem.identifier})</Link>
                        </div>
                    )) : (<CircularProgress />)}
                </div>
            </div>
        );
    }
}


const ItemDisplayComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <ItemsContext.Consumer>
                    {itemsState => (
                        <StatefulItemDisplayComponent {...props} sessionState={sessionState} itemsState={itemsState} />
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemDisplayComponent;