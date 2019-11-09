import { CircularProgress } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import { withStyles } from '@material-ui/core/styles';
import React from 'react';
import { createOrUpdateItem, fetchItem } from '../../actions/item';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import ItemDisplayComponent from './item-display';
import ItemEditComponent from './item-edit';

const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },
    button: {
        marginRight: theme.spacing.unit,
    },

});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class ItemDetailComponent extends React.Component {

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

    loadItem() {
        let id = this.props.match.params.id
        if (id === 'new') {
            this.setState({
                edit: true,
                item: {
                    broken: false,
                    consumable: false,
                    depth: '',
                    description: '',
                    hasBarcode: false,
                    height: '',
                    identifier: Date.now().toString(36),
                    name: '',
                    outsideQualified: false,
                    pictureUrl: '',
                    quantity: 1,
                    unit: 'Stück',
                    width: '',
                },
            })
        } else {
            fetchItem({ accessToken: this.props.sessionState.accessToken, itemId: id }).then(item => this.setState({ item }))
        }
    }

    saveIfBroken(broken) {
        this.setState(prevState => ({
            savingIfBroken: true,
            item: {
                ...prevState.item,
                broken,
            }
        }), () => createOrUpdateItem({
            accessToken: this.props.sessionState.accessToken,
            item: this.state.item,
        }).then(item => this.setState({
            savingIfBroken: false,
        })))

    }

    componentDidMount() {
        this.loadItem();
    }

    render() {
        const { classes } = this.props
        const { item, savingIfBroken } = this.state
        if (!item) {
            return (<CircularProgress />)
        }
        return (
            <>
                [Detail-Ansicht für {this.props.match.params.id}]
                {this.state.edit ? (
                    <>
                        <ItemEditComponent item={item}></ItemEditComponent>
                        <Button variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(false)}>
                            Speichern
                        </Button>
                    </>
                ) : (
                        <>
                            <ItemDisplayComponent item={item}></ItemDisplayComponent>
                            <Button variant="contained" className={classes.button} onClick={() => this.changeEditState(true)}>
                                Bearbeiten
                        </Button>
                            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Verschieben"')}>
                                Verschieben
                        </Button>
                            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Kopieren"')}>
                                Kopieren
                        </Button>
                            <Button variant="contained" className={classes.button} onClick={() => this.saveIfBroken(!item.broken)}>
                                {savingIfBroken ? (<CircularProgress size="12" />) : item.broken ? 'Repariert' : 'Defekt'}
                            </Button>
                            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Ausleihen"')}>
                                Ausleihen
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
