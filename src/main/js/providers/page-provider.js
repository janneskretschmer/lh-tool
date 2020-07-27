import React from 'react';
import { Link } from '@material-ui/core';
import { withRouter, matchPath } from 'react-router-dom';
import PAGES from '../pages';
import { SessionContext } from './session-provider';
import NotFoundHandlerComponent from '../components/notfound';

export const PageContext = React.createContext();

class StatefulPageProvider extends React.Component {

    state = {
        currentPath: null,
        currentPage: null,
        currentTitleComponents: null,
        currentItemName: null,
        tabParent: null,
    };

    componentDidMount() {
        this.handlePageChanged();
    }

    componentDidUpdate() {
        // if path of currentPage is used and an unknown path gets called, the page will crash
        if (this.state.currentPath !== this.props.location.pathname) {
            this.handlePageChanged();
        }
    }

    handlePageChanged() {
        const trace = this.traceCurrentPage();
        this.setState({
            currentPath: this.props.location.pathname,
            currentPage: trace.currentPage,
            currentTitleComponents: trace.pageTrace,
            currentItemName: null,
            tabParent: trace.tabParent,
        });
    }

    setCurrentItemName({ name }) {
        const trace = this.traceCurrentPage(name);
        this.setState({
            currentItemName: name,
            currentTitleComponents: trace.pageTrace,
        });
    }

    traceCurrentPage(currentItemName) {
        const path = this.props.location.pathname;
        let matchedPage = PAGES;
        let currentPage = null;
        let pageTrace = [];
        let tabParent = null;
        while (matchedPage) {
            // workaround for detail pages (e.g. /users/:id) with dynamic title
            if (!matchedPage.title && !matchedPage.subPages && currentItemName) {
                matchedPage = {
                    ...matchedPage,
                    path,
                    title: currentItemName,
                };
            }
            pageTrace = [...pageTrace, matchedPage];
            currentPage = matchedPage;
            if (matchedPage.tabs) {
                tabParent = matchedPage;
            }
            matchedPage = matchedPage.subPages && matchedPage.subPages.find(page => matchPath(path, {
                path: page.path,
                exact: false,
                strict: false,
            }));
        }

        return { currentPage, pageTrace, tabParent };
    }

    isUserAllowedToSeeCurrentPage() {
        const page = this.state.currentPage;
        return this.isUserAllowedToSeePage(page);
    }

    isUserAllowedToSeePage(page) {
        return !page || !page.permissions || page.permissions.every(permission => this.props.sessionsState.hasPermission(permission));
    }

    getTabValue() {
        if (this.state.tabParent) {
            const tabPage = this.state.tabParent.subPages.find(subPage => matchPath(this.state.currentPath, {
                path: subPage.path,
                exact: false,
                strict: false,
            }));
            return tabPage && tabPage.path;
        }
        return null;
    }

    render() {
        if (!this.isUserAllowedToSeeCurrentPage()) {
            return (<NotFoundHandlerComponent />);
        }
        return (
            <PageContext.Provider
                value={{
                    ...this.state,
                    setCurrentItemName: this.setCurrentItemName.bind(this),
                    isUserAllowedToSeePage: this.isUserAllowedToSeePage.bind(this),
                    getTabValue: this.getTabValue.bind(this),
                }}
            >
                {this.props.children}
            </PageContext.Provider>
        );
    }
}

const PageProvider = props => (
    <SessionContext.Consumer>
        {sessionsState => (
            <StatefulPageProvider {...props} sessionsState={sessionsState} />
        )}
    </SessionContext.Consumer>
);
export default withRouter(PageProvider);