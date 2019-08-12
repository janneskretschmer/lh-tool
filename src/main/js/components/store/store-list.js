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
import { fetchOwnStores } from '../../actions/store';
import CircularProgress from '@material-ui/core/CircularProgress';

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
            stores: null
        };
    }

    redirect(id) {
        this.setState({ redirect: id })
    }

    componentDidMount() {
        fetchOwnStores(this.props.sessionState.accessToken).then(stores => this.setState({
            stores
        }));
    }

    render() {
        const { stores } = this.state;
        const { classes } = this.props;
        return (
            <SessionContext.Consumer>
                {sessionState => (
                    <>
                        <List>
                            {stores ? stores.map(store => (
                                <>
                                    <Link to={fullPathOfStore(store.id)} className={classes.noDecoration} key={store.id}>
                                        <ListItem button>
                                            <ListItemIcon>
                                                {store.type === 'MOBIL' ? (<LocalShippingIcon />) : (<HomeIcon />)}
                                            </ListItemIcon>
                                            <ListItemText primary={store.name} secondary={store.address} />
                                        </ListItem>
                                    </Link>
                                    <Divider />
                                </>
                            )) : (<CircularProgress />)}
                        </List>
                        <Link to={fullPathOfStore('new')} className={classes.noDecoration}>
                            <Button variant="contained" onClick={() => alert('TODO: Dialog zum eingeben und Speichern der Daten öffnen')} className={classes.new}>
                                Hinzufügen
                            </Button>
                        </Link>
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
