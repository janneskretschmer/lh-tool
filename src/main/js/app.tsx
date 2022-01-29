import { createTheme, StyledEngineProvider, ThemeProvider } from '@mui/material/styles';
import { SnackbarProvider } from 'notistack';
import React from 'react';
import LHToolRootWrapper from './components/root';
import settings from './settings';
import { EmptyPropsComponent } from './types/empty-props-component';

const theme = createTheme({
	palette: {
		//needs to be changed in deploy.sh as well
		primary: settings.theme.primary,
		secondary: settings.theme.secondary,
	},
});

const ThemedRoot: EmptyPropsComponent = ({ }) => (
	<StyledEngineProvider injectFirst>
		<ThemeProvider theme={theme}>
			<LHToolRootWrapper />
		</ThemeProvider>
	</StyledEngineProvider>
);

const SnackbaredRoot: EmptyPropsComponent = ({ }) => (
	<SnackbarProvider maxSnack={3}>
		<ThemedRoot />
	</SnackbarProvider>
);

const LHToolApp = SnackbaredRoot;
export default LHToolApp;
