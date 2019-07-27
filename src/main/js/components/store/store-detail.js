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

const styles = theme => ({
    button: {
        marginRight: theme.spacing.unit,
    },
    bold: {
        fontWeight: '500',
    },
    centered: {
        textAlign: 'center'
    },
    title: {
        fontSize: '30px',
        marginBottom: '10px',
    },
    image: {
        maxWidth: '400px',
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
    shelfWrapper: {
        borderCollapse: 'collapse',
        display: 'inline-block',
        verticalAlign: 'top',
        margin: theme.spacing.unit,
    },
    shelfHeader: {
        border: '1px solid ' + theme.palette.primary.main,
        padding: theme.spacing.unit,
    },
    shelfName: {
        fontWeight: 'bold',
    },
    slot: {
        border: '1px solid ' + theme.palette.primary.main,
        padding: theme.spacing.unit,
    },
    slotName: {
        fontWeight: '500',
    },
    leftRight: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: theme.spacing.unit,
    },
    noPadding: {
        padding: 0,
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
            edit: false,
        };
    }

    changeEditState(edit) {
        this.setState({
            edit
        })
    }

    render() {
        const { classes, match } = this.props
        const { shelves, edit } = this.state
        const id = match.params.id * 1;
        return (
            <>
                [Detail-Ansicht für {id}]
                <div>
                    <div className={classes.title}>
                        Kehlheim
                        {edit ? (
                            <>
                                <IconButton variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(false)}>
                                    <SaveIcon />
                                </IconButton>
                                <IconButton variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(false)}>
                                    <CloseIcon />
                                </IconButton>
                            </>
                        ) : (
                                <>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(true)}>
                                        <EditIcon />
                                    </IconButton>
                                </>
                            )}
                    </div>
                    <div className={classes.container}>
                        {edit ? (
                            <>
                                <TextField
                                    id="address"
                                    label="Adresse"
                                    multiline
                                    className={classes.textField}
                                    value={'Giselastr. 39 93309 Kelheim'}
                                    onChange={null}
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
                                    Giselastr. 39 93309 Kelheim<br />
                                    <br />
                                </>
                            )}
                        {edit ? (
                            <>
                                <FormControl className={classes.formControl}>
                                    <InputLabel htmlFor="type">Typ</InputLabel>
                                    <Select
                                        value={3}
                                        onChange={null}
                                        inputProps={{
                                            name: 'type',
                                            id: 'type',
                                        }}
                                    >
                                        <MenuItem value={1}>Lager</MenuItem>
                                        <MenuItem value={2}>Magazin</MenuItem>
                                        <MenuItem value={3}>Hauptlager</MenuItem>
                                    </Select>
                                </FormControl><br />
                                <br />
                            </>
                        ) : (
                                <>
                                    <div className={classes.bold}>
                                        Typ
                        </div>
                                    Hauptlager<br />
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
                <ItemListComponent store={id}/>
            </>
        )
    }
}
