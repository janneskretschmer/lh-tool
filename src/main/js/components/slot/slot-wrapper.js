import React from 'react';
import { Route, Switch } from 'react-router';
import { fullPathOfSlot, fullPathOfSlots } from '../../paths';
import NeedsProvider from '../../providers/needs-provider';
import { requiresLogin } from '../../util';
import NotFoundHandlerComponent from '../notfound';
import SlotDetailComponent from './slot-detail';
import SlotListComponent from './slot-list';
import SlotsProvider from '../../providers/slots-provider';

class SlotWrapperComponent extends React.Component {
    render() {
        return (<>
            <SlotsProvider>
                <Switch>
                    {/* 
                            KEEP IN SYNC WITH pages.js
                            it's necessary for the generation of the title breadcrump
                        */}
                    <Route path={fullPathOfSlot()} component={SlotDetailComponent} />
                    <Route path={fullPathOfSlots()} component={SlotListComponent} />
                    <Route component={NotFoundHandlerComponent} />
                </Switch>
            </SlotsProvider>
        </>);
    }
}
export default requiresLogin(SlotWrapperComponent);