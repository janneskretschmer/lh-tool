import React from 'react';
import { SessionContext } from '../providers/session-provider';
import { withContext } from '../util';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableFooter from '@material-ui/core/TableFooter';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import SearchIcon from '@material-ui/icons/Search';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import TextField from '@material-ui/core/TextField';
import InputAdornment from '@material-ui/core/InputAdornment';
import { withStyles } from '@material-ui/core/styles';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import { Link } from 'react-router-dom';
import { fullPathOfItem } from '../paths';

const styles = theme => ({
  button: {
    margin: '7px',
  },
  link: {
    textDecoration: 'none',
  },
});

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class ItemListComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
                rows: [
                  {id:1,name:'Hammer',store:1,slot:1,quantity:3,unit:'Stueck'},
                  {id:2,name:'Hammer',store:2,slot:2,quantity:3,unit:'Stueck'},
                  {id:3,name:'Meissel',store:1,slot:3,quantity:4,unit:'Stueck'},
                  {id:4,name:'Presslufthammer',slot:4,store:1,quantity:5,unit:'Stueck'},
                  {id:5,name:'Schlauch dick',slot:5,store:2,quantity:2,unit:'m'},
                  {id:6,name:'Schlauch duenn',slot:1,store:2,quantity:2,unit:'m'},
                  {id:7,name:'Schlauch dick',slot:2,store:1,quantity:4,unit:'m'},
                  {id:8,name:'Silikon',store:2,slot:3,quantity:2,unit:'Stueck'},
                  {id:9,name:'Zange',store:2,slot:4,quantity:3,unit:'Stueck'},
                  {id:10,name:'Zange',store:3,slot:5,quantity:2,unit:'Stueck'},
                  {id:11,name:'Zollstock',store:2,slot:1,quantity:2,unit:'Stueck'},
                  {id:12,name:'Zollstock',store:1,slot:3,quantity:7,unit:'Stueck'},
                ],//.sort((a, b) => (a.name < b.name ? -1 : 1)),
                page: 0,
                rowsPerPage: 10,

                showFilters: false,
              };
    }

  handleChangePage = (event, page) => {
    this.setState({ page });
  };

  handleChangeRowsPerPage = event => {
    this.setState({ page: 0, rowsPerPage: event.target.value });
  };

  handleToggleShowFilters = () => {
    this.setState({ showFilters: !this.state.showFilters})
  }

    render() {
        const { classes } = this.props
        const { rows, rowsPerPage, page, showFilters } = this.state;
        const emptyRows = rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
        return (
            <SessionContext.Consumer>
                {sessionState => (
                  <>
                    <TextField
                      id="free-search"
                      variant="outlined"
                      label="Freitextsuche"
                      margin="dense"
                      InputProps={{
                        startAdornment: <InputAdornment position="start"><SearchIcon /></InputAdornment>,
                      }}
                    />
                    <IconButton className={classes.button} variant="outlined" onClick={this.handleToggleShowFilters.bind(this)}>
                      {showFilters ? (<ExpandLessIcon />) : (<ExpandMoreIcon />)}
                    </IconButton>
                    {showFilters ? (
                      <>
                        <br />
                        <TextField
                          id="name-search"
                          variant="outlined"
                          label="Bezeichnung"
                          margin="dense"
                        />
                        <TextField
                          id="description-search"
                          variant="outlined"
                          label="Beschreibung"
                          margin="dense"
                        />
                        <br />
                        TODO: Dropdown fuer Lager, Platz (abhaengig von Lager), Tags, Gewerke, evtl. Menge (mindestens)
                        <br />
                      </>
                    ) : null}
                    <Button variant="contained">
                      Suchen
                    </Button>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Bezeichnung</TableCell>
                          <TableCell align="right">Lager</TableCell>
                          <TableCell align="right">Platz</TableCell>
                          <TableCell align="right">Menge</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                            <TableRow key={row.id}>
                              <TableCell component="th" scope="row">
                                <Link to={fullPathOfItem()} className={classes.link}>
                                  {row.name}
                                </Link>
                              </TableCell>
                              <TableCell align="right">
                                <Link to={fullPathOfItem()} className={classes.link}>
                                  {row.store}
                                </Link>
                              </TableCell>
                              <TableCell align="right">
                                <Link to={fullPathOfItem()} className={classes.link}>
                                  {row.slot}
                                </Link>
                              </TableCell>
                              <TableCell align="right">
                                <Link to={fullPathOfItem()} className={classes.link}>
                                  {row.quantity} {row.unit}
                                </Link>
                              </TableCell>
                            </TableRow>
                        ))}
                        {emptyRows > 0 && (
                          <TableRow style={{ height: 48 * emptyRows }}>
                            <TableCell colSpan={6} />
                          </TableRow>
                        )}
                      </TableBody>
                      <TableFooter>
                        <TableRow>
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
                        </TableRow>
                      </TableFooter>
                    </Table>
                  </>
                )}
            </SessionContext.Consumer>
        );
    }
}
