import { CircularProgress, FormControlLabel } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import React from 'react';
import { fetchTechnicalCrews } from '../../actions/technical-crew';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import SlotFieldComponent from '../slot/slot-field';


const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },
    textField: {
        marginRight: theme.spacing.unit,
    },
    textArea: {
        width: '100%',
        maxWidth: '700px',
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
    },
    verticallyCenteredContainer: {
        display: 'flex',
        alignItems: 'center',
    },
});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class ItemEditComponent extends React.Component {

    constructor(props) {
        super(props);
        const { currentUser } = props.sessionState;
        this.state = {
            item: props.item,
        };
    }

    componentDidMount() {
        fetchTechnicalCrews(this.props.sessionState.accessToken).then(
            technicalCrews => this.setState(prevState => ({
                technicalCrews, item: {
                    ...prevState.item,
                    technicalCrewId: prevState.item.technicalCrewId ? prevState.item.technicalCrewId : technicalCrews[0].id,
                }
            }))
        );
    }

    changeItem(attribute, value) {
        this.setState(prevState => ({
            item: {
                ...prevState.item,
                [attribute]: value,
            }
        }));
    }
    render() {
        const { classes } = this.props;
        const { technicalCrews, item } = this.state;
        return (
            <div>
                <div className={classes.verticallyCenteredContainer}>
                    <TextField
                        id="name"
                        label="Name"
                        className={classes.textField}
                        value={item.name}
                        onChange={event => this.changeItem('name', event.target.value)}
                        margin="dense"
                        variant="outlined"
                    /><TextField
                        id="id"
                        label="Eindeutiger Bezeichner"
                        className={classes.textField}
                        value={item.identifier}
                        onChange={event => this.changeItem('identifier', event.target.value)}
                        margin="dense"
                        variant="outlined"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={item.hasBarcode}
                                onChange={event => this.changeItem('hasBarcode', event.target.checked)}
                                disableRipple
                            />
                        }
                        label="ist Barcode"
                    />
                </div>

                Lagerplatz: <SlotFieldComponent edit={true} slotId={item.slotId}/>
                <br />
                <br />
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
                <img className={classes.image} src="https://images-na.ssl-images-amazon.com/images/I/71tTWyypTKL._SX679_.jpg" />
                <br /><br />
                <div className={classes.verticallyCenteredContainer}>
                    <TextField
                        id="quantity"
                        label="Menge"
                        className={classes.textField}
                        value={item.quantity}
                        onChange={event => this.changeItem('quantity', event.target.value)}
                        margin="dense"
                        variant="outlined"
                        type="number"
                        inputProps={{ min: "1" }}
                    />
                    <TextField
                        id="unit"
                        label="Einheit"
                        className={classes.textField}
                        value={item.unit}
                        onChange={event => this.changeItem('unit', event.target.value)}
                        margin="dense"
                        variant="outlined"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={item.consumable}
                                onChange={event => this.changeItem('consumable', event.target.checked)}
                                disableRipple
                            />
                        }
                        label="Verbrauchsgegenstand"
                    />
                </div>
                <TextField
                    id="width"
                    label="Breite in cm"
                    className={classes.textField}
                    value={item.width}
                    onChange={event => this.changeItem('width', event.target.value)}
                    margin="dense"
                    variant="outlined"
                    type="number"
                    inputProps={{ min: "0" }}
                />
                <TextField
                    id="height"
                    label="Höhe in cm"
                    className={classes.textField}
                    value={item.height}
                    onChange={event => this.changeItem('height', event.target.value)}
                    margin="dense"
                    variant="outlined"
                    type="number"
                    inputProps={{ min: "0" }}
                />
                <TextField
                    id="depth"
                    label="Tiefe in cm"
                    className={classes.textField}
                    value={item.depth}
                    onChange={event => this.changeItem('depth', event.target.value)}
                    margin="dense"
                    variant="outlined"
                    type="number"
                    inputProps={{ min: "0" }}
                />
                <br />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={item.outsideQualified}
                            onChange={event => this.changeItem('outsideQualified', event.target.checked)}
                            disableRipple
                        />
                    }
                    label="Wetterbeständig"
                />
                <br />
                <br />
                <br />
                <TextField
                    id="description"
                    label="Beschreibung"
                    multiline
                    className={classes.textArea}
                    value={item.description}
                    onChange={event => this.changeItem('description', event.target.value)}
                    margin="dense"
                    variant="outlined"
                /><br />
                <br />
                <br />
                {technicalCrews ? (
                    <FormControl className={classes.formControl}>
                        <InputLabel htmlFor="technical-crew">Gewerk</InputLabel>
                        <Select
                            value={item.technicalCrewId}
                            onChange={event => this.changeItem('technicalCrewId', event.target.value)}
                            inputProps={{
                                name: 'technical-crew',
                                id: 'technical-crew',
                            }}
                        >
                            {technicalCrews.map(crew => (
                                <MenuItem key={crew.id} value={crew.id}>{crew.name}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                ) : (
                        <CircularProgress />
                    )}

                <br />
                <br />
                <br />
                <div className={classes.bold}>
                    Zugehörig
                </div>
                TODO: übersichtliche Auswahlliste mit Namen+Id+Projekt+Lagerplatz von Artikeln
                <br />
                <br />
            </div>
        );
    }
}


