import React from 'react';
import { StaticRouter } from 'react-router-dom';
import ReactDOMServer from 'react-dom/server';
import { Resolver } from "react-resolver";
import { Helmet } from 'react-helmet';
import LHToolApp from './app';

global.window = global;

function main(path, callback) {
  const context = {};
  Resolver
    .resolve(() => (
      <StaticRouter location={path} context={context}>
        <LHToolApp />
      </StaticRouter>
    ))
    .then(({ Resolved, data }) => {
      const app = ReactDOMServer.renderToString(<Resolved />);
      const helmet = Helmet.renderStatic();
      callback(app, helmet.title.toString(), JSON.stringify(data));
    });

  // TODO
  //    .catch((error) => res.status(500).send(error)) // Just in case!
}

main(__GLOBAL_CONFIG__.fullPath, renderDoneCallback);
