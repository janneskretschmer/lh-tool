import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import AddIcon from '@material-ui/icons/Add';
import CloseIcon from '@material-ui/icons/Close';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';
import SaveIcon from '@material-ui/icons/Save';
import React from 'react';
import { Link } from 'react-router-dom';
import { fullPathOfItem } from '../../paths';
import { SessionContext } from '../../providers/session-provider';
import { withContext, convertToMUIFormat, convertFromMUIFormat } from '../../util';
import ItemListComponent from '../item/item-list';
import CircularProgress from '@material-ui/core/CircularProgress';
import { fetchStore, createOrUpdateStore, fetchStoreProjects, deleteAndCreateStoreProjects } from '../../actions/store';
import { fetchOwnProjects } from '../../actions/project';
import SlotListComponent from '../slot/slot-list';

const styles = theme => ({
    button: {
        marginRight: theme.spacing.unit,
    },
    bold: {
        fontWeight: '500',
    },
    title: {
        fontSize: '30px',
        marginBottom: '10px',
    },
    container: {
        display: 'inline-block',
        verticalAlign: 'top',
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        marginTop: theme.spacing.unit,
    },
    verticalCenteredContainer: {
        display: 'flex',
        alignItems: 'baseline',
    },
    margin: {
        margin: theme.spacing.unit,
    }

});

const ProjectList = (props) => {
    return props.storeProjects ? (
        props.storeProjects.length > 0 ?
            props.storeProjects.map(storeProject => (
                <span key={storeProject.projectId + storeProject.start}>
                    <ProjectName projects={props.projects} projectId={storeProject.projectId}></ProjectName> ({storeProject.start.format('DD.MM.YYYY')} - {storeProject.end.format('DD.MM.YYYY')}) {props.edit ? (<IconButton onClick={() => props.deletionHandler(storeProject)}><DeleteIcon /></IconButton>) : null}<br />
                </span>
            )) : (<>-<br /></>)
    ) : props.edit ? null : (<CircularProgress />)
}

const ProjectName = (props) => {
    if (props.projects) {
        const project = props.projects.filter(project => project.id === props.projectId)[0];
        if (project) {
            return (<>{project.name}</>)
        }
    }
    return (<CircularProgress size={15} />)
}

