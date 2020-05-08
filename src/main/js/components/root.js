import React from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import classNames from 'classnames';
import { Helmet } from 'react-helmet';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { fullPathOfChangePw, fullPathOfDataProtection, fullPathOfImprint, fullPathOfItem, fullPathOfItems, fullPathOfLogin, fullPathOfNeedApply, fullPathOfNeedApprove, fullPathOfNeedQuantities, fullPathOfProjects, fullPathOfSlot, fullPathOfStore, fullPathOfStores, partialPathOfNeed, fullPathOfSettings } from '../paths';
import SessionProvider from '../providers/session-provider';
import ChangePasswordComponent from './changepw';
import AppHeader from './header';
import LoginComponent from './login';
import AppMenu from './menu';
import NeedWrapperComponent from './need/need-wrapper';
import ItemListComponent from './item/item-list';
import ItemDetailComponent from './item/item-detail';
import NotFoundComponent from './notfound';
import ProjectsComponent from './project/project';
import SlotDetailComponent from './slot/slot-detail';
import StoreDetailComponent from './store/store-detail';
import StoreListComponent from './store/store-list';
import DataProtection from './util/data-protection';
import Imprint from './util/imprint';
import SettingsComponent from './settings';

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
            drawerOpen: window.innerWidth > 960,
            contentMarginTop: 0,
        };
    }

    handleDrawerOpen() {
        this.setState({ drawerOpen: true });
    }

    handleDrawerClose() {
        this.setState({ drawerOpen: false });
    }

    setContentTopMargin(margin) {
        this.setState({ contentMarginTop: margin });
    }

    render() {
        const { classes, theme } = this.props;
        const { drawerOpen, contentMarginTop } = this.state;
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
                                setContentTopMargin={margin => this.setContentTopMargin(margin)}
                                drawerOpen={drawerOpen}
                                //defaultTitle={TITLE}
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
                                style={{ marginTop: `${contentMarginTop}px` }}
                            >
                                <Switch>
                                    <Route path={fullPathOfLogin()} component={LoginComponent} />
                                    <Route path={fullPathOfProjects()} component={ProjectsComponent} />
                                    <Route path={partialPathOfNeed()} component={NeedWrapperComponent} exact={false} />
                                    <Route path={fullPathOfStore()} component={StoreDetailComponent} />
                                    <Route path={fullPathOfStores()} component={StoreListComponent} />
                                    <Route path={fullPathOfSlot()} component={SlotDetailComponent} />
                                    <Route path={fullPathOfItem()} component={ItemDetailComponent} />
                                    <Route path={fullPathOfItems()} component={ItemListComponent} />
                                    <Route path={fullPathOfSettings()} component={SettingsComponent} exact={false} />
                                    <Route path={fullPathOfChangePw()} component={ChangePasswordComponent} />
                                    <Route path={fullPathOfImprint()} component={Imprint} />
                                    <Route path={fullPathOfDataProtection()} component={DataProtection} />
                                    <Route component={NotFoundComponent} />
                                </Switch>
                            </main>

                        </SessionProvider>
                    </>
                </Router>
            </div>
        );
    }
}
