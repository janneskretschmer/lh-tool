import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import SearchIcon from '@mui/icons-material/Search';
import { IconButton, TextField, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableFooter from '@mui/material/TableFooter';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import { Box } from '@mui/system';
import _ from 'lodash';
import React from 'react';
import { NEW_ENTITY_ID_PLACEHOLDER } from '../config';
import LenientRedirect from './util/lenient-redirect';

export default class PagedTable extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            expandFilters: false,
            page: 0,
            rowsPerPage: 10,
            selected: [],
        };
    }

    static getDerivedStateFromProps(props, state) {
        // componentDidUpdate didn't get called in some scenarios, even though props changed
        if (props.selected && !_.isEqual(state.selected, props.selected)) {
            return { selected: props.selected };
        }
        return null;
    }

    handleSelection(index) {
        if (this.props.onToggleSelect) {
            this.props.onToggleSelect(index);
        } else {
            this.setState({
                selected: this.state.selected.includes(index) ? this.state.selected.filter(item => item !== index) : this.state.selected.concat(index),
            });
        }
    }

    handleBulkCheckbox() {
        if (this.props.onToggleSelect) {
            if (this.state.selected.length > 0) {
                this.state.selected.forEach(id => this.props.onToggleSelect(id));
            } else {
                this.props.rows.forEach(row => this.props.onToggleSelect(row.id));
            }
        } else {
            this.setState(prevState => ({
                selected: prevState.selected.length > 0 ? [] : this.props.rows.map(row => row.id),
            }));
        }
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
        if (this.state.selected.length > 0 || !this.props.redirect) {
            this.handleSelection(id);
        } else {
            this.setState({ redirect: id });
        }
    }

    toggleExpandFilters() {
        this.setState(prevState => ({
            expandFilters: !prevState.expandFilters,
        }));
    }

    render() {
        const { classes, rows, SelectionHeader, onToggleSelect, filter, headers, redirect, title, showAddButton, fitWidth, freeTextValue, onChangeFreeText, onFilter, additionalFilters, keepFiltersExpanded } = this.props;
        const { rowsPerPage, page, selected, expandFilters } = this.state;
        const emptyRows = rowsPerPage >= rows.length ? 0 : rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
        const singlePage = emptyRows < rows.length;
        if (this.state.redirect) {
            return (<LenientRedirect to={redirect(this.state.redirect)} />);
        }
        return (
            <Table sx={fitWidth && {
                width: 'initial',
            }}>
                {(filter || SelectionHeader) && (
                    <TableHead>
                        <TableRow>
                            <TableCell align="left" colSpan={headers.length + 1}>
                                <Box sx={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    flexWrap: 'wrap-reverse',
                                }}>
                                    {SelectionHeader && selected.length > 0 ? (
                                        <div>
                                            <Box typography="h6" sx={{
                                                marginRight: '18px',
                                                display: 'inline',
                                                verticalAlign: 'middle',
                                            }}>
                                                Auswahl ({selected.length})
                                            </Box>
                                            <SelectionHeader selected={selected} resetSelection={() => this.resetSelection()} />
                                        </div>
                                    ) : (
                                        <Typography variant="h6">{title}</Typography>
                                    )}
                                    <Box sx={{
                                        textAlign: 'right',
                                    }}>
                                        {(onChangeFreeText && onFilter) && (
                                            <Box sx={{
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'end'
                                            }}>
                                                <div>
                                                    <TextField
                                                        sx={{ m: 1 }}
                                                        id="free-search"
                                                        value={freeTextValue}
                                                        onChange={event => onChangeFreeText(event.target.value)}
                                                        variant="outlined"
                                                        label="Freitextsuche"
                                                        size="small"
                                                    />
                                                </div>
                                                {additionalFilters && (
                                                    <IconButton
                                                        disabled={!!keepFiltersExpanded}
                                                        onClick={() => this.toggleExpandFilters()}
                                                        size="large">
                                                        {expandFilters || keepFiltersExpanded ? (<ExpandLessIcon />) : (<ExpandMoreIcon />)}
                                                    </IconButton>
                                                )}
                                                <IconButton onClick={() => onFilter()} size="large">
                                                    <SearchIcon />
                                                </IconButton>
                                            </Box>
                                        )}
                                        {(expandFilters || keepFiltersExpanded) && (
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                {additionalFilters}
                                            </Box>
                                        )}
                                    </Box>
                                </Box>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                )
                }
                <TableHead>
                    <TableRow>
                        {(selected || SelectionHeader) && (
                            <TableCell align="left" sx={{ width: '50px' }} padding="checkbox">
                                <Checkbox
                                    indeterminate={selected.length > 0 && selected.length < rows.length}
                                    checked={selected.length >= rows.length}
                                    onChange={() => this.handleBulkCheckbox()}
                                    color="primary"
                                />
                            </TableCell>
                        )}
                        {headers.map(header => !header.hidden && (
                            <TableCell sx={{
                                color: 'primary.main',
                                display: {
                                    xs: ((header.semiImportant || header.unimportant) && 'none') || 'table-cell',
                                    md: ((header.unimportant) && 'none') || 'table-cell',
                                    lg: 'table-cell'
                                }
                            }} key={header.key} align={header.align ? header.align : 'left'}>
                                {header.name}
                            </TableCell>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                        <TableRow
                            key={row.id}
                            sx={{
                                cursor: 'pointer',
                            }}
                        >
                            {(selected || SelectionHeader) && (
                                <TableCell align="left" sx={{ width: '50px' }} padding="checkbox">
                                    <Checkbox
                                        checked={selected.includes(row.id)}
                                        onChange={() => this.handleSelection(row.id)}
                                    />
                                </TableCell>
                            )}
                            {headers.map(header => !header.hidden && (
                                <TableCell sx={{
                                    display: {
                                        xs: ((header.semiImportant || header.unimportant) && 'none') || 'table-cell',
                                        md: ((header.unimportant) && 'none') || 'table-cell',
                                        lg: 'table-cell'
                                    }
                                }} key={header.key} align={header.align ? header.align : 'left'} onClick={event => this.handleRowClick(row.id)}>
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
                            <Box sx={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                flexWrap: 'wrap-reverse',
                                alignItems: 'baseline',
                            }}>
                                {showAddButton ? (
                                    <Button variant="contained" onClick={() => this.handleRowClick(NEW_ENTITY_ID_PLACEHOLDER)} sx={{ mt: 1 }}>
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
                                        onPageChange={this.handleChangePage}
                                        onRowsPerPageChange={this.handleChangeRowsPerPage}
                                    />
                                )}
                            </Box>
                        </TableCell>
                    </TableRow>
                </TableFooter>
            </Table >
        );
    }
}
