import '@babel/polyfill';
import React from 'react';
import { SnackbarProvider } from 'notistack';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles';
import blueGrey from '@material-ui/core/colors/blueGrey';
import LHToolRoot from './components/root';
import settings from './settings';

const theme = createMuiTheme({
	palette: {
		//needs to be changed in deploy.sh as well
		primary: settings.theme.primary,
		secondary: blueGrey,
	},
	status: {
		danger: 'orange',
	},
	typography: {
		useNextVariants: true,
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
