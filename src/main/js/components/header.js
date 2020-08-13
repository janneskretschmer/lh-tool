import { Tab, Tabs } from '@material-ui/core';
import AppBar from '@material-ui/core/AppBar';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import MenuIcon from '@material-ui/icons/Menu';
import classNames from 'classnames';
import React from 'react';
import { Link } from 'react-router-dom';
import { PageContext } from '../providers/page-provider';
import LenientRedirect from './util/lenient-redirect';

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
            // empiric start value
            height: 64,
            redirectToUrl: null,
        };

        this.headerRef = React.createRef();
    }

    componentDidUpdate() {
        if (this.state.path !== this.props.pagesState.currentPath) {
            this.setState({
                path: this.props.pagesState.currentPath,
                redirectToUrl: null,
            }, () => this.handleTitleChanged());
        }
    }

    handleTitleChanged() {
        this.setState({
            height: this.headerRef.current.getBoundingClientRect().height,
        });
    }

    redirect(redirectToUrl) {
        this.setState({ redirectToUrl });
    }

    render() {
        const { classes, drawerOpen, pagesState, match } = this.props;
        const { height, redirectToUrl } = this.state;
        const tabValue = pagesState.tabParent && pagesState.getTabValue();
        return (
            <>
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
                        {pagesState.tabParent && (
                            <>
                                {tabValue ? (
                                    <Tabs
                                        value={tabValue}
                                        onChange={(event, value) => this.redirect(value)}
                                    >
                                        {pagesState.tabParent.subPages
                                            .filter(subPage => pagesState.isUserAllowedToSeePage(subPage))
                                            .map(subPage => (
                                                <Tab
                                                    key={subPage.path}
                                                    value={subPage.path}
                                                    label={subPage.title}
                                                />
                                            ))}
                                    </Tabs>
                                ) : (
                                        <LenientRedirect to={pagesState.tabParent.subPages[0].path} />
                                    )}
                            </>
                        )}
                    </div>
                </AppBar>

                {/* Top spacer */}
                <div style={{ height: `${height}px` }}>
                </div>

                {
                    //keep header in case of redirect, because hight is probably the same
                    redirectToUrl && (<LenientRedirect to={redirectToUrl} />)
                }
            </>
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
