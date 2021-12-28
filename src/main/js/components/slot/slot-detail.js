import CloseIcon from '@mui/icons-material/Close';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import { Button, Checkbox, FormControlLabel } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import { Box } from '@mui/system';
import React from 'react';
import { fullPathOfSlot, fullPathOfSlots } from '../../paths';
import { PageContext } from '../../providers/page-provider';
import { SlotsContext } from '../../providers/slots-provider';
import { getIdMapValues, getSlotBarcodeString } from '../../util';
import BarcodeGenerator from '../util/barcode-generator';
import BoldText from '../util/bold-text';
import IdNameSelect from '../util/id-name-select';
import LenientRedirect from '../util/lenient-redirect';

class StatefulSlotDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            redirectToUrl: null,
        };
    }

    componentDidMount() {
        this.props.slotsState.selectSlot(this.props.match.params.id);
    }


    componentDidUpdate() {
        const slot = this.props.slotsState.selectedSlot;
        if (!this.state.redirectToUrl && slot && slot.id && parseInt(this.props.match.params.id, 10) !== slot.id) {
            this.setState({ redirectToUrl: fullPathOfSlot(slot.id) });
        }
        if (slot && this.props.pageState.currentItemName !== slot.name) {
            this.props.pageState.setCurrentItemName(slot);
        }
    }

    redirectToSlots() {
        this.setState({ redirectToUrl: fullPathOfSlots() });
    }

    render() {
        const { slotsState } = this.props;
        const { redirectToUrl } = this.state;
        const slot = slotsState.selectedSlot;
        const edit = slotsState.edit;
        const stores = getIdMapValues(slotsState.stores);
        const saveDisabled = !slotsState.isSlotValid();

        if (redirectToUrl) {
            return (<LenientRedirect to={redirectToUrl} onSamePage={() => this.setState({ redirectToUrl: null })} />);
        }

        return slot ? (
            <>
                <div>
                    <Box sx={{ fontSize: '30px', mb: 1 }}>
                        {edit ? (
                            <TextField
                                id="name"
                                label="Name"
                                sx={{ mr: 1 }}
                                value={slot.name || ''}
                                onChange={event => slotsState.changeName(event.target.value)}
                                size="small"
                                variant="outlined"
                            />
                        ) : (slot.name)}
                        {edit ? (
                            slotsState.actionInProgress ? (<>&nbsp;<CircularProgress /></>) : (
                                <>
                                    <IconButton
                                        variant="contained"
                                        sx={{ mr: 1 }}
                                        type="submit"
                                        onClick={() => slotsState.saveSelectedSlot()}
                                        size="large">
                                        <SaveIcon />
                                    </IconButton>
                                    <IconButton
                                        variant="contained"
                                        sx={{ mr: 1 }}
                                        type="submit"
                                        onClick={() => slotsState.resetSelectedSlot()}
                                        size="large">
                                        <CloseIcon />
                                    </IconButton>
                                </>
                            )
                        ) : slot && (
                            <>
                                <IconButton
                                    variant="contained"
                                    sx={{ mr: 1 }}
                                    type="submit"
                                    onClick={() => slotsState.changeEdit(true)}
                                    size="large">
                                    <EditIcon />
                                </IconButton>
                            </>
                        )}
                    </Box>
                </div>
                <Box sx={{
                    display: 'inline-block',
                    verticalAlign: 'top',
                    mr: 3,
                    mb: 1
                }}>
                    {stores ? (edit ? (
                        <IdNameSelect
                            label="Lager"
                            value={slot && slot.storeId}
                            onChange={value => slotsState.changeStoreId(value)}
                            data={stores}
                        />
                    ) : (
                        <>
                            <BoldText>
                                Lager
                            </BoldText>
                            {slot.store && slot.store.name}
                        </>
                    )
                    ) : (<CircularProgress />)}
                    <br />
                    <br />
                    {edit ? (
                        <Box sx={{
                            display: 'flex',
                            alignItems: 'center',
                        }}>
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={slot.outside}
                                        onChange={event => slotsState.changeOutside(event.target.checked)}
                                        disableRipple
                                    />
                                }
                                label="Draußen"
                            />
                        </Box>
                    ) : (
                        <>
                            <BoldText>
                                Draußen
                            </BoldText>
                            {slot.outside ? 'Ja' : 'Nein'}<br />
                        </>
                    )}
                </Box>
                {edit ? (
                    <>
                        <br />
                        <br />
                        <TextField
                            id="description"
                            label="Beschreibung"
                            multiline
                            sx={{ mr: 1 }}
                            value={slot.description}
                            onChange={event => slotsState.changeDescription(event.target.value)}
                            size="small"
                            variant="outlined"
                        />
                    </>
                ) : (
                    <>
                        <BoldText>
                            Beschreibung
                        </BoldText>
                        {slot.description}
                    </>
                )}<br />
                <br />
                {edit ?
                    slotsState.actionInProgress ? (<>&nbsp;<CircularProgress /></>) : (
                        <>
                            <Button
                                disabled={saveDisabled}
                                variant="contained"
                                sx={{ mr: 1 }}
                                type="submit"
                                onClick={() => slotsState.saveSelectedSlot()}>
                                Speichern
                            </Button>
                            <Button variant="outlined" sx={{ mr: 1 }} type="submit" onClick={() => slotsState.resetSelectedSlot()}>
                                Abbrechen
                            </Button>
                        </>
                    )
                    : (
                        <>
                            <Button
                                variant="contained"
                                sx={{ mr: 1 }}
                                onClick={() => this.redirectToSlots()}
                                disabled={slotsState.actionInProgress}
                            >
                                Übersicht
                            </Button>
                            <Button
                                variant="contained"
                                sx={{ mr: 1 }}
                                onClick={() => slotsState.changeEdit(true)}
                                disabled={slotsState.actionInProgress}
                            >
                                Bearbeiten
                            </Button>
                            <BarcodeGenerator
                                content={getSlotBarcodeString(slot.storeId, slot.name)}
                            >
                                <Button
                                    variant="contained"
                                    sx={{ mr: 1 }}
                                    disabled={slotsState.actionInProgress}>
                                    Barcode generieren
                                </Button>
                            </BarcodeGenerator>
                        </>
                    )}
            </>
        ) : (<CircularProgress />);
    }
}

const SlotDetailComponent = props => (
    <>
        <PageContext.Consumer>
            {pageState => (
                <SlotsContext.Consumer>
                    {slotsState => (<StatefulSlotDetailComponent {...props} pageState={pageState} slotsState={slotsState} />)}
                </SlotsContext.Consumer>
            )}
        </PageContext.Consumer>
    </>
);
export default SlotDetailComponent;