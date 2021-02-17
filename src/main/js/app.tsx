import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';
import { SnackbarProvider } from 'notistack';
import * as React from 'react';
import LHToolRootWrapper from './components/root';
import * as settings from './settings';
import { EmptyPropsComponent } from './types/empty-props-component';

const theme = createMuiTheme({
	palette: {
		//needs to be changed in deploy.sh as well
		primary: settings.theme.primary,
		secondary: settings.theme.secondary,
	},
	typography: {
		useNextVariants: true,
	},
});

const ThemedRoot: EmptyPropsComponent = ({ }) => (
	<MuiThemeProvider theme={theme}>
		<LHToolRootWrapper />
	</MuiThemeProvider>
);

const SnackbaredRoot: EmptyPropsComponent = ({ }) => (
	<SnackbarProvider maxSnack={3}>
		<ThemedRoot />
	</SnackbarProvider>
);

const LHToolApp = SnackbaredRoot;
export default LHToolApp;
