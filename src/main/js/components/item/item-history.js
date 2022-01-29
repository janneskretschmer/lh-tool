import { CircularProgress, Table, TableBody, TableCell, TableHead, TableRow } from '@mui/material';
import React from 'react';
import { ItemsContext } from '../../providers/items-provider';
import { PageContext } from '../../providers/page-provider';
import { SessionContext } from '../../providers/session-provider';
import { convertToDDMMYYYY_HHMM } from '../../util';

class StatefulItemHistoryComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentDidMount() {
        const id = this.props.match.params.id;
        this.props.itemsState.selectItemHistory(id);
    }

    componentDidUpdate() {
        if (this.props.itemsState.selectedItem && this.props.pageState.currentItemName !== this.props.itemsState.selectedItem.name) {
            this.props.pageState.setCurrentItemName(this.props.itemsState.selectedItem);
        }
    }

    getHistoryActionText(event) {
        switch (event.type) {
            case 'CREATED':
                return 'Angelegt';
            case 'UPDATED':
                return 'Geändert';
            case 'QUANTITY_CHANGED':
                return `Menge von ${event.data.from} auf ${event.data.to} geändert`;
            case 'MOVED':
                return `Von ${event.data.from} nach ${event.data.to} verschoben`;
            case 'BROKEN':
                return 'Defekt gemeldet';
            case 'FIXED':
                return 'Reparatur gemeldet';
        }
        return '';
    }

    getFirstAndLastName({ userId, user }) {
        if (!userId) {
            return '-';
        }
        if (!user) {
            return (<CircularProgress size={12} />);
        }
        return user.firstName + ' ' + user.lastName;
    }

    render() {
        const { itemsState } = this.props;
        const item = itemsState.getSelectedItem();
        if (!item) {
            return (<CircularProgress />);
        }
        if (!item.id) {
            return (<>Für diesen Artikel ist noch kein Protokoll verfügbar.</>);
        }
        return (
            <>
                <Table size="small" sx={{ width: 'initial' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell>Datum</TableCell>
                            <TableCell>Benutzer</TableCell>
                            <TableCell>Aktion</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {item.history ? item.history.map(event => (
                            <TableRow key={event.id}>
                                <TableCell>{convertToDDMMYYYY_HHMM(event.timestamp)}</TableCell>
                                <TableCell>
                                    {this.getFirstAndLastName(event)}
                                </TableCell>
                                <TableCell>{this.getHistoryActionText(event)}</TableCell>
                            </TableRow>
                        )) : (
                            <TableRow>
                                <TableCell colSpan={3}>
                                    <CircularProgress />
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </>
        );
    }
}

const ItemHistoryComponent = props => (
    <>
        <SessionContext.Consumer>
            {sessionState => (
                <ItemsContext.Consumer>
                    {itemsState => (
                        <PageContext.Consumer>
                            {pageState => (
                                <StatefulItemHistoryComponent {...props} sessionState={sessionState} itemsState={itemsState} pageState={pageState} />
                            )}
                        </PageContext.Consumer>
                    )}
                </ItemsContext.Consumer>
            )}
        </SessionContext.Consumer>
    </>
);
export default ItemHistoryComponent;