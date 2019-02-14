import React from 'react';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles';
import deepPurple from '@material-ui/core/colors/deepPurple';
import green from '@material-ui/core/colors/green';
import LHToolRoot from './components/root';

const theme = createMuiTheme({
	palette: {
		primary: deepPurple,
		secondary: green,
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
