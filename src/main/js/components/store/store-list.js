import React from 'react';
import { SessionContext } from '../../providers/session-provider';
import { withContext } from '../../util';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableFooter from '@material-ui/core/TableFooter';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import { Link } from 'react-router-dom';
import { fullPathOfStore } from '../../paths';
import LocalShippingIcon from '@material-ui/icons/LocalShipping';
import HomeIcon from '@material-ui/icons/Home';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Divider from '@material-ui/core/Divider';
import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';

const styles = theme => ({
    noDecoration: {
        textDecoration: 'none',
    },
});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class StoreListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {

        };
    }

    redirect(id) {
        this.setState({ redirect: id })
    }

    render() {
        const { } = this.state;
        const { classes } = this.props;
        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <>
                        <List>
                            <Link to={fullPathOfStore(1)} className={classes.noDecoration}>
                                <ListItem button>
                                    <ListItemIcon>
                                        <HomeIcon />
                                    </ListItemIcon>
                                    <ListItemText primary="Kehlheim" secondary="Giselastr. 39 93309 Kelheim" />
                                </ListItem>
                            </Link>
                            <Divider />
                            <Link to={fullPathOfStore(2)} className={classes.noDecoration}>
                                <ListItem button>
                                    <ListItemIcon>
                                        <LocalShippingIcon />
                                    </ListItemIcon>
                                    <ListItemText primary="Magazin 1" />
                                </ListItem>
                            </Link>
                            <Divider />
                            <Link to={fullPathOfStore(3)} className={classes.noDecoration}>
                                <ListItem button>
                                    <ListItemIcon>
                                        <LocalShippingIcon />
                                    </ListItemIcon>
                                    <ListItemText primary="Magazin 2" />
                                </ListItem>
                            </Link>
                            <Divider />
                            <Link to={fullPathOfStore(4)} className={classes.noDecoration}>
                                <ListItem button>
                                    <ListItemIcon>
                                        <HomeIcon />
                                    </ListItemIcon>
                                    <ListItemText primary="Stuttgart" />
                                </ListItem>
                            </Link>
                            <Divider />
                            <Link to={fullPathOfStore(5)} className={classes.noDecoration}>
                                <ListItem button>
                                    <ListItemIcon>
                                        <HomeIcon />
                                    </ListItemIcon>
                                    <ListItemText primary="Vöhringen" secondary="Johannisweg 10 89269 Vöhringen" />
                                </ListItem>
                            </Link>
                        </List>
                        <Button variant="contained" onClick={() => alert('TODO: Dialog zum eingeben und Speichern der Daten öffnen')} className={classes.new}>
                            Hinzufügen
                        </Button>
                    </>
                )}
            </SessionContext.Consumer>
        )
        /*
        {sessionState => (
                    <>
                        <List>
                            {rows.map(row => (
                                <Link to={fullPathOfStore(row.id)} className={classes.noDecoration}>
                                    <ListItem button>
                                        <ListItemIcon>
                                            <InboxIcon />
                                        </ListItemIcon>
                                        <ListItemText primary={row.name} />
                                        </ListItem>
                                </Link>
                            ))}
                        </List>
                    </>
                )}
                */
    }
}
