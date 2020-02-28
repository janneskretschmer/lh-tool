import { Checkbox, Button } from '@material-ui/core';
import CircularProgress from '@material-ui/core/CircularProgress';
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import CloseIcon from '@material-ui/icons/Close';
import EditIcon from '@material-ui/icons/Edit';
import SaveIcon from '@material-ui/icons/Save';
import React from 'react';
import { createOrUpdateSlot, fetchSlot } from '../../actions/slot';
import { fetchOwnStores } from '../../actions/store';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import ItemListComponent from '../item/item-list';

const styles = theme => ({
    button: {
        marginRight: theme.spacing.unit,
    },
    bold: {
        fontWeight: '500',
    },
    title: {
        fontSize: '30px',
        marginBottom: '10px',
    },
    container: {
        display: 'inline-block',
        verticalAlign: 'top',
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        marginTop: theme.spacing.unit,
    },
    verticalCenteredContainer: {
        display: 'flex',
        alignItems: 'center',
    },
    margin: {
        margin: theme.spacing.unit,
    },
    textField: {
        marginRight: theme.spacing.unit
    }

});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class SlotDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            edit: props.new,
        };
    }


    changeEditState(edit, callback) {
        this.setState({
            edit,
            saving: false,
        }, callback);
    }

    changeTitle(event) {
        const name = event.target.value
        this.setState(prevState => ({
            slot: {
                ...prevState.slot,
                name,
            }
        }));
    }

    changeDescription(event) {
        const description = event.target.value
        this.setState(prevState => ({
            slot: {
                ...prevState.slot,
                description,
            }
        }));
    }

    changeWidth(event) {
        const width = event.target.value
        this.setState(prevState => ({
            slot: {
                ...prevState.slot,
                width,
            }
        }));
    }

    changeHeight(event) {
        const height = event.target.value
        this.setState(prevState => ({
            slot: {
                ...prevState.slot,
                height,
            }
        }));
    }

    changeDepth(event) {
        const depth = event.target.value
        this.setState(prevState => ({
            slot: {
                ...prevState.slot,
                depth,
            }
        }));
    }

    changeOutside(event) {
        const outside = event.target.checked
        this.setState(prevState => ({
            slot: {
                ...prevState.slot,
                outside,
            }
        }));
    }

    loadSlot() {
        const id = this.props.match.params.id
        if (id === 'new') {
            this.setState({
                edit: true,
                slot: {
                    name: '',
                    description: '',
                    width: '',
                    height: '',
                    depth: '',
                    outside: false,
                },
            });
        } else {
            fetchSlot({ accessToken: this.props.sessionState.accessToken, slotId: id }).then(slot => this.changeSlot(slot));
        }
    }

    changeSlot(slot, callback) {
        this.setState(prevState => ({
            slot,
        }), callback);
    }

    loadStores() {
        fetchOwnStores(this.props.sessionState.accessToken).then(stores => this.setState(prevState => ({
            stores,
            slot: {
                ...prevState.slot,
                storeId: prevState.slotId ? prevState.slotId : stores[0].id,
            }
        })));     
    }

    changeStore(event) {
        this.changeSlot({
            ...this.state.slot,
            storeId: event.target.value
        });
    }

    componentDidMount() {
        this.loadSlot();
        this.loadStores();
    }

    save() {
        this.setState({
            saving: true
        });
        createOrUpdateSlot({ accessToken: this.props.sessionState.accessToken, slot: this.state.slot }).then(slot => this.changeSlot(slot, () => this.changeEditState(false)));
    }

    cancel() {
        this.changeEditState(false,
            this.changeSlot(null, () => {
                this.loadSlot();
            })
        );
    }

    render() {
        const { classes, match } = this.props;
        const { edit, slot, stores, saving } = this.state;
        return slot || edit ? (
            <>
                <div>
                    <div className={classes.title}>
                        {edit ? (
                            <TextField
                                id="name"
                                label="Name"
                                className={classes.textField}
                                value={slot.name ? slot.name : ''}
                                onChange={this.changeTitle.bind(this)}
                                margin="dense"
                                variant="outlined"
                            />
                        ) : (slot.name)}
                        {edit ? (
                            saving ? (<>&nbsp;<CircularProgress /></>) : (
                                <>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={this.save.bind(this)}>
                                        <SaveIcon />
                                    </IconButton>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={this.cancel.bind(this)}>
                                        <CloseIcon />
                                    </IconButton>
                                </>
                            )
                        ) : slot && (
                            <>
                                <IconButton variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(true)}>
                                    <EditIcon />
                                </IconButton>
                            </>
                        )}
                    </div>
                </div>
                {stores ? (edit ? (
                    <FormControl className={classes.formControl}>
                        <InputLabel htmlFor="store">Lager</InputLabel>
                        <Select
                            value={slot && slot.storeId ? slot.storeId : stores[0].id}
                            onChange={this.changeStore.bind(this)}
                            inputProps={{
                                name: 'type',
                                id: 'type',
                            }}
                        >
                            {stores.map(store => (
                                <MenuItem key={store.id} value={store.id}>{store.name}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                ) : (
                        <>
                            <div className={classes.bold}>
                                Lager
                        </div>
                            {stores.find(store => store.id === slot.storeId).name}
                        </>
                    )
                ) : (<CircularProgress />)}
                <br />
                <br />
                {edit ? (
                    <TextField
                        id="description"
                        label="Beschreibung"
                        multiline
                        className={classes.textField}
                        value={slot.description}
                        onChange={this.changeDescription.bind(this)}
                        margin="dense"
                        variant="outlined"
                    />
                ) : (
                        <>
                            <div className={classes.bold}>
                                Beschreibung
                            </div>
                            {slot.description}
                        </>
                    )}
                <br />
                <br />
                <div className={classes.bold}>
                    Maße
                </div>
                {edit ? (
                    <>
                        <TextField
                            id="width"
                            label="Breite in cm"
                            className={classes.textField}
                            value={slot.width ? slot.width : ''}
                            onChange={this.changeWidth.bind(this)}
                            margin="dense"
                            variant="outlined"
                            type="number"
                            inputProps={{ min: "0" }}
                        />
                        <TextField
                            id="height"
                            label="Höhe in cm"
                            className={classes.textField}
                            value={slot.height ? slot.height : ''}
                            onChange={this.changeHeight.bind(this)}
                            margin="dense"
                            variant="outlined"
                            type="number"
                            inputProps={{ min: "0" }}
                        />
                        <TextField
                            id="depth"
                            label="Tiefe in cm"
                            className={classes.textField}
                            value={slot.depth ? slot.depth : ''}
                            onChange={this.changeDepth.bind(this)}
                            margin="dense"
                            variant="outlined"
                            type="number"
                            inputProps={{ min: "0" }}
                        />
                    </>
                ) : (
                        <>
                            Breite: {slot.width} cm<br />
                            Höhe: {slot.height} cm<br />
                            Tiefe: {slot.depth} cm<br />
                        </>
                    )}
                <br />
                <br />
                {edit ? (
                    <div className={classes.verticalCenteredContainer}>
                        <Checkbox
                            checked={slot.outside}
                            onChange={this.changeOutside.bind(this)}
                            disableRipple
                        />
                        Draußen
                    </div>
                ) : (
                        <>
                            <div className={classes.bold}>
                                Draußen
                </div>
                            {slot.outside ? 'Ja' : 'Nein'}
                        </>
                    )}
                {edit ?
                    saving ? (<>&nbsp;<CircularProgress /></>) : (
                        <>
                            <Button variant="contained" className={classes.button} type="submit" onClick={this.save.bind(this)}>
                                Speichern
                            </Button>
                            <Button variant="outlined" className={classes.button} type="submit" onClick={this.cancel.bind(this)}>
                                Abbrechen
                            </Button>
                        </>
                    )
                : (
                    <>
                        <br />
                        <br />
                        <div className={classes.bold}>
                            Artikel
                        </div>
                        <ItemListComponent storeId={slot.storeId} slotId={match.params.id * 1} />
                    </>
                )}
            </>
        ) : (<CircularProgress />);
    }
}
