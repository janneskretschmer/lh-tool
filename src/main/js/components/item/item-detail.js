import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import ItemDisplayComponent from './item-display';
import ItemEditComponent from './item-edit';
import Button from '@material-ui/core/Button';

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
                    name: '',
                    id: '',
                    
                    type: 'STANDARD'
                },
            })
        } else {
            fetchStore({ accessToken: this.props.sessionState.accessToken, storeId: id }).then(store => this.changeStore(store))
            fetchStoreProjects({ accessToken: this.props.sessionState.accessToken, storeId: id }).then(storeProjects => this.setState({ storeProjects }))
        }
    }

    render() {
        const { classes } = this.props
        return (
            <>
                [Detail-Ansicht für {this.props.match.params.id}]
                {this.state.edit ? (
                    <>
                        <ItemEditComponent></ItemEditComponent>
                        <Button variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(false)}>
                            Speichern
                        </Button>
                    </>
                ) : (
                        <>
                            <ItemDisplayComponent></ItemDisplayComponent>
                            <Button variant="contained" className={classes.button} onClick={() => this.changeEditState(true)}>
                                Bearbeiten
                        </Button>
                            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Verschieben"')}>
                                Verschieben
                        </Button>
                            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Kopieren"')}>
                                Kopieren
                        </Button>
                            <Button variant="contained" className={classes.button} onClick={() => alert('TODO: implement "Zerstörung"')}>
                                Defekt
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
