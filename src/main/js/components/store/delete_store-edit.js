import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Chip from '@material-ui/core/Chip';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import InputLabel from '@material-ui/core/InputLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Button from '@material-ui/core/Button';

const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },
    textField: {
        marginRight: theme.spacing.unit,
    },
    bottom: {
        display: 'inline-block',
        verticalAlign: 'bottom',
    },
    imageInput: {
        display: 'none',
    },
    image: {
        display: 'inline-block',
        verticalAlign: 'bottom',
        maxHeight: '36px',
        marginLeft: theme.spacing.unit,
    }
});

const StoreEditComponent = props => {
    const {classes} = props
    return (
        <div>
            <TextField
                id="name"
                label="Name"
                className={classes.textField}
                value={'Hammer'}
                onChange={null}
                margin="dense"
                variant="outlined"
            /><TextField
                id="id"
                label="Eindeutiger Bezeichner"
                className={classes.textField}
                value={'9515301538'}
                onChange={null}
                margin="dense"
                variant="outlined"
              />
            <div className={classes.bottom}>
                <Checkbox
                    checked={true}
                    disableRipple
                /> ist Barcode
            </div><br /><br />
            <input
                accept="image/*"
                className={classes.imageInput}
                id="image"
                multiple
                type="file"
            />
            <label htmlFor="image">
                <Button variant="contained" component="span" className={classes.button}>
                    Bild auswählen
                </Button>
            </label>
            <img className={classes.image} src="https://images-na.ssl-images-amazon.com/images/I/71tTWyypTKL._SX679_.jpg"/>
            <br /><br />
            <TextField
                id="quantity"
                label="Menge"
                className={classes.textField}
                value={'1'}
                onChange={null}
                margin="dense"
                variant="outlined"
            />
            <TextField
                id="id"
                label="Einheit"
                className={classes.textField}
                value={'Stück'}
                onChange={null}
                margin="dense"
                variant="outlined"
            /><br />
            <TextField
                id="length"
                label="Länge in cm"
                className={classes.textField}
                value={'20'}
                onChange={null}
                margin="dense"
                variant="outlined"
            />
            <TextField
                id="width"
                label="Breite in cm"
                className={classes.textField}
                value={'7'}
                onChange={null}
                margin="dense"
                variant="outlined"
            />
            <TextField
                id="height"
                label="Höhe in cm"
                className={classes.textField}
                value={'3'}
                onChange={null}
                margin="dense"
                variant="outlined"
            /><br />
            <Checkbox
                checked={false}
                disableRipple
            />
            Verbrauchsgegenstand<br />
            <TextField
                id="description"
                label="Beschreibung"
                multiline
                className={classes.textField}
                value={'Der Absolute Hammer'}
                onChange={null}
                margin="dense"
                variant="outlined"
            /><br />
            <br />
            <TextField
                id="tag"
                label="Tag hinzufügen"
                className={classes.textField}
                onChange={() => alert('TODO: Hinzufügen')}
                margin="dense"
            />
            <div className={classes.bottom}>
                <Chip label="Hammer" className={classes.chip} onDelete={() => alert('TODO: Entfernen')} />
                <Chip label="Absolut" className={classes.chip} onDelete={() => alert('TODO: Entfernen')}  />
                <Chip label="Schlagen" className={classes.chip} onDelete={() => alert('TODO: Entfernen')}  />
            </div><br />
            <br />
            <FormControl className={classes.formControl}>
                <InputLabel htmlFor="technical-crew">Gewerk</InputLabel>
                <Select
                    value={3}
                    onChange={null}
                    inputProps={{
                        name: 'technical-crew',
                        id: 'technical-crew',
                    }}
                >
                    <MenuItem value={1}>Maler</MenuItem>
                    <MenuItem value={2}>Elektriker</MenuItem>
                    <MenuItem value={3}>Zimmerer</MenuItem>
                </Select>
            </FormControl>
            <br />
            <br />
            <div className={classes.bold}>
                Zugehörig
            </div>
            TODO: übersichtliche Auswahlliste mit Namen+Id+Projekt+Lagerplatz von Artikeln
            <br />
            <br />
        </div>
        )
    }


    export default withStyles(styles)(StoreEditComponent);
