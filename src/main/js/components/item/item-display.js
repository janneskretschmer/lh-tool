import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { Button, CircularProgress, Divider, IconButton, TextField } from '@mui/material';
import React from 'react';
import { Link } from 'react-router-dom';
import { fullPathOfItemData } from '../../paths';
import { RIGHT_ITEMS_NOTES_DELETE, RIGHT_ITEMS_NOTES_DELETE_FOREIGN, RIGHT_ITEMS_NOTES_POST, RIGHT_ITEMS_PATCH_QUANTITY } from '../../permissions';
import { ItemsContext } from '../../providers/items-provider';
import { SessionContext } from '../../providers/session-provider';
import { convertToDDMMYYYY_HHMM } from '../../util';
import SimpleDialog from '../simple-dialog';
import BoldText from '../util/bold-text';
import WithPermission from '../with-permission';
import ItemTagsComponent from './item-tags';
import { Box } from '@mui/system';


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
        const { itemsState } = this.props;
        const item = itemsState.getSelectedItem();

        return (
            <div>
                <Box sx={{
                    fontSize: '30px',
                    marginBottom: '10px',
                    textAlign: 'center',
                }}>
                    {item.name}
                </Box>
                {item.imageUrl &&
                    <Box component="img" sx={{
                        float: 'right',
                        maxWidth: '100%',
                        display: 'inline-block',
                        verticalAlign: 'top',
                    }} src={item.imageUrl} />
                }
                <Box sx={{
                    display: 'inline-block',
                    verticalAlign: 'top',
                    mr: 3,
                    mb: 3,
                }}>
                    <Box sx={{ typography: 'h6' }}>Daten</Box>
                    <Box sx={{
                        display: 'inline-block',
                        verticalAlign: 'top',
                        mr: 3,
                        mb: 3,
                    }}>
                        <BoldText>
                            Eindeutiger Bezeichner{item.hasBarcode ? ' (Barcode)' : ''}
                        </BoldText>
                        {item.identifier}<br />
                        <br />
                        <BoldText>
                            Lagerplatz
                        </BoldText>
                        {item.storeName}: {item.slotName}
                        <br />
                        <br />
                        <BoldText>
                            Menge
                        </BoldText>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            {itemsState.modifiedQuantity ? (<>
                                <TextField
                                    sx={{ width: '115px' }}
                                    value={itemsState.modifiedQuantity}
                                    onChange={event => itemsState.changeModifiedQuantity(event.target.value)}
                                    size="small"
                                    variant="outlined"
                                    type="number"
                                    inputProps={{ min: '1' }}
                                />
                                <IconButton
                                    disabled={itemsState.actionsDisabled}
                                    onClick={event => itemsState.saveQuantity()}
                                    size="large">
                                    <CheckIcon />
                                </IconButton>
                                <IconButton
                                    disabled={itemsState.actionsDisabled}
                                    onClick={event => itemsState.resetQuantity()}
                                    size="large">
                                    <CloseIcon />
                                </IconButton>
                            </>) : (<>
                                {item.quantity} {item.unit}
                                <WithPermission permission={RIGHT_ITEMS_PATCH_QUANTITY}>
                                    <IconButton
                                        disabled={itemsState.actionsDisabled}
                                        onClick={event => itemsState.editQuantity()}
                                        size="large">
                                        <EditIcon />
                                    </IconButton>
                                </WithPermission>
                            </>)}
                        </Box>
                        <BoldText>
                            Gewerk
                        </BoldText>
                        {item.technicalCrewName}<br />
                        <br />
                    </Box>
                    <Box sx={{
                        display: 'inline-block',
                        verticalAlign: 'top',
                        mr: 3,
                        mb: 3,
                    }}>
                        <BoldText>
                            Verbrauchsgegenstand
                        </BoldText>
                        {item.consumable ? 'Ja' : 'Nein'}<br />
                        <br />
                        <BoldText>
                            Wetterbeständig
                        </BoldText>
                        {item.outsideQualified ? 'Ja' : 'Nein'}<br />
                        <br />
                        <BoldText>
                            Beschreibung
                        </BoldText>
                        <Box sx={{ maxWidth: '450px', textAlign: 'justify' }}>
                            {item.description}
                        </Box>
                        <br />
                        <BoldText>
                            Status
                        </BoldText>
                        {item.state}
                    </Box>
                </Box>
                <Box sx={{
                    display: 'inline-block',
                    verticalAlign: 'top',
                    mr: 3,
                    mb: 3,
                }}>
                    <Box sx={{ typography: 'h6' }}>Notizen</Box>
                    {item.notes ? item.notes.map(note => (
                        <div key={note.id}>
                            <Box sx={{
                                fontWeight: '500',
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                            }}>
                                <div>
                                    <BoldText>
                                        {this.getFirstAndLastName(note)}
                                    </BoldText>
                                    <Box sx={{ typography: "caption" }}>{convertToDDMMYYYY_HHMM(note.timestamp)}</Box>
                                </div>
                                {this.showNoteDeleteButton(note) &&
                                    <IconButton
                                        onClick={() => itemsState.deleteNote(note)}
                                        disabled={itemsState.actionsDisabled}
                                        size="large">
                                        <DeleteIcon />
                                    </IconButton>
                                }
                            </Box>
                            <Box sx={{ maxWidth: '450px', textAlign: 'justify' }}>
                                {note.note}
                            </Box>
                            <Divider sx={{ mt: 1, mb: 1 }} />
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
                </Box>
                <br />
                <Box sx={{ mr: 3, mb: 3 }}>
                    <Box sx={{ typography: 'h6' }}>Schlagwörter</Box>
                    <ItemTagsComponent />
                </Box>
                <br />
                <Box sx={{
                    display: 'inline-block',
                    verticalAlign: 'top',
                    mr: 3,
                    mb: 3,
                }}>
                    <Box sx={{ typography: 'h6' }}>Zugehörige Artikel</Box>
                    {item.items ? item.items.map(relatedItem => (
                        <div key={relatedItem.id}>
                            <Link sx={{
                                color: 'primary.main',
                                textDecoration: 'none',
                                cursor: 'pointer',
                            }} to={fullPathOfItemData(relatedItem.id)}>{relatedItem.name} ({relatedItem.identifier})</Link>
                        </div>
                    )) : (<CircularProgress />)}
                </Box>
            </div >
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