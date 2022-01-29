import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { fullPathOfItemData, fullPathOfItems, fullPathOfItemHistory } from '../../paths';
import { requiresLogin } from '../../util';
import NotFoundHandlerComponent from '../notfound';
import ItemsProvider from '../../providers/items-provider';
import ItemDetailComponent from './item-detail';
import ItemHistoryComponent from './item-history';
import ItemListComponent from './item-list';

class ItemWrapperComponent extends React.Component {
    render() {
        return (<>
            <ItemsProvider>
                <Switch>
                    {/* 
                            KEEP IN SYNC WITH pages.js
                            it's necessary for the generation of the title breadcrump
                        */}
                    <Route path={fullPathOfItemData()} component={ItemDetailComponent} />
                    <Route path={fullPathOfItemHistory()} component={ItemHistoryComponent} />
                    <Route path={fullPathOfItems()} component={ItemListComponent} />
                    <Route component={NotFoundHandlerComponent} />
                </Switch>
            </ItemsProvider>
        </>);
    }
}
export default requiresLogin(ItemWrapperComponent);