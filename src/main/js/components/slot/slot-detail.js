import { Button, Checkbox, FormControlLabel } from '@material-ui/core';
import CircularProgress from '@material-ui/core/CircularProgress';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import CloseIcon from '@material-ui/icons/Close';
import EditIcon from '@material-ui/icons/Edit';
import SaveIcon from '@material-ui/icons/Save';
import React from 'react';
import { fullPathOfSlot, fullPathOfSlots } from '../../paths';
import { PageContext } from '../../providers/page-provider';
import { SlotsContext } from '../../providers/slots-provider';
import { getIdMapValues } from '../../util';
import IdNameSelect from '../util/id-name-select';
import LenientRedirect from '../util/lenient-redirect';

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
        marginRight: theme.spacing.unit * 3,
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
        const { classes, match, slotsState } = this.props;
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
                    <div className={classes.title}>
                        {edit ? (
                            <TextField
                                id="name"
                                label="Name"
                                className={classes.textField}
                                value={slot.name || ''}
                                onChange={event => slotsState.changeName(event.target.value)}
                                margin="dense"
                                variant="outlined"
                            />
                        ) : (slot.name)}
                        {edit ? (
                            slotsState.actionInProgress ? (<>&nbsp;<CircularProgress /></>) : (
                                <>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={() => slotsState.saveSelectedSlot()}>
                                        <SaveIcon />
                                    </IconButton>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={() => slotsState.resetSelectedSlot()}>
                                        <CloseIcon />
                                    </IconButton>
                                </>
                            )
                        ) : slot && (
                            <>
                                <IconButton variant="contained" className={classes.button} type="submit" onClick={() => slotsState.changeEdit(true)}>
                                    <EditIcon />
                                </IconButton>
                            </>
                        )}
                    </div>
                </div>
                <div className={classes.container}>
                    {stores ? (edit ? (
                        <IdNameSelect
                            label="Lager"
                            value={slot && slot.storeId}
                            onChange={value => slotsState.changeStoreId(value)}
                            data={stores}
                        />
                    ) : (
                            <>
                                <div className={classes.bold}>
                                    Lager
                            </div>
                                {slot.store && slot.store.name}
                            </>
                        )
                    ) : (<CircularProgress />)}
                    <br />
                    <br />
                    {edit ? (
                        <div className={classes.verticalCenteredContainer}>
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
                        </div>
                    ) : (
                            <>
                                <div className={classes.bold}>
                                    Draußen
                            </div>
                                {slot.outside ? 'Ja' : 'Nein'}<br />
                            </>
                        )}
                </div>
                {edit ? (
                    <>
                        <br />
                        <br />
                        <TextField
                            id="description"
                            label="Beschreibung"
                            multiline
                            className={classes.textField}
                            value={slot.description}
                            onChange={event => slotsState.changeDescription(event.target.value)}
                            margin="dense"
                            variant="outlined"
                        />
                    </>
                ) : (
                        <>
                            <div className={classes.bold}>
                                Beschreibung
                            </div>
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
                                className={classes.button}
                                type="submit"
                                onClick={() => slotsState.saveSelectedSlot()}>
                                Speichern
                            </Button>
                            <Button variant="outlined" className={classes.button} type="submit" onClick={() => slotsState.resetSelectedSlot()}>
                                Abbrechen
                            </Button>
                        </>
                    )
                    : (
                        <>
                            <Button
                                variant="contained"
                                className={classes.button}
                                onClick={() => this.redirectToSlots()}
                                disabled={slotsState.actionInProgress}
                            >
                                Übersicht
                            </Button>
                            <Button
                                variant="contained"
                                className={classes.button}
                                onClick={() => slotsState.changeEdit(true)}
                                disabled={slotsState.actionInProgress}
                            >
                                Bearbeiten
                            </Button>
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