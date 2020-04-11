import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';
import LHToolApp from './app';

ReactDOM.hydrate(
	<BrowserRouter>
		<LHToolApp />
	</BrowserRouter>,
	document.getElementById('main-app-container')
);
