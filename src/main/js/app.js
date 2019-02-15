import React from 'react';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles';
import deepPurple from '@material-ui/core/colors/deepPurple';
import blueGrey from '@material-ui/core/colors/blueGrey';
import LHToolRoot from './components/root';

const theme = createMuiTheme({
	palette: {
		primary: deepPurple,
		secondary: blueGrey,
	},
	status: {
		danger: 'orange',
	},
});

const ThemedRoot = () => (
    <MuiThemeProvider theme={theme}>
        <LHToolRoot />
    </MuiThemeProvider>
);

const LHToolApp = ThemedRoot;
export default LHToolApp;
