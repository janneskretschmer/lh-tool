import React from 'react';
import { Route, Switch } from 'react-router';
import NeedsProvider from '../../providers/needs-provider';
import { requiresLogin } from '../../util';
import NeedApplyComponent from './apply';
import NeedApproveComponent from './approve';
import NeedQuantityComponent from './quantities';
import { fullPathOfNeedApprove, fullPathOfNeedApply, fullPathOfNeedQuantities } from '../../paths';
import NotFoundHandlerComponent from '../notfound';
import ProjectsProvider from '../../providers/projects-provider';

class NeedWrapperComponent extends React.Component {
    render() {
        return (<>
            <NeedsProvider>
                <Switch>
                    {/* 
                            KEEP IN SYNC WITH pages.js
                            it's necessary for the generation of the title breadcrump
                        */}
                    <Route path={fullPathOfNeedQuantities()} component={NeedQuantityComponent} />
                    <Route path={fullPathOfNeedApply()} component={NeedApplyComponent} />
                    <Route path={fullPathOfNeedApprove()} component={NeedApproveComponent} />
                    <Route component={NotFoundHandlerComponent} />
                </Switch>
            </NeedsProvider>
        </>);
    }
}
export default requiresLogin(NeedWrapperComponent);