import { Button, CircularProgress, Link, TextField } from '@material-ui/core';
import Chip from '@material-ui/core/Chip';
import { withStyles } from '@material-ui/core/styles';
import React from 'react';
import { createItemNote, fetchItemHistory, fetchItemNotes, fetchItemTags } from '../../actions/item';
import { fetchSlot } from '../../actions/slot';
import { fetchStore } from '../../actions/store';
import { fetchUser } from '../../actions/user';
import { fullPathOfSlot, fullPathOfStore } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { convertToReadableFormat, withContext } from '../../util';
import SimpleDialog from '../simple-dialog';
import { fetchTechnicalCrew } from '../../actions/technical-crew';
import SlotFieldComponent from '../slot/slot-field';

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
        marginBottom: '20px',
    },
    chip: {
        marginRight: theme.spacing.unit,
    },
});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class ItemDisplayComponent extends React.Component {

    constructor(props) {
        super(props);
        const { currentUser } = props.sessionState;
        this.state = {
            users: {
                [currentUser.id]: currentUser.firstName + ' ' + currentUser.lastName,
            },
            note: '',
        };
    }

    saveNote() {
        if (this.state.note && this.state.note.length > 0) {
            createItemNote({
                accessToken: this.props.sessionState.accessToken, itemNote: {
                    note: this.state.note,
                    itemId: this.props.item.id,
                }
            }).then(note => this.setState(prevState => ({
                note: '',
                notes: [
                    ...prevState.notes,
                    note,
                ]
            })));
        }
    }

    componentDidMount() {
        fetchSlot({ accessToken: this.props.sessionState.accessToken, slotId: this.props.item.slotId }).then(
            slot => this.setState({ slot }, () => fetchStore({ accessToken: this.props.sessionState.accessToken, storeId: this.state.slot.storeId }).then(
                store => this.setState({ store })
            ))
        );
        fetchItemNotes({ accessToken: this.props.sessionState.accessToken, itemId: this.props.item.id }).then(
            notes => this.setState({ notes }, () => this.state.notes.map(note => note.userId).filter((v, i, a) => a.indexOf(v) === i).map(
                userId => fetchUser({ accessToken: this.props.sessionState.accessToken, userId }).then(
                    user => this.setState(prevState => ({
                        users: {
                            ...prevState.users,
                            [user.id]: user.firstName + ' ' + user.lastName,
                        }
                    }))
                )
            ))
        );
        fetchItemTags({ accessToken: this.props.sessionState.accessToken, itemId: this.props.item.id }).then(
            tags => this.setState({ tags })
        );
        fetchItemHistory({ accessToken: this.props.sessionState.accessToken, itemId: this.props.item.id }).then(
            history => this.setState({ history })
        );
        fetchTechnicalCrew({ accessToken: this.props.sessionState.accessToken, technicalCrewId: this.props.item.technicalCrewId }).then(
            technicalCrew => this.setState({ technicalCrew })
        );
    }

    getHistoryText(historyEntry) {
        let res = convertToReadableFormat(historyEntry.timestamp) + ': '
        switch (historyEntry.type) {
            case 'CREATED':
                res += 'Angelegt'
                break;
            case 'UPDATED':
                res += 'Geändert'
                break;
            case 'MOVED':
                res += 'Von ' + historyEntry.data.from + ' nach ' + historyEntry.data.to + ' verschoben'
                break;
            case 'BROKEN':
                res += 'Defekt gemeldet'
                break;
            case 'FIXED':
                res += 'Reparatur gemeldet'
                break;
        }
        if (historyEntry.userId) {
            const { users } = this.state;
            res += ' von ' + (users[historyEntry.userId] ? users[historyEntry.userId] : (<CircularProgress size="12" />));
        }
        return res;
    }

    render() {
        const { classes, item } = this.props;
        const { store, slot, notes, note, users, tags, history, technicalCrew } = this.state;

        return (
            <div>
                <div className={classes.title}>Hammer</div>
                <img className={classes.image} src={item.pictureUrl} />
                <div className={classes.container}>
                    <div className={classes.bold}>
                        Eindeutiger Bezeichner{item.hasBarcode ? ' (Barcode)' : ''}
                    </div>
                    {item.identifier}<br />
                    <br />
                    <div className={classes.bold}>
                        Lagerplatz
                </div>
                    <SlotFieldComponent slotId={item.slotId} />
                    <br />
                    <br />
                    <div className={classes.bold}>
                        Menge+Einheit
                </div>
                    {item.quantity} {item.unit}<br />
                    <br />
                    <div className={classes.bold}>
                        Maße
                </div>
                    Breite: {item.width} cm<br />
                    Höhe: {item.height} cm<br />
                    Tiefe: {item.depth} cm<br />
                    <br />
                    <div className={classes.bold}>
                        Verbrauchsgegenstand
                </div>
                    {item.consumable ? 'Ja' : 'Nein'}<br />
                    <br />
                    <div className={classes.bold}>
                        Beschreibung
                </div>
                    {item.description}<br />
                    <br />
                    <div className={classes.bold}>
                        Tags
                </div>
                    {tags ? tags.map(tag => (
                        <Chip key={tag.name} label={tag.name} className={classes.chip} />
                    )) : (<CircularProgress />)}
                    <br />
                    <br />
                    <div className={classes.bold}>
                        Gewerk
                </div>
                    {technicalCrew ? technicalCrew.name : (<CircularProgress size={12} />)}
                    <br />
                    <br />
                    <div className={classes.bold}>
                        Protokoll
                </div>
                    {history ? history.map(entry => (
                        <div key={entry.id}>
                            {this.getHistoryText(entry)}
                        </div>
                    )) : (<CircularProgress />)}
                    <br />
                    <div className={classes.bold}>
                        Zugehörig
                </div>
                    <a href="javascript:alert('Umleitung zum Nagel')">Nagel</a><br />
                    <a href="javascript:alert('Umleitung zum Meißel')">Meißel</a><br />
                    <br />
                    <div className={classes.bold}>
                        Notizen
                </div>
                    {notes ? notes.map(note => (
                        <div key={note.id}>
                            {convertToReadableFormat(note.timestamp)} von {users[note.userId] ? users[note.userId] : (<CircularProgress size="12" />)}<br />
                            {note.note}<br />
                            <br />
                        </div>
                    )) : (<CircularProgress />)}

                    <SimpleDialog
                        title={'Notiz hinzufügen'}
                        content={(<TextField
                            variant="outlined"
                            multiline
                            value={note}
                            onChange={event => this.setState({ note: event.target.value })}
                        />)}
                        cancelText="Abbrechen"
                        okText={'Speichern'}
                        onOK={this.saveNote.bind(this)}
                    >
                        <Button variant="contained">
                            Notiz hinzufügen
                        </Button>
                    </SimpleDialog>
                </div>
            </div>
        );
    }
}
