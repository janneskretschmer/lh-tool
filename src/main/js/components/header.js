import React from 'react';
import classNames from 'classnames';
import { withStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import { Helmet } from 'react-helmet';

const drawerWidth = 240;
const styles = theme => ({
    appBar: {
        transition: theme.transitions.create(['margin', 'width'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
    },
    appBarShift: {
        width: `calc(100% - ${drawerWidth}px)`,
        marginLeft: drawerWidth,
        transition: theme.transitions.create(['margin', 'width'], {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    menuButton: {
        marginLeft: 12,
        marginRight: 20,
    },
    hide: {
        display: 'none',
    },
});

@withStyles(styles)
export default class AppHeader extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            title: props.defaultTitle,
        };
    }

    render() {
        const { classes, drawerOpen } = this.props;
        const { title } = this.state;

        return (
            <AppBar
                position="fixed"
                className={classNames(classes.appBar, {
                    [classes.appBarShift]: drawerOpen,
                })}
            >
                <Helmet onChangeClientState={newState => this.setState({ title: newState.title })} />
                <Toolbar disableGutters={!drawerOpen}>
                    <IconButton
                        color="inherit"
                        aria-label="Open drawer"
                        onClick={() => this.props.onOpenRequest && this.props.onOpenRequest()}
                        className={classNames(classes.menuButton, drawerOpen && classes.hide)}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography
                        component="h1"
                        variant="h6"
                        color="inherit"
                        noWrap
                    >
                        {title}
                    </Typography>
                </Toolbar>
            </AppBar>
        );
    }
}

