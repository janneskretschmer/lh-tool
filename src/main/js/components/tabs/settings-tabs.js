import { withStyles } from '@material-ui/core/styles';
import React from 'react';
import { Tabs, Tab } from '@material-ui/core';
import { fullPathOfUsersSettings } from '../../paths';

const styles = theme => ({
    tabs: {
        display: 'block',
    },
    break: {
        flexBasis: '100%',
        height: '0',
    },
});

// TODO generate dynamically from subpages
@withStyles(styles)
export default class SettingsTabsComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    getValueByPath() {
        const path = this.props.location.pathname;
        if (path.startsWith(fullPathOfUsersSettings())) {
            return 'users';
        }
    }

    render() {
        const { classes } = this.props;
        const value = this.getValueByPath();
        return (
            <>
                <div className={classes.break} />
                <Tabs value={value} className={classes.tabs}>
                    <Tab value={'users'} label="Benutzer" />
                    {/* <Tab value={'projects'} label="Projekte" /> */}
                </Tabs>
            </>
        );
    }
}