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
import classNames from 'classnames';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import { Typography } from '@material-ui/core';
import LenientRedirect from './util/lenient-redirect';


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
        display: 'inline',
        verticalAlign: 'middle',
    },
    toolbar: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flexWrap: 'wrap-reverse',
    },
    clickable: {
        cursor: 'pointer',
    },
    checkboxColumn: {
        width: '50px',
    },
    tableHeadCell: {
        color: theme.palette.primary.main,
    },
    semiImportantCell: {
        [theme.breakpoints.down('xs')]: {
            display: 'none',
        },
    },
    unimportantCell: {
        [theme.breakpoints.down('sm')]: {
            display: 'none',
        },
    },
    paginationWrapper: {
        display: 'flex',
        justifyContent: 'space-between',
        flexWrap: 'wrap-reverse',
    },
    fitWidth: {
        width: 'initial',
    },
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

    handleSelection(index) {
        this.setState({
            selected: this.state.selected.includes(index) ? this.state.selected.filter(item => item !== index) : this.state.selected.concat(index),
        });
    }

    handleBulkCheckbox() {
        this.setState(prevState => ({
            selected: prevState.selected.length > 0 ? [] : this.props.rows.map(row => row.id),
        }));
    }

    resetSelection() {
        this.setState(prevState => ({
            selected: [],
        }));
    }

    handleChangePage = (event, page) => {
        this.setState({ page });
    };

    handleChangeRowsPerPage = event => {
        this.setState({ page: 0, rowsPerPage: parseInt(event.target.value) });
    };

    handleRowClick(id) {
        if (this.state.selected.length > 0) {
            this.handleSelection(id);
        } else {
            this.setState({ redirect: id });
        }
    }


    render() {
        const { classes, rows, SelectionHeader, filter, headers, redirect, title, showAddButton, fitWidth } = this.props;
        const { rowsPerPage, page, selected } = this.state;
        const emptyRows = rowsPerPage >= rows.length ? 0 : rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
        const singlePage = emptyRows < rows.length;
        if (this.state.redirect) {
            return (<LenientRedirect to={redirect(this.state.redirect)} />);
        }
        return (
            <Table className={fitWidth && classes.fitWidth}>
                {(filter || SelectionHeader) && (
                    <TableHead>
                        <TableRow>
                            <TableCell align="left" colSpan={headers.length + 1}>
                                <div className={classes.toolbar}>
                                    {selected.length > 0 ? (
                                        <div>
                                            <Typography variant="h6" className={classes.selectionText}>
                                                Auswahl ({selected.length})
                                    </Typography>
                                            <SelectionHeader selected={selected} resetSelection={() => this.resetSelection()} />
                                        </div>
                                    ) : (
                                            <Typography variant="h6">{title}</Typography>
                                        )}
                                    <div>
                                        {filter}
                                    </div>
                                </div>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                )}
                <TableHead>
                    <TableRow>
                        {SelectionHeader && (
                            <TableCell align="left" className={classes.checkboxColumn} padding="checkbox">
                                <Checkbox
                                    indeterminate={selected.length > 0 && selected.length < rows.length}
                                    checked={selected.length >= rows.length}
                                    onChange={() => this.handleBulkCheckbox()}
                                    color="primary"
                                />
                            </TableCell>
                        )}
                        {headers.map(header => !header.hidden && (
                            <TableCell className={classNames(classes.tableHeadCell, {
                                [classes.semiImportantCell]: header.semiImportant,
                                [classes.unimportantCell]: header.unimportant,
                            })} key={header.key} align={header.align ? header.align : 'left'}>
                                {header.name}
                            </TableCell>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                        <TableRow
                            key={row.id}
                            className={classes.clickable}
                        >
                            {SelectionHeader && (
                                <TableCell align="left" className={classes.checkboxColumn} padding="checkbox">
                                    <Checkbox
                                        checked={selected.includes(row.id)}
                                        onChange={() => this.handleSelection(row.id)}
                                    />
                                </TableCell>
                            )}
                            {headers.map(header => !header.hidden && (
                                <TableCell className={classNames({
                                    [classes.semiImportantCell]: header.semiImportant,
                                    [classes.unimportantCell]: header.unimportant,
                                })} key={header.key} align={header.align ? header.align : 'left'} onClick={event => this.handleRowClick(row.id)}>
                                    {header.converter ? header.converter(row[header.key]) : row[header.key]}
                                </TableCell>
                            ))}
                        </TableRow>
                    ))}
                    {emptyRows > 0 && singlePage && (
                        <TableRow style={{ height: 48 * emptyRows }}>
                            <TableCell colSpan={headers.length + (SelectionHeader ? 1 : 0)} />
                        </TableRow>
                    )}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TableCell colSpan={headers.length + (SelectionHeader ? 1 : 0)}>
                            <div className={classes.paginationWrapper}>
                                {showAddButton ? (
                                    <Button variant="contained" onClick={() => this.handleRowClick(NEW_ENTITY_ID_PLACEHOLDER)} className={classes.new}>
                                        Hinzufügen
                                    </Button>
                                ) : (<div></div>)}

                                {emptyRows < rows.length && (
                                    <TablePagination
                                        labelRowsPerPage="Einträge pro Seite:"
                                        rowsPerPageOptions={[5, 10, 25, 100, 250]}
                                        count={rows.length}
                                        rowsPerPage={rowsPerPage}
                                        page={page}
                                        SelectProps={{
                                            native: true,
                                        }}
                                        component="div"
                                        onChangePage={this.handleChangePage}
                                        onChangeRowsPerPage={this.handleChangeRowsPerPage}
                                    />
                                )}
                            </div>
                        </TableCell>
                    </TableRow>
                </TableFooter>
            </Table>
        );
    }
}
