import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import { withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableFooter from '@material-ui/core/TableFooter';
import TableHead from '@material-ui/core/TableHead';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import React from 'react';
import { Redirect } from 'react-router';


const styles = theme => ({
    button: {
        margin: '7px',
    },
    link: {
        textDecoration: 'none',
    },
    formControl: {
        width: '100px',
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    searchInput: {
        marginRight: theme.spacing.unit,
    },
    new: {
        marginTop: theme.spacing.unit,
    },
    selectionText: {
        marginRight: '18px',
    },
    clickable: {
        cursor: 'pointer',
    }
});

@withStyles(styles)
export default class PagedTable extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            page: 0,
            rowsPerPage: 10,
            selected: [],
        };
    }

    handleSelection = (index) => {
        this.setState({
            selected: this.state.selected.includes(index) ? this.state.selected.filter(item => item !== index) : this.state.selected.concat(index),
        });
    }

    handleChangePage = (event, page) => {
        this.setState({ page });
    };

    handleChangeRowsPerPage = event => {
        this.setState({ page: 0, rowsPerPage: event.target.value });
    };

    handleRowClick(id) {
        if (this.state.selected.length > 0) {
            this.handleSelection(id);
        } else {
            this.setState({ redirect: id });
        }
    }

    render() {
        const { classes, rows, selectionHeader, headers, redirect } = this.props;
        const { rowsPerPage, page, selected } = this.state;
        const emptyRows = rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
        const singlePage = emptyRows < rows.length;
        if (this.state.redirect) {
            return (<Redirect to={redirect(this.state.redirect)} />);
        }
        return (
            <Table>
                {selected.length > 0 ? (
                    <>
                        <TableHead>
                            <TableRow>
                                <TableCell align="right" colSpan={headers.length + 1}>
                                    <span className={classes.selectionText}>
                                            Auswahl ({selected.length})
                                    </span>
                                    {selectionHeader}
                                </TableCell>
                            </TableRow>
                        </TableHead>
                    </>
                ) : null}
                <TableHead>
                    <TableRow>
                        {headers.map(header => !header.hidden && (
                                <TableCell key={header.key} align={header.align ? header.align : 'left'} onClick={event => this.handleRowClick(row.id)}>
                                    {header.name}
                                </TableCell>
                            ))}
                        <TableCell align="right" padding="checkbox">
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                        <TableRow
                            key={row.id}
                            className={classes.clickable}
                        >
                            {headers.map(header => !header.hidden && (
                                <TableCell key={header.key} align={header.align ? header.align : 'left'}  onClick={event => this.handleRowClick(row.id)}>
                                    {row[header.key]}
                                </TableCell>
                            ))}
                            <TableCell align="right" padding="checkbox">
                                <Checkbox
                                    checked={selected.includes(row.id)}
                                    onChange={() => this.handleSelection(row.id)}
                                />
                            </TableCell>
                        </TableRow>
                    ))}
                    {emptyRows > 0 && singlePage && (
                        <TableRow style={{ height: 48 * emptyRows }}>
                            <TableCell colSpan={6} />
                        </TableRow>
                    )}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TableCell>
                            <Button variant="contained" onClick={() => this.handleRowClick('new')} className={classes.new}>
                                Hinzufügen
                            </Button>
                        </TableCell>
                        {emptyRows < rows.length && (
                            <TablePagination
                                rowsPerPageOptions={[5, 10, 25]}
                                colSpan={3}
                                count={rows.length}
                                rowsPerPage={rowsPerPage}
                                page={page}
                                SelectProps={{
                                    native: true,
                                }}
                                onChangePage={this.handleChangePage}
                                onChangeRowsPerPage={this.handleChangeRowsPerPage}
                            />
                        )}
                    </TableRow>
                </TableFooter>
            </Table>
        );
    }
}
