import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom';
import { fullPathOfChangePw, fullPathOfDataProtection, fullPathOfImprint, fullPathOfItems, fullPathOfLogin, fullPathOfSettings, fullPathOfSlots, partialPathOfNeed } from '../paths';
import ChangePasswordComponent from './changepw';
import ItemWrapperComponent from './item/item-wrapper';
import LoginComponent from './login';
import NeedWrapperComponent from './need/need-wrapper';
import NotFoundHandlerComponent from './notfound';
import SettingsComponent from './settings';
import SlotWrapperComponent from './slot/slot-wrapper';
import DataProtection from './util/data-protection';
import Imprint from './util/imprint';

class MainRouter extends Component {

    render() {
        return (
            <Switch>
                {/* 
            KEEP IN SYNC WITH pages.js
            it's necessary for the generation of the title breadcrump
            FUTURE: could probably be generated dynamically
        */}
                <Route path={fullPathOfLogin()} component={LoginComponent} />
                <Route path={partialPathOfNeed()} component={NeedWrapperComponent} exact={false} />
                <Route path={fullPathOfSlots()} component={SlotWrapperComponent} exact={false} />
                <Route path={fullPathOfItems()} component={ItemWrapperComponent} exact={false} />
                <Route path={fullPathOfSettings()} component={SettingsComponent} exact={false} />
                <Route path={fullPathOfChangePw()} component={ChangePasswordComponent} />
                <Route path={fullPathOfImprint()} component={Imprint} />
                <Route path={fullPathOfDataProtection()} component={DataProtection} />
                <Route component={NotFoundHandlerComponent} />
            </Switch>
        );
    }
}
export default MainRouter;