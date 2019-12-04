import '@babel/polyfill';
import React from 'react';
import { SnackbarProvider } from 'notistack';
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

const SnackbaredRoot = () => (
	<SnackbarProvider maxSnack={3}>
		<ThemedRoot />
	</SnackbarProvider>
);

const LHToolApp = SnackbaredRoot;
export default LHToolApp;
