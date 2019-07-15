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
import React from 'react';
import { Link } from 'react-router-dom';
import { fullPathOfItem } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';

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
            shelves: [
                {
                    id: 0, name: 'A', outside: false, slots: [
                        {
                            id: 0, name: 1, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Hammer' },
                                { id: 2, name: 'Nagel' },
                                { id: 3, name: 'Säge' },
                            ]
                        },
                        {
                            id: 1, name: 2, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Schrauben' },
                                { id: 2, name: 'Wasserwaage' },
                                { id: 3, name: 'Silikon' },
                                { id: 4, name: 'Spritzpistole' },
                            ]
                        },
                        {
                            id: 2, name: 3, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Zange' },
                                { id: 2, name: 'Schraubenzieher' },
                            ]
                        },
                        {
                            id: 3, name: 4, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Seitenschneider' },
                                { id: 2, name: 'Schraubenzieher' },
                                { id: 3, name: 'Kombizange' },
                            ]
                        },
                        {
                            id: 4, name: 5, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Seitenschneider' },
                            ]
                        },
                        {
                            id: 5, name: 6, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Schrauben' },
                                { id: 2, name: 'Wasserwaage' },
                                { id: 3, name: 'Silikon' },
                                { id: 4, name: 'Spritzpistole' },
                            ]
                        },
                    ]
                },
                {
                    id: 1, name: 'B', outside: false, slots: [
                        {
                            id: 0, name: 1, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Hammer' },
                                { id: 2, name: 'Nagel' },
                                { id: 3, name: 'Säge' },
                            ]
                        },
                        {
                            id: 1, name: 2, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Schrauben' },
                                { id: 2, name: 'Wasserwaage' },
                                { id: 3, name: 'Silikon' },
                                { id: 4, name: 'Spritzpistole' },
                            ]
                        },
                        {
                            id: 2, name: 3, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Zange' },
                                { id: 2, name: 'Schraubenzieher' },
                            ]
                        },
                        {
                            id: 3, name: 4, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Seitenschneider' },
                                { id: 2, name: 'Schraubenzieher' },
                                { id: 3, name: 'Kombizange' },
                            ]
                        },
                        {
                            id: 4, name: 5, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Seitenschneider' },
                            ]
                        },
                        {
                            id: 5, name: 6, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Schrauben' },
                                { id: 2, name: 'Wasserwaage' },
                                { id: 3, name: 'Silikon' },
                                { id: 4, name: 'Spritzpistole' },
                            ]
                        },
                    ]
                },
                {
                    id: 2, name: 'C', outside: false, slots: [
                        {
                            id: 0, name: 1, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Hammer' },
                                { id: 2, name: 'Nagel' },
                                { id: 3, name: 'Säge' },
                            ]
                        },
                        {
                            id: 4, name: 5, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Seitenschneider' },
                            ]
                        },
                        {
                            id: 5, name: 6, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Schrauben' },
                                { id: 2, name: 'Wasserwaage' },
                                { id: 3, name: 'Silikon' },
                                { id: 4, name: 'Spritzpistole' },
                            ]
                        },
                    ]
                },
                {
                    id: 3, name: 'Schaufelständer', outside: true, slots: [
                        {
                            id: 0, name: null, width: 30, height: 40, depth: 50, items: [
                                { id: 1, name: 'Grabschaufel' },
                                { id: 2, name: 'Grabschaufel' },
                                { id: 3, name: 'Spaten' },
                            ]
                        },
                    ]
                },
            ],
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
        return (
            <>
                [Detail-Ansicht für {match.params.id}]
                <div>
                    <div className={classes.title}>Kehlheim</div>
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
                    {shelves.map((shelf) => (
                        <table key={shelf.id} className={classes.shelfWrapper}>
                            <thead>
                                <tr>
                                    <td className={classes.shelfHeader}>
                                        <div className={classes.leftRight}>
                                            <div className={classes.shelfName}>
                                                {shelf.name}
                                            </div>
                                            {edit ? (
                                                <span>
                                                    <IconButton className={classes.noPadding}
                                                        onClick={() => alert('TODO: Dialog öffnen, um Daten ändern und speichern zu können.')}>
                                                        <EditIcon />
                                                    </IconButton>
                                                    <IconButton className={classes.noPadding}
                                                        onClick={() => alert('TODO: Fragen, ob nur Regal oder auch Artikel gelöscht werden sollen.')}>
                                                        <DeleteIcon />
                                                    </IconButton>
                                                </span>
                                            ) : null}
                                        </div>
                                        <div>
                                            {shelf.outside ? 'Draußen' : 'Drinnen'}
                                        </div>
                                    </td>
                                </tr>
                            </thead>
                            <tbody>
                                {shelf.slots.map((slot) => (
                                    <tr key={slot.id}>
                                        <td className={classes.slot}>
                                            <div className={classes.leftRight}>
                                                <span>{shelf.name} {slot.name}</span>
                                                {edit ? (
                                                    <span>
                                                        <IconButton className={classes.noPadding}
                                                            onClick={() => alert('TODO: Dialog öffnen, um Daten ändern und speichern zu können.')}>
                                                            <EditIcon />
                                                        </IconButton>
                                                        <IconButton className={classes.noPadding}
                                                            onClick={() => alert('TODO: Fragen, ob nur Fach oder auch Artikel gelöscht werden sollen.')}>
                                                            <DeleteIcon />
                                                        </IconButton>
                                                    </span>
                                                ) : null}
                                            </div>
                                            <div className={classes.items}>
                                                {slot.items.map((item) => (
                                                    <div className={classes.leftRight}>
                                                        <Link key={item.id} to={fullPathOfItem(item.id)}>{item.name}</Link>
                                                        {edit ? (
                                                            <IconButton className={classes.noPadding}
                                                                onClick={() => alert('TODO: Artikel aus Regal entfernen (jedoch nicht löschen?).')}>
                                                                <CloseIcon />
                                                            </IconButton>
                                                        ) : null}
                                                    </div>
                                                ))}
                                            </div>
                                            {edit ? (
                                                <div className={classes.centered}>
                                                    <IconButton
                                                        onClick={() => alert('TODO: Seite zum Anlegen von Artikeln öffnen.')}>
                                                        <AddIcon />
                                                    </IconButton>
                                                </div>
                                            ) : null}
                                        </td>
                                    </tr>
                                ))}
                                {edit ? (
                                    <tr>
                                        <td className={classes.slot}>
                                            <div className={classes.centered}>
                                                <Button
                                                    onClick={() => alert('TODO: Dialog öffnen, um Daten eingeben und speichern zu können.')}>
                                                    Fach hinzufügen
                                                </Button>
                                            </div>
                                        </td>
                                    </tr>
                                ) : null}
                            </tbody>
                        </table>
                    ))}
                    {edit ? (
                        <table className={classes.shelfWrapper}>
                            <thead>
                                <tr>
                                    <td className={classes.shelfHeader}>
                                        <div className={classes.centered}>
                                            <Button
                                                onClick={() => alert('TODO: Dialog öffnen, um Daten eingeben und speichern zu können.')}>
                                                Regal hinzufügen
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            </thead>
                        </table>
                    ) : null}
                </div>
                {edit ? (
                    <>
                        <Button variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(false)}>
                            Speichern
                        </Button>
                    </>
                ) : (
                        <>
                            <Button variant="contained" className={classes.button} onClick={() => this.changeEditState(true)}>
                                Bearbeiten
                        </Button>
                            <Button variant="outlined" className={classes.button} onClick={() => alert('TODO: implement "Zerstörung"')}>
                                Löschen
                        </Button>
                        </>
                    )}
            </>
        )
    }
}
