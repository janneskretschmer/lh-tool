import MenuIcon from '@mui/icons-material/Menu';
import { Link, Tab, Tabs } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import IconButton from '@mui/material/IconButton';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import React from 'react';
import { PageContext } from '../providers/page-provider';
import LenientRedirect from './util/lenient-redirect';

const drawerWidth = 220;

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
        return <>
            <AppBar
                position="fixed"
                sx={{
                    transition: theme => theme.transitions.create(['margin', 'width'], {
                        easing: theme.transitions.easing.sharp,
                        duration: theme.transitions.duration.leavingScreen,
                    }),
                    width: drawerOpen ? 'calc(100% - 220px)' : '100%',
                }}
            >
                <div ref={this.headerRef}>
                    <Toolbar disableGutters={!drawerOpen}>
                        <IconButton
                            color="inherit"
                            aria-label="Open drawer"
                            onClick={() => this.props.onOpenRequest && this.props.onOpenRequest()}
                            sx={{
                                marginLeft: '12px',
                                marginRight: '20px',
                                display: drawerOpen ? 'none' : 'inline-block',
                            }}
                            size="large">
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
                                            <Link href={component.path} sx={{
                                                color: 'primary.contrastText',
                                                textDecoration: 'none',
                                                cursor: 'pointer',
                                            }}>{component.title}</Link>
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
                                    textColor="inherit"
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
        </>;
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
