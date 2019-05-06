import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { SessionContext } from '../providers/session-provider';
import { withContext } from '../util';

//@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class ItemDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
              };
    }

    render () {
      return (
        <>
          Detail-Ansicht
        </>
      )
    }
}
