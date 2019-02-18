import React from 'react';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import classNames from 'classnames';
import { withStyles } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import Drawer from '@material-ui/core/Drawer';
import IconButton from '@material-ui/core/IconButton';
import Divider from '@material-ui/core/Divider';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import { Helmet } from 'react-helmet';
import { fullPathOfHome, fullPathOfLogin, fullPathOfHeartbeat, fullPathOfProjects } from '../paths';
import AppHeader from './header';
import HomeComponent from './home';
import LoginComponent from './login';
import HeartbeatComponent from './heartbeat';
import ProjectsComponent from './project';
import AppMenu from './menu';
import SessionProvider from '../providers/session-provider';

const drawerWidth = 240;

const styles = theme => ({
    root: {
        display: 'flex',
    },
    drawer: {
        width: drawerWidth,
        flexShrink: 0,
    },
    drawerPaper: {
        width: drawerWidth,
    },
    drawerHeader: {
        display: 'flex',
        alignItems: 'center',
        padding: '0 8px',
        ...theme.mixins.toolbar,
        justifyContent: 'flex-end',
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing.unit * 3,
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
        marginLeft: -drawerWidth,
    },
    contentShift: {
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
        }),
        marginLeft: 0,
    },
});

@withStyles(styles, { withTheme: true })
export default class LHToolRoot extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            drawerOpen: false,
        };
    }

    handleDrawerOpen() {
        this.setState({ drawerOpen: true });
    }

    handleDrawerClose() {
        this.setState({ drawerOpen: false });
    }

    render() {
        const { classes, theme } = this.props;
        const { drawerOpen } = this.state;
        const TITLE = 'LH-Tool';

        return (
            <div className={classes.root}>
                <Router>
                    <>
                        <CssBaseline />
                        <Helmet>
                            <title>{TITLE}</title>
                        </Helmet>
                        <SessionProvider>
                            <AppHeader
                                drawerOpen={drawerOpen}
                                defaultTitle={TITLE}
                                onOpenRequest={this.handleDrawerOpen.bind(this)} />

                            <Drawer
                                className={classes.drawer}
                                variant="persistent"
                                anchor="left"
                                open={drawerOpen}
                                classes={{
                                    paper: classes.drawerPaper,
                                }}
                            >
                                <div className={classes.drawerHeader}>
                                    <IconButton onClick={this.handleDrawerClose.bind(this)}>
                                        {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
                                    </IconButton>
                                </div>
                                <Divider />
                                <AppMenu />
                            </Drawer>

                            <main
                                className={classNames(classes.content, {
                                    [classes.contentShift]: drawerOpen,
                                })}
                            >
                                <div className={classes.drawerHeader} />
                                <Route path={fullPathOfHome()} exact component={HomeComponent} />
                                <Route path={fullPathOfLogin()} component={LoginComponent} />
                                <Route path={fullPathOfHeartbeat()} component={HeartbeatComponent} />
                                <Route path={fullPathOfProjects()} component={ProjectsComponent} />
                            </main>

                        </SessionProvider>
                    </>
                </Router>
            </div>
        );
    }
}