@withStyles(styles)
@withContext('sessionState', SessionContext)
export default class StoreDetailComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            edit: props.new,
            store: null,
            storeProjects: null,
            selectedProjectIndex: 0,
            selectedProjectStartDate: null,
            selectedProjectEndDate: null,
        };
    }


    changeEditState(edit, callback) {
        this.setState({
            edit,
            saving: false,
        }, callback)
    }

    changeTitle(event) {
        const name = event.target.value
        this.setState(prevState => ({
            store: {
                ...prevState.store,
                name,
            }
        }))
    }

    changeAddress(event) {
        const address = event.target.value
        this.setState(prevState => ({
            store: {
                ...prevState.store,
                address,
            }
        }))
    }

    changeType(event) {
        const type = event.target.value
        this.setState(prevState => ({
            store: {
                ...prevState.store,
                type,
            }
        }))
    }

    changeSelectedProject(event) {
        this.changeSelectedProjectIndex(event.target.value)
    }

    changeSelectedProjectIndex(selectedProjectIndex) {
        this.setState(prevState => ({
            selectedProjectIndex,
            selectedProjectStartDate: convertToMUIFormat(prevState.projects[selectedProjectIndex].startDate),
            selectedProjectEndDate: convertToMUIFormat(prevState.projects[selectedProjectIndex].endDate),
        }))
    }

    changeSelectedProjectStartDate(event) {
        var selectedProjectStartDate = event.target.value
        if (selectedProjectStartDate > this.state.selectedProjectEndDate) {
            this.changeSelectedProjectEndDate(event)
        }
        this.setState({
            selectedProjectStartDate: this.getDateWithinProjectRange(selectedProjectStartDate)
        })
    }

    changeSelectedProjectEndDate(event) {
        var selectedProjectEndDate = event.target.value
        if (selectedProjectEndDate < this.state.selectedProjectStartDate) {
            this.changeSelectedProjectStartDate(event)
        }
        this.setState({
            selectedProjectEndDate: this.getDateWithinProjectRange(selectedProjectEndDate)
        })
    }

    getDateWithinProjectRange(date) {
        const projectStartDate = convertToMUIFormat(this.state.projects[this.state.selectedProjectIndex].startDate)
        if (date <= projectStartDate) {
            return projectStartDate
        }
        const projectEndDate = convertToMUIFormat(this.state.projects[this.state.selectedProjectIndex].endDate)
        if (date >= projectEndDate) {
            return projectEndDate
        }
        return date
    }

    addProject() {
        this.setState(prevState => ({
            storeProjects: [...(prevState.storeProjects ? prevState.storeProjects : []), {
                //doesn't work with prevState...
                projectId: this.state.projects[this.state.selectedProjectIndex].id,
                storeId: prevState.store.id,
                start: convertFromMUIFormat(this.state.selectedProjectStartDate),
                end: convertFromMUIFormat(this.state.selectedProjectEndDate),
            }],
        }), this.changeSelectedProjectIndex(0))
    }

    removeProject(storeProject) {
        this.setState(prevState => ({
            storeProjects: prevState.storeProjects.filter(tmp => tmp.projectId != storeProject.projectId || tmp.start != storeProject.start)
        }))
    }

    loadStore() {
        const id = this.props.match.params.id
        if (id === 'new') {
            this.setState({
                edit: true,
                store: {
                    name: '',
                    address: '',
                    type: 'STANDARD'
                },
            })
        } else {
            fetchStore({ accessToken: this.props.sessionState.accessToken, storeId: id }).then(store => this.changeStore(store))
            fetchStoreProjects({ accessToken: this.props.sessionState.accessToken, storeId: id }).then(storeProjects => this.setState({ storeProjects }))
        }
    }

    changeStore(store, callback) {
        this.setState(prevState => ({
            store,
            storeProjects: prevState.storeProjects ? prevState.storeProjects.map(storeProject => ({
                ...storeProject,
                storeId: store.id,
            })) : null,
        }), callback)
    }

    componentDidMount() {
        this.loadStore()
        fetchOwnProjects({ accessToken: this.props.sessionState.accessToken }).then(projects => this.setState({ projects }, () => this.changeSelectedProjectIndex(0)))
    }

    save() {
        this.setState({
            saving: true
        })
        createOrUpdateStore({ accessToken: this.props.sessionState.accessToken, store: this.state.store }).then(store => this.changeStore(store,
            () => deleteAndCreateStoreProjects({ accessToken: this.props.sessionState.accessToken, storeId: this.state.store.id, storeProjects: this.state.storeProjects }).then(
                storeProjects => this.setState({ storeProjects }, () => this.changeEditState(false))
            )))
    }

    cancel() {
        this.changeEditState(false,
            this.changeStore(null, () => {
                this.loadStore()
            })
        )
    }

    render() {
        const { classes, match } = this.props
        const { edit, store, saving, storeProjects, projects, selectedProjectIndex, selectedProjectStartDate, selectedProjectEndDate } = this.state
        const types = {
            MAIN: 'Hauptlager',
            STANDARD: 'Lager',
            MOBILE: 'Magazin',
        }
        return store || edit ? (
            <>
                <div>
                    <div className={classes.title}>
                        {edit ? (
                            <TextField
                                id="name"
                                label="Name"
                                className={classes.textField}
                                value={store.name ? store.name : ''}
                                onChange={this.changeTitle.bind(this)}
                                margin="dense"
                                variant="outlined"
                            />
                        ) : (store.name)}
                        {edit ? (
                            saving ? (<>&nbsp;<CircularProgress /></>) : (
                                <>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={this.save.bind(this)}>
                                        <SaveIcon />
                                    </IconButton>
                                    <IconButton variant="contained" className={classes.button} type="submit" onClick={this.cancel.bind(this)}>
                                        <CloseIcon />
                                    </IconButton>
                                </>
                            )
                        ) : store ? (
                            <>
                                <IconButton variant="contained" className={classes.button} type="submit" onClick={() => this.changeEditState(true)}>
                                    <EditIcon />
                                </IconButton>
                            </>
                        ) : null}
                    </div>
                    <div className={classes.container}>
                        {edit ? (
                            <>
                                <TextField
                                    id="address"
                                    label="Adresse"
                                    multiline
                                    className={classes.textField}
                                    value={store.address ? store.address : ''}
                                    onChange={this.changeAddress.bind(this)}
                                    margin="dense"
                                    variant="outlined"
                                /><br />
                                <br />
                            </>
                        ) : (
                                <>
                                    <div className={classes.bold}>
                                        Adresse
                                </div>
                                    {store.address}<br />
                                    <br />
                                </>
                            )}
                        {edit ? (
                            <>
                                <FormControl className={classes.formControl}>
                                    <InputLabel htmlFor="type">Typ</InputLabel>
                                    <Select
                                        value={store.type ? store.type : 'STANDARD'}
                                        onChange={this.changeType.bind(this)}
                                        inputProps={{
                                            name: 'type',
                                            id: 'type',
                                        }}
                                    >
                                        <MenuItem value={'STANDARD'}>{types['STANDARD']}</MenuItem>
                                        <MenuItem value={'MOBILE'}>{types['MOBILE']}</MenuItem>
                                        <MenuItem value={'MAIN'}>{types['MAIN']}</MenuItem>
                                    </Select>
                                </FormControl><br />
                                <br />
                            </>
                        ) : (
                                <>
                                    <div className={classes.bold}>
                                        Typ
                        </div>
                                    {types[store.type]}<br />
                                </>
                            )}
                        <br />
                        <div className={classes.bold}>
                            Projekte
                        </div>
                        {edit ? projects ? (
                            <div className={classes.verticalCenteredContainer}>
                                <FormControl className={classes.formControl}>
                                    <InputLabel htmlFor="project">Projekt</InputLabel>
                                    <Select
                                        value={selectedProjectIndex ? selectedProjectIndex : 0}
                                        onChange={this.changeSelectedProject.bind(this)}
                                        inputProps={{
                                            name: 'project',
                                            id: 'project',
                                        }}
                                    >
                                        {projects.map((project, index) => (
                                            <MenuItem key={project.id} value={index}>{project.name}</MenuItem>
                                        ))}
                                    </Select>
                                </FormControl> von <TextField
                                    type="date"
                                    value={selectedProjectStartDate ? selectedProjectStartDate : ''}
                                    className={classes.margin}
                                    margin="dense"
                                    variant="outlined"
                                    onChange={this.changeSelectedProjectStartDate.bind(this)}
                                /> bis <TextField
                                    type="date"
                                    value={selectedProjectEndDate ? selectedProjectEndDate : ''}
                                    className={classes.margin}
                                    margin="dense"
                                    variant="outlined"
                                    onChange={this.changeSelectedProjectEndDate.bind(this)}
                                />
                                <Button variant="contained" onClick={this.addProject.bind(this)}>Hinzufügen</Button>
                            </div>
                        ) : (<CircularProgress />) : null}
                        <ProjectList storeProjects={storeProjects} projects={projects} edit={edit} deletionHandler={this.removeProject.bind(this)}></ProjectList>
                    </div>
                </div>
                {edit ?
                    saving ? (<>&nbsp;<CircularProgress /></>) : (
                        <>
                            <Button variant="contained" className={classes.button} type="submit" onClick={this.save.bind(this)}>
                                Speichern
                            </Button>
                            <Button variant="outlined" className={classes.button} type="submit" onClick={this.cancel.bind(this)}>
                                Abbrechen
                            </Button>
                        </>
                    )
                    : (
                        <>
                            <br />
                            <br />
                            <div className={classes.bold}>
                                Lagerplätze
                             </div>
                            <SlotListComponent storeId={match.params.id * 1} />
                            <br />
                            <br />
                            <div className={classes.bold}>
                                Artikel
                            </div>
                            <ItemListComponent store={match.params.id * 1} />
                        </>
                    )}
            </>
        ) : (<CircularProgress />)
    }
}
