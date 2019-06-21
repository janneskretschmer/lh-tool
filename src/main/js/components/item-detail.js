import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { SessionContext } from '../providers/session-provider';
import { withContext } from '../util';
import ItemDisplayComponent from './item-display';
import ItemEditComponent from './item-item-edit';
import Button from '@material-ui/core/Button';

//@withStyles(styles)
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

    render () {
      return (
        <>
            [Detail-Ansicht für {this.props.match.params.id}]
            {this.state.edit ? (
                <>
                    <ItemEditComponent></ItemEditComponent>
                    <Button variant="contained" type="submit" onClick={() => this.changeEditState(false)}>
                            Speichern
                    </Button>
                </>
            ):(
                <>
                    <ItemDisplayComponent></ItemDisplayComponent>
                    <Button variant="contained" onClick={() => this.changeEditState(true)}>
                        Bearbeiten
                    </Button>
                </>
            )}
            <span>Hammer</span>
            Eindeutiger Bezeichner
            (Barcode)
            Menge+Einheit
            Maße
            Verbrauchsgegenstand
            Beschreibung
            Tags
            Gewerke
            Historie
        </>
      )
    }
}
