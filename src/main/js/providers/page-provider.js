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
        while (matchedPage) {
            // workaround for detail pages (e.g. /users/:id) with dynamic title
            if (!matchedPage.title && !matchedPage.subPages && currentItemName) {
                matchedPage.path = path;
                matchedPage.title = currentItemName;
            }
            pageTrace = [...pageTrace, matchedPage];
            currentPage = matchedPage;
            matchedPage = matchedPage.subPages && matchedPage.subPages.find(page => matchPath(path, {
                path: page.path,
                exact: false,
                strict: false,
            }));
        }

        return { currentPage, pageTrace };
    }

    isUserAllowedToSeeCurrentPage() {
        const page = this.state.currentPage;
        return !page || !page.permissions || !page.permissions.find(permission => !this.props.sessionsState.hasPermission(permission));
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