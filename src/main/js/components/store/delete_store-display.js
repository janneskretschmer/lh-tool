import React from 'react';
import { withContext } from '../../util';
import { withStyles } from '@material-ui/core/styles';
import { SessionContext } from '../../providers/session-provider';
import { Link } from 'react-router-dom';
import { fullPathOfItem } from '../../paths';

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
    shelfWrapper: {
        borderCollapse: 'collapse',
        display: 'inline-block',
        verticalAlign: 'top',
        margin: theme.spacing.unit,
    },
    shelfHeader: {
        border: '1px solid '+theme.palette.primary.main,
        padding: theme.spacing.unit,
    },
    shelfName: {
        fontWeight: 'bold',
    },
    slot: {
        border: '1px solid '+theme.palette.primary.main,
        padding: theme.spacing.unit,
    }
});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class StoreDisplayComponent extends React.Component {

    constructor(props) {
        super(props)
        this.state = {

        }
    }

    render() {
        const {classes} = this.props
        const {shelves} = this.state
        return (
            <div>
                <div className={classes.title}>Kehlheim</div>
                <div className={classes.bold}>
                    Adresse
                </div>
                Keine<br />
                Ahnung<br />
                Wo das ist<br />
                <br />
                <div className={classes.bold}>
                    Typ
                </div>
                Hauptlager<br />
                <br />
                {shelves.map((shelf) => (
                    <table key={shelf.id} className={classes.shelfWrapper}>
                        <thead>
                            <tr>
                                <td className={classes.shelfHeader}>
                                    <div className={classes.shelfName}>
                                        {shelf.name}
                                    </div>
                                    <div>
                                        {shelf.outside ? 'Draußen' : 'Drinnen'}
                                    </div>
                                </td>
                            </tr>
                        </thead>
                        <tbody>
                            {shelf.slots.map((slot) => (
                                <tr key={slot.id}>
                                    <td className={classes.slot}>
                                        <div className={classes.slotName}>
                                            {shelf.name} {slot.name}
                                        </div>
                                        <div className={classes.items}>
                                            {slot.items.map((item) => (
                                                <>
                                                    <Link key={item.id} to={fullPathOfItem(item.id)}>{item.name}</Link><br />
                                                </>
                                            ))}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ))}
            </div>
        )
    }
}
