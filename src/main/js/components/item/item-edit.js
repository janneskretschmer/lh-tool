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
import { SessionContext } from '../../providers/session-provider';
import { ItemsContext } from '../../providers/items-provider';
import ItemSlotEditComponent from './item-slot';
import { isStringBlank } from '../../util';
import IdNameSelect from '../util/id-name-select';
import ItemListComponent from './item-list';

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
    technicalCrew: {
        minWidth: '100px',
    },
});

@withStyles(styles)
class StatefulItemEditComponent extends React.Component {

    constructor(props) {
        super(props);
        const { currentUser } = props.sessionState;
        this.state = {
        };
    }

    componentDidMount() {
    }

    render() {
        const { classes, itemsState } = this.props;
        const item = itemsState.getSelectedItem();
        return (
            <div>
                <div className={classes.verticallyCenteredContainer}>
                    <TextField
                        id="name"
                        label="Name"
                        className={classes.textField}
                        value={item.name}
                        onChange={event => itemsState.changeItemName(event.target.value)}
                        margin="dense"
                        variant="outlined"
                    /><TextField
                        id="id"
                        label="Eindeutiger Bezeichner"
                        className={classes.textField}
                        value={item.identifier}
                        onChange={event => itemsState.changeItemIdentifier(event.target.value)}
                        margin="dense"
                        variant="outlined"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={item.hasBarcode}
                                onChange={event => itemsState.changeItemHasBarcode(event.target.checked)}
                                disableRipple
                            />
                        }
                        label="ist Barcode"
                    />
                </div>
                <ItemSlotEditComponent />
                <br />
                <br />
                <input
                    accept="image/png,image/jpeg"
                    className={classes.imageInput}
                    id="image"
                    type="file"
                    onChange={event => itemsState.changeImage(event.target.files.length > 0 && event.target.files[0])}
                />
                <label htmlFor="image">
                    <Button variant="contained" component="span" className={classes.button}>
                        Bild auswählen
                    </Button>
                </label>
                <img className={classes.image} src={item.imageUrl} />&nbsp;
                {item.imageName}
                <br /><br />
                <div className={classes.verticallyCenteredContainer}>
                    <TextField
                        id="quantity"
                        label="Menge"
                        className={classes.textField}
                        value={item.quantity}
                        onChange={event => itemsState.changeItemQuantity(event.target.value)}
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
                        onChange={event => itemsState.changeItemUnit(event.target.value)}
                        margin="dense"
                        variant="outlined"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={item.consumable}
                                onChange={event => itemsState.changeItemConsumable(event.target.checked)}
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
                    onChange={event => itemsState.changeItemWidth(event.target.value)}
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
                    onChange={event => itemsState.changeItemHeight(event.target.value)}
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
                    onChange={event => itemsState.changeItemDepth(event.target.value)}
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
                            onChange={event => itemsState.changeItemOutsideQualified(event.target.checked)}
                            disableRipple
                        />
                    }
                    label="Wetterbeständig"
                />
                <br />
                <br />
                <TextField
                    id="description"
                    label="Beschreibung"
                    multiline
                    className={classes.textArea}
                    value={item.description}
                    onChange={event => itemsState.changeItemDescription(event.target.value)}
                    margin="dense"
                    variant="outlined"
                /><br />
                <br />
                {itemsState.technicalCrews ? (
                    <IdNameSelect
                        label="Gewerk"
                        className={classes.technicalCrew}
                        value={item.technicalCrewId}
                        onChange={value => itemsState.changeItemTechnicalCrewId(value)}
                        data={itemsState.technicalCrews}
                    />
                ) : (
                        <CircularProgress />
                    )}
                <br />
                <br />
                <br />
                <div className={classes.bold}>
                    Zugehörig
                </div>
                <ItemListComponent
                    keepSelectedItem
                    hideAdd
                    hideHeader
                    hiddenItemId={item.id}
                    selected={item.items && item.items.map(relatedItem => relatedItem.id) || []}
                    onToggleSelect={id => itemsState.toggleItemRelation(id)}
                />
            </div>
        );
    }
}



const ItemEditComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <ItemsContext.Consumer>
                    {itemsState => (
                        <StatefulItemEditComponent {...props} sessionState={sessionState} itemsState={itemsState} />
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemEditComponent;