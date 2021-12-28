import { CircularProgress, FormControlLabel } from '@mui/material';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import TextField from '@mui/material/TextField';
import { Box } from '@mui/system';
import React from 'react';
import { ItemsContext } from '../../providers/items-provider';
import { SessionContext } from '../../providers/session-provider';
import BoldText from '../util/bold-text';
import IdNameSelect from '../util/id-name-select';
import ItemListComponent from './item-list';
import ItemSlotEditComponent from './item-slot';

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
        const { itemsState } = this.props;
        const item = itemsState.getSelectedItem();

        const sxTextField = { mr: 1 };

        return (
            <div>
                <Box sx={{
                    display: 'flex',
                    alignItems: 'center',
                }}>
                    <TextField
                        id="name"
                        label="Name"
                        sx={sxTextField}
                        value={item.name}
                        onChange={event => itemsState.changeItemName(event.target.value)}
                        size="small"
                        variant="outlined"
                    /><TextField
                        id="id"
                        label="Eindeutiger Bezeichner"
                        sx={sxTextField}
                        value={item.identifier}
                        onChange={event => itemsState.changeItemIdentifier(event.target.value)}
                        size="small"
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
                </Box>
                <ItemSlotEditComponent />
                <br />
                <br />
                <Box
                    component="input"
                    accept="image/png,image/jpeg"
                    sx={{ display: 'none' }}
                    id="image"
                    type="file"
                    onChange={event => itemsState.changeImage(event.target.files.length > 0 && event.target.files[0])}
                />
                <label htmlFor="image">
                    <Button variant="contained" component="span">
                        Bild auswählen
                    </Button>
                </label>
                <Box
                    component="img"
                    sx={{
                        display: 'inline-block',
                        verticalAlign: 'bottom',
                        maxHeight: '36px',
                        ml: 1,
                    }}
                    src={item.imageUrl}
                />&nbsp;
                {item.imageName}
                <br /><br />
                <Box sx={{
                    display: 'flex',
                    alignItems: 'center',
                }}>
                    <TextField
                        id="quantity"
                        label="Menge"
                        sx={sxTextField}
                        value={item.quantity}
                        onChange={event => itemsState.changeItemQuantity(event.target.value)}
                        size="small"
                        variant="outlined"
                        type="number"
                        inputProps={{ min: '1' }}
                    />
                    <TextField
                        id="unit"
                        label="Einheit"
                        sx={sxTextField}
                        value={item.unit}
                        onChange={event => itemsState.changeItemUnit(event.target.value)}
                        size="small"
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
                </Box>
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
                    sx={{
                        width: '100%',
                        maxWidth: '700px',
                    }}
                    value={item.description}
                    onChange={event => itemsState.changeItemDescription(event.target.value)}
                    size="small"
                    variant="outlined"
                /><br />
                <br />
                {
                    itemsState.technicalCrews ? (
                        <IdNameSelect
                            label="Gewerk"
                            value={item.technicalCrewId}
                            onChange={value => itemsState.changeItemTechnicalCrewId(value)}
                            data={itemsState.technicalCrews}
                        />
                    ) : (
                        <CircularProgress />
                    )
                }
                <br />
                <br />
                <br />
                <BoldText>
                    Zugehörig
                </BoldText>
                <ItemListComponent
                    keepSelectedItem
                    hideAdd
                    hideHeader
                    hiddenItemId={item.id}
                    selected={item.items && item.items.map(relatedItem => relatedItem.id) || []}
                    onToggleSelect={id => itemsState.toggleItemRelation(id)}
                />
            </div >
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