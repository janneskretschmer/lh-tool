import { Button, CircularProgress, Divider, IconButton, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@material-ui/core';
import Select, { components } from 'react-select';
import Chip from '@material-ui/core/Chip';
import { withStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import React from 'react';
import { createItemNote } from '../../actions/item';
import { ItemsContext } from '../../providers/items-provider';
import { SessionContext } from '../../providers/session-provider';
import { convertToReadableFormat, convertToReadableFormatWithTime } from '../../util';
import SimpleDialog from '../simple-dialog';
import WithPermission from '../with-permission';
import ItemTagsComponent from './item-tags';


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
        marginRight: '30px',
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
});

@withStyles(styles)
class StatefulItemDisplayComponent extends React.Component {

    constructor(props) {
        super(props);
        const { currentUser } = props.sessionState;
        this.state = {
        };
    }

    getHistoryActionText(event) {
        switch (event.type) {
            case 'CREATED':
                return 'Angelegt'
            case 'UPDATED':
                return 'Geändert'
            case 'QUANTITY_CHANGED':
                return 'Menge von ' + event.data.from + ' auf ' + event.data.to + ' geändert'
            case 'MOVED':
                return 'Von ' + event.data.from + ' nach ' + event.data.to + ' verschoben'
            case 'BROKEN':
                return 'Defekt gemeldet'
            case 'FIXED':
                return 'Reparatur gemeldet'
        }
        return '';
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
        return sessionState.hasPermission('ROLE_RIGHT_ITEMS_NOTES_DELETE')
            && (userId === sessionState.currentUser.id || sessionState.hasPermission('ROLE_RIGHT_ITEMS_NOTES_DELETE_FOREIGN'));
    }

    render() {
        const { classes, itemsState } = this.props;
        const item = itemsState.getSelectedItem();

        return (
            <div>
                <div className={classes.title}>{item.name}</div>
                <img className={classes.image} src={item.pictureUrl} />
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
                            Menge + Einheit
                    </div>
                        {item.quantity} {item.unit}<br />
                        <br />
                        <div className={classes.bold}>
                            Maße
                    </div>
                    Breite: {item.width} cm<br />
                    Höhe: {item.height} cm<br />
                    Tiefe: {item.depth} cm<br />
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
                        {item.description}<br />
                        <br />
                        <div className={classes.bold}>
                            Gewerk
                    </div>
                        {item.technicalCrewName}
                    </div>
                </div>
                <div className={classes.container}>
                    <Typography variant="h6">Protokoll</Typography>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Datum</TableCell>
                                <TableCell>Benutzer</TableCell>
                                <TableCell>Aktion</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>

                            {item.history ? item.history.map(event => (
                                <TableRow key={event.id}>
                                    <TableCell>{convertToReadableFormat(event.timestamp.local())}</TableCell>
                                    <TableCell>
                                        {this.getFirstAndLastName(event)}
                                    </TableCell>
                                    <TableCell>{this.getHistoryActionText(event)}</TableCell>
                                </TableRow>
                            )) : (
                                    <TableRow>
                                        <TableCell colSpan={3}>
                                            <CircularProgress />
                                        </TableCell>
                                    </TableRow>
                                )}
                        </TableBody>
                    </Table>
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
                                    <Typography variant="caption">{convertToReadableFormatWithTime(note.timestamp.local())}</Typography>
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
                            {note.note}<br />
                            <Divider className={classes.divider} />
                        </div>
                    )) : (<CircularProgress />)}
                    <WithPermission permission="ROLE_RIGHT_ITEMS_NOTES_POST">
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
                    <a href="javascript:alert('Umleitung zum Nagel')">Nagel</a><br />
                    <a href="javascript:alert('Umleitung zum Meißel')">Meißel</a><br />
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