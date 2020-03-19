import React from 'react';
import { Route, Switch } from 'react-router';
import NeedsProvider from '../../providers/needs-provider';
import ProjectsProvider from '../../providers/projects-provider';
import { requiresLogin } from '../../util';
import NeedApplyComponent from './apply';
import NeedApproveComponent from './approve';
import NeedQuantityComponent from './quantities';
import { fullPathOfNeedApprove, fullPathOfNeedApply, fullPathOfNeedQuantities } from '../../paths';
import NotFoundComponent from '../notfound';

class NeedWrapperComponent extends React.Component {
    render() {
        return (<>
            <ProjectsProvider>
                <NeedsProvider>
                    <Switch>
                        <Route path={fullPathOfNeedQuantities()} component={NeedQuantityComponent} />
                        <Route path={fullPathOfNeedApply()} component={NeedApplyComponent} />
                        <Route path={fullPathOfNeedApprove()} component={NeedApproveComponent} />
                        <Route component={NotFoundComponent} />
                    </Switch>
                </NeedsProvider>
            </ProjectsProvider>
        </>);
    }
}
export default requiresLogin(NeedWrapperComponent);