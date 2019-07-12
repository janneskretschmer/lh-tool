import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import Chip from '@material-ui/core/Chip';

const styles = theme => ({
    bold: {
        fontWeight: 'bold',
    },
    title: {
        fontSize: '30px',
        marginBottom: '10px',
        textAlign: 'center',
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
});

const ItemDisplayComponent = props => {
    const {classes} = props

    return (
        <div>
            <div className={classes.title}>Hammer</div>
            <img className={classes.image} src="https://images-na.ssl-images-amazon.com/images/I/71tTWyypTKL._SX679_.jpg"/>
            <div className={classes.container}>
                <div className={classes.bold}>
                    Eindeutiger Bezeichner (Barcode)
                </div>
                9515301538<br />
                <br />
                <div className={classes.bold}>
                    Lagerplatz
                </div>
                <a href="javascript:alert('Umleitung zu Lager Kehlheim')">Kehlheim</a> <a href="javascript:alert('Umleitung zu Regal A Platz 7')">A7</a><br />
                <br />
                <div className={classes.bold}>
                    Menge+Einheit
                </div>
                1 Stück<br />
                <br />
                <div className={classes.bold}>
                    Maße
                </div>
                Länge: 20cm<br />
                Breite: 7cm<br />
                Höhe: 3cm<br />
                <br />
                <div className={classes.bold}>
                    Verbrauchsgegenstand
                </div>
                Nein<br />
                <br />
                <div className={classes.bold}>
                    Beschreibung
                </div>
                Der Absolute Hammer<br />
                <br />
                <div className={classes.bold}>
                    Tags
                </div>
                <Chip label="Hammer" className={classes.chip} />
                <Chip label="Absolut" className={classes.chip} />
                <Chip label="Schlagen" className={classes.chip} /><br />
                <br />
                <div className={classes.bold}>
                    Gewerk
                </div>
                Zimmerer<br />
                <br />
                <div className={classes.bold}>
                    Protokoll
                </div>
                12.07.19: Projekt Stuttgart zugeteilt von Max Mustermann<br />
                30.05.19: Bearbeitet von Jannes Kretschmer<br />
                27.02.19: Als repariert gemeldet von Mecha Niker<br />
                21.02.19: Als defekt gemeldet von Hel Fer<br />
                15.01.19: Angelegt von Hans Dampf<br />
                <br />
                <div className={classes.bold}>
                    Zugehörig
                </div>
                <a href="javascript:alert('Umleitung zum Nagel')">Nagel</a><br />
                <a href="javascript:alert('Umleitung zum Meißel')">Meißel</a><br />
            </div>
        </div>
    )
}


export default withStyles(styles)(ItemDisplayComponent);
