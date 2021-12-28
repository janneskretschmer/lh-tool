import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import CssBaseline from '@mui/material/CssBaseline';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import { Box } from '@mui/system';
import React from 'react';
import PageProvider from '../providers/page-provider';
import SessionProvider from '../providers/session-provider';
import AppHeader from './header';
import AppMenu from './menu';
import MainRouter from './main-router';
import { BrowserRouter as Router } from 'react-router-dom';

class LHToolRoot extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            drawerOpen: window.innerWidth > 960,
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
        const { classes } = this.props;
        const { drawerOpen } = this.state;
        const TITLE = 'LH-Tool';

        return (
            <Box sx={{
                display: 'flex',
            }}>
                <Router>
                    <>
                        <CssBaseline />
                        <SessionProvider>
                            <PageProvider>
                                <Box
                                    sx={{
                                        width: '220px',
                                        flexShrink: 0,
                                        display: drawerOpen ? 'block' : 'none',
                                        position: 'fixed',
                                        borderRight: '1px solid rgba(0, 0, 0, 0.12)',
                                    }}
                                >
                                    <Box sx={{
                                        width: '220px',
                                        height: '64px',
                                        display: 'flex',
                                        alignContent: 'center',
                                    }}>
                                        <IconButton onClick={this.handleDrawerClose.bind(this)} size="large">
                                            <ChevronLeftIcon />
                                        </IconButton>
                                    </Box>
                                    <Divider />
                                    <AppMenu />
                                </Box>
                                <Box
                                    component="main"
                                    sx={{
                                        flexGrow: 1,
                                        p: 3,
                                        marginLeft: drawerOpen ? '220px' : '0',
                                        overflow: 'auto',
                                    }}
                                >
                                    <AppHeader
                                        drawerOpen={drawerOpen}
                                        onOpenRequest={this.handleDrawerOpen.bind(this)} />
                                    <MainRouter />
                                </Box>
                            </PageProvider>
                        </SessionProvider>
                    </>
                </Router>
            </Box>
        );
    }
}


// typescript doesn't like class components written in javascript
const LHToolRootWrapper = props => (<LHToolRoot />);
export default LHToolRootWrapper;
