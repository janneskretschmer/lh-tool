import AppBar from '@material-ui/core/AppBar';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import MenuIcon from '@material-ui/icons/Menu';
import classNames from 'classnames';
import React from 'react';
import { Route, Switch, Link } from 'react-router-dom';
import { fullPathOfSettings } from '../paths';
import { PageContext } from '../providers/page-provider';
import SettingsTabsComponent from './tabs/settings-tabs';

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
    link: {
        color: theme.palette.primary.contrastText,
        textDecoration: 'none',
        cursor: 'pointer',
    },
});

@withStyles(styles)
class StatefulAppHeader extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            path: props.pagesState.currentPath,
        };

        this.headerRef = React.createRef();
    }

    componentDidUpdate() {
        if (this.state.path !== this.props.pagesState.currentPath) {
            this.setState({ path: this.props.pagesState.currentPath }, () => this.handleTitleChanged());
        }
    }

    handleTitleChanged() {
        this.props.setContentTopMargin(this.headerRef.current.getBoundingClientRect().height);
    }

    render() {
        const { classes, drawerOpen, pagesState } = this.props;

        return (
            <AppBar
                position="fixed"
                className={classNames(classes.appBar, {
                    [classes.appBarShift]: drawerOpen,
                })}
            >
                <div ref={this.headerRef}>
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
                            {
                                //build breadcrump
                                pagesState.currentTitleComponents ? pagesState.currentTitleComponents
                                    .filter(component => component.title)
                                    .map((component, i) => (
                                        <span key={component.path}>
                                            {i !== 0 ? ' › ' : null}
                                            <Link to={component.path} className={classes.link}>{component.title}</Link>
                                        </span>
                                    )) : null
                            }
                        </Typography>
                    </Toolbar>
                    {/* TODO: dynamic tabs from subPages */}
                    <Switch>
                        <Route path={fullPathOfSettings()} component={SettingsTabsComponent} exact={false} />
                    </Switch>
                </div>
            </AppBar>
        );
    }
}

const AppHeader = props => (
    <>
        <PageContext.Consumer>
            {pagesState => (
                (<StatefulAppHeader {...props} pagesState={pagesState} />)
            )}
        </PageContext.Consumer>
    </>
);
export default AppHeader;
