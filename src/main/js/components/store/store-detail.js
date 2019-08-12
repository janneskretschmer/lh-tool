import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import AddIcon from '@material-ui/icons/Add';
import CloseIcon from '@material-ui/icons/Close';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';
import SaveIcon from '@material-ui/icons/Save';
import React from 'react';
import { Link } from 'react-router-dom';
import { fullPathOfItem } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import ItemListComponent from '../item/item-list';
import CircularProgress from '@material-ui/core/CircularProgress';
import { fetchStore, createOrUpdateStore } from '../../actions/store';

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
    }

});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class StoreDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            edit: props.new,
            store: null,

        };
    }


    changeEditState(edit, callback) {
        this.setState({
            edit,
            saving: false,
        }, callback)
    }

    changeTitle(event) {
        let name = event.target.value
        this.setState(prevState => ({
            store: {
                ...prevState.store,
                name,
            }
        }))
    }

    changeAddress(event) {
        let address = event.target.value
        this.setState(prevState => ({
            store: {
                ...prevState.store,
                address,
            }
        }))
    }

    changeType(event) {
        let type = event.target.value
        this.setState(prevState => ({
            store: {
                ...prevState.store,
                type,
            }
        }))
    }

    loadStore() {
        let id = this.props.match.params.id
        if (id === 'new') {
            this.setState({
                edit: true,
                store: {
                    name: '',
                    address: '',
                    type: 'STANDARD'
                },
            })
        } else {
            fetchStore({ accessToken: this.props.sessionState.accessToken, storeId: id }).then(store => this.changeStore(store))
        }
    }

    changeStore(store, callback) {
        this.setState({
            store
        }, callback)
    }

    componentDidMount() {
        this.loadStore()
    }

    save() {
        this.setState({
            saving: true
        })
        createOrUpdateStore({ accessToken: this.props.sessionState.accessToken, store: this.state.store }).then(store => this.changeStore(store, () => this.changeEditState(false)))
    }

    cancel() {
        this.changeEditState(false,
            this.changeStore(null, () => {
                this.loadStore()
            })
        )
    }

    render() {
        const { classes, match } = this.props
        const { edit, store, saving } = this.state
        const types = {
            MAIN: 'Hauptlager',
            STANDARD: 'Lager',
            MOBILE: 'Magazin',
        }
        return store || edit ? (
            <>
                <div>
                    <div className={classes.title}>
                        {edit ? (
                            <TextField
                                id="name"
                                label="Name"
                                className={classes.textField}
                                value={store.name}
                                onChange={this.changeTitle.bind(this)}
                                margin="dense"
                                variant="outlined"
                            />
                        ) : (store.name)}
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
                        ) : store ? (
                            <>
                                <IconButton variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(true)}>
                                    <EditIcon />
                                </IconButton>
                            </>
                        ) : null}
                    </div>
                    <div className={classes.container}>
                        {edit ? (
                            <>
                                <TextField
                                    id="address"
                                    label="Adresse"
                                    multiline
                                    className={classes.textField}
                                    value={store.address}
                                    onChange={this.changeAddress.bind(this)}
                                    margin="dense"
                                    variant="outlined"
                                /><br />
                                <br />
                            </>
                        ) : (
                                <>
                                    <div className={classes.bold}>
                                        Adresse
                                </div>
                                    {store.address}<br />
                                    <br />
                                </>
                            )}
                        {edit ? (
                            <>
                                <FormControl className={classes.formControl}>
                                    <InputLabel htmlFor="type">Typ</InputLabel>
                                    <Select
                                        value={store.type}
                                        onChange={this.changeType.bind(this)}
                                        inputProps={{
                                            name: 'type',
                                            id: 'type',
                                        }}
                                    >
                                        <MenuItem value={'STANDARD'}>{types['STANDARD']}</MenuItem>
                                        <MenuItem value={'MOBILE'}>{types['MOBILE']}</MenuItem>
                                        <MenuItem value={'MAIN'}>{types['MAIN']}</MenuItem>
                                    </Select>
                                </FormControl><br />
                                <br />
                            </>
                        ) : (
                                <>
                                    <div className={classes.bold}>
                                        Typ
                        </div>
                                    {types[store.type]}<br />
                                </>
                            )}
                        <br />
                        <div className={classes.bold}>
                            Projekte
                        </div>
                        {edit ? (
                            <>
                                <Checkbox
                                    checked={true}
                                    disableRipple
                                />
                                Altötting&nbsp;(01.11.17&nbsp;-&nbsp;01.12.18)<br />
                                <Checkbox
                                    checked={false}
                                    disableRipple
                                />
                                Project&nbsp;X&nbsp;(01.12.18&nbsp;-&nbsp;02.12.18)<br />
                                <Checkbox
                                    checked={true}
                                    disableRipple
                                />
                                Vöhringen&nbsp;(01.02.19&nbsp;-&nbsp;15.02.19)<br />
                                <Checkbox
                                    checked={true}
                                    disableRipple
                                />
                                Stuttgart&nbsp;(01.05.19&nbsp;-&nbsp;01.05.20)<br />
                                <Checkbox
                                    checked={false}
                                    disableRipple
                                />
                                Project&nbsp;Z&nbsp;(01.12.18&nbsp;-&nbsp;02.12.18)
                            </>
                        ) : (
                                <>
                                    Altötting&nbsp;(01.11.17&nbsp;-&nbsp;01.12.18)<br />
                                    Vöhringen&nbsp;(01.02.19&nbsp;-&nbsp;15.02.19)<br />
                                    Stuttgart&nbsp;(01.05.19&nbsp;-&nbsp;01.05.20)<br />
                                </>
                            )}
                    </div>
                </div>
                <ItemListComponent store={match.params.id * 1} />
            </>
        ) : (<CircularProgress />)
    }
}
