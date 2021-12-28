import EditIcon from '@mui/icons-material/Edit';
import { Button, CircularProgress, IconButton, Link, TextField } from '@mui/material';
import React from 'react';
import { fetchSlot } from '../../actions/slot';
import { fetchStore } from '../../actions/store';
import { fullPathOfSlot, fullPathOfStore } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import SimpleDialog from '../simple-dialog';

@withContext('sessionState', SessionContext)
export default class SlotFieldComponent extends React.Component {

    constructor(props) {
        super(props);
        const { currentUser } = props.sessionState;
        this.state = {

        };
    }

    mounted = false
    componentDidMount() {
        this.mounted = true
        this.props.slotId && fetchSlot({ accessToken: this.props.sessionState.accessToken, slotId: this.props.slotId }).then(
            slot => this.mounted && this.setState({ slot }, () => fetchStore({ accessToken: this.props.sessionState.accessToken, storeId: this.state.slot.storeId }).then(
                store => this.mounted && this.setState({ store })
            ))
        );
    }
    componentWillUnmount() {
        this.mounted = false
    }

    render() {
        const { classes, slotId, edit } = this.props;
        const { store, slot } = this.state;

        return <>
            {
                store && slot ? (
                    edit ?
                        store.name + ' ' + slot.name
                        : (
                            <>
                                <Link href={fullPathOfStore(store.id)}>{store.name}</Link>
                                &nbsp;
                                <Link href={fullPathOfSlot(slot.id)}>{slot.name}</Link>
                            </>
                        )
                ) : (
                    edit || (<CircularProgress />)
                )
            }
            {
                edit && (
                    <SimpleDialog
                        title={'Notiz hinzufügen'}
                        content={(<TextField
                            variant="outlined"
                            multiline
                            value={'test'}
                            onChange={null}
                        />)}
                        cancelText="Abbrechen"
                        okText={'Speichern'}
                        onOK={null}
                    >
                        {store && slot ? (
                            <IconButton size="large"><EditIcon /></IconButton>
                        ) : (
                            <Button variant="contained">
                                Lagerplatz auswählen
                            </Button>
                        )}

                    </SimpleDialog>
                )
            }
        </>;
    }
}
