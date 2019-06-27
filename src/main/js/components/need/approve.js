import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import { withSnackbar } from 'notistack';
import { requiresLogin } from '../../util';

const styles = theme => ({
});

@withStyles(styles)
@withSnackbar
class NeedApproveComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            project: null,
            months: null,
            month: 0,
            needData: null,
            day: null,
        };
    }

    render() {
        const { classes, sessionState } = this.props;

        return (
            <>
                Approve
            </>
        )
    }
}

export default requiresLogin(NeedApproveComponent);
