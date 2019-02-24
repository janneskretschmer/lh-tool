import React from 'react';
import { withSnackbar } from 'notistack';
import { withStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import { ProjectsContext } from '../providers/projects-provider';
import { SessionContext } from '../providers/session-provider';
import { deleteProject } from '../actions/project';
<<<<<<< Upstream, based on origin/master
import WithPermission from './with-permission';
=======
import Typography from '@material-ui/core/Typography';
<<<<<<< Upstream, based on origin/master

import Checkbox from '@material-ui/core/Checkbox';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
>>>>>>> ef8c21b started project edit component
=======
import UserComponent from './user-detail';
<<<<<<< Upstream, based on origin/master
import { createNewUser } from '../actions/user'
<<<<<<< Upstream, based on origin/master
>>>>>>> 0224d3f Edit mask for local coordinator
=======
=======
import { createNewUser, deleteUser } from '../actions/user'
>>>>>>> 4820795 implemented deletion of user
import SimpleDialog from './simple-dialog.js'
import Button from '@material-ui/core/Button';
>>>>>>> ef296a8 refactored dialog

const styles = theme => ({
    root: {
    },
    button: {
        margin: theme.spacing.unit,
    },
    leftIcon: {
        marginRight: theme.spacing.unit,
    },
    rightIcon: {
        marginLeft: theme.spacing.unit,
    },
});

<<<<<<< Upstream, based on origin/master
const Transition = props => (<Slide direction="up" {...props} />);

<<<<<<< Upstream, based on origin/master
<<<<<<< Upstream, based on origin/master
@withSnackbar
=======
const UserComponent = props => {
    let inputFirstName = null;
    let inputLastName = null;
    let inputEmail = null;
    let inputIsFemale = null;
    let inputTelephoneBusiness = null;
    let inputTelephoneMobile = null;
    let inputTelephoneHome = null;
    
    return (
        <div>
            <TextField
                id="first_name"
                label="Vorname"
                type="text"
                name="first_name"
                autoComplete="first name"
                margin="dense"
                variant="outlined"
                InputProps={{
                    inputRef: ref => inputFirstName = ref
                }}
                required
            />
            <TextField
                id="last_name"
                label="Nachname"
                type="text"
                name="last_name"
                autoComplete="last name"
                margin="dense"
                variant="outlined"
                InputProps={{
                    inputRef: ref => inputLastName = ref
                }}
                required
            />
            <TextField
                id="email"
                label="Email"
                type="email"
                name="email"
                autoComplete="email"
                margin="dense"
                variant="outlined"
                InputProps={{
                    inputRef: ref => inputEmail = ref
                }}
                required
            /><br/>
            <TextField
                id="telephone_home"
                label="Telefon Festnetz"
                type="text"
                name="telephone_home"
                autoComplete="telephone home"
                margin="dense"
                variant="outlined"
                InputProps={{
                    inputRef: ref => inputTelephoneHome = ref
                }}
            />
            <TextField
                id="telephone_mobile"
                label="Telefon Mobil"
                type="text"
                name="telephone_mobile"
                autoComplete="telephone mobile"
                margin="dense"
                variant="outlined"
                InputProps={{
                    inputRef: ref => inputTelephoneMobile = ref
                }}
            />
            <TextField
                id="telephone_business"
                label="Telefon Geschäftlich"
                type="text"
                name="telephone_business"
                autoComplete="telephone business"
                margin="dense"
                variant="outlined"
                InputProps={{
                    inputRef: ref => inputTelephoneBusiness = ref
                }}
            /><br></br>
            <FormControlLabel
                control={
                    <Checkbox
                        checked={props.user && props.user.gender == 'FEMALE'}
                
                        value="gender"
                        inputProps={{
                            ref: ref => inputIsFemale = ref
                        }}
                    />
                }
                label="Schwester"
            />
            <Button size="small" color="secondary">
                Abbrechen
            </Button>
            <Button variant="contained" type="submit">
                Speichern
            </Button>
        </div>
    )
}

>>>>>>> ef8c21b started project edit component
=======
>>>>>>> 0224d3f Edit mask for local coordinator
=======
>>>>>>> ef296a8 refactored dialog
@withStyles(styles)
export default class ProjectEditPanel extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            deleteDeleteDialogOpen: false,
        };
    }

    handleDeleteButtonClicked() {
        this.setState({ deleteDeleteDialogOpen: true });
    }

    handleDeleteDialogClose() {
        this.setState({ deleteDeleteDialogOpen: false });
    }

    handleDeleteFailure() {
        this.props.enqueueSnackbar('Fehler beim Löschens des Projekts', {
            variant: 'error',
        });
    }

    render() {
        const { classes, project } = this.props;
        return (
            <div>
                <SessionContext.Consumer>
                    {sessionState => (
                        <ProjectsContext.Consumer>
                            {projectsState => (
<<<<<<< Upstream, based on origin/master
                                <WithPermission permission="ROLE_RIGHT_PROJECTS_DELETE">
=======
                                <>
                                    <div><Typography variant="h6">Baudiener</Typography></div>
                                    <div><Typography variant="h6">Lokaler Koordinator</Typography></div>
                                    <UserComponent
                                        user={project.localCoordinator}
                                        role="ROLE_LOCAL_COORDINATOR"
                                        showEdit={true}
                                        onSave={user => createNewUser({ accessToken: sessionState.accessToken, ...user, projectId:project.id, projectsState })}
                                        showDelete={true}
                                        onDelete={user => deleteUser({accessToken: sessionState.accessToken, userId: user.id, projectsState})}
                                    />
                                    <div><Typography variant="h6">Verkündiger</Typography></div>
<<<<<<< Upstream, based on origin/master
>>>>>>> ef8c21b started project edit component
                                    <Button variant="contained" color="primary" className={classes.button} onClick={this.handleDeleteButtonClicked.bind(this)}>
                                        Projekt löschen
                                        <DeleteIcon className={classes.rightIcon} />
                                    </Button>
                                    <Dialog
                                        open={this.state.deleteDeleteDialogOpen}
                                        TransitionComponent={Transition}
                                        keepMounted
                                        onClose={this.handleDeleteDialogClose.bind(this)}
                                        aria-labelledby="alert-dialog-slide-title"
                                        aria-describedby="alert-dialog-slide-description"
                                    >
                                        <DialogTitle id="alert-dialog-slide-title">
                                            {`Projekt ${project.name} löschen`}
                                        </DialogTitle>
                                        <DialogContent>
                                            <DialogContentText id="alert-dialog-slide-description">
                                                {`Soll das Projekt ${project.name} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
                                            </DialogContentText>
                                        </DialogContent>
                                        <DialogActions>
                                            <Button onClick={this.handleDeleteDialogClose.bind(this)} color="secondary">
                                                {'Nein'}
                                            </Button>
                                            <Button color="primary" onClick={() => {
=======
                                    <SimpleDialog
                                        title={`Projekt ${project.name} löschen`}
                                        text={`Soll das Projekt ${project.name} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
                                        cancelText="Nein"
                                        okText={`Ja, Projekt ${project.name} löschen`}
                                        onOK={() => {
>>>>>>> ef296a8 refactored dialog
                                                deleteProject({
                                                    accessToken: sessionState.accessToken,
                                                    projectsState: projectsState,
                                                    projectId: this.props.project.id,
                                                    handleFailure: this.handleDeleteFailure.bind(this),
                                                });
<<<<<<< Upstream, based on origin/master
                                            }}>
                                                {`Ja, Projekt ${project.name} löschen`}
                                            </Button>
                                        </DialogActions>
                                    </Dialog>
                                </WithPermission>
=======
                                            }}
                                    >
                                        <Button variant="contained" color="primary" className={classes.button}>
                                            Projekt löschen
                                            <DeleteIcon className={classes.rightIcon} />
                                        </Button>
                                    </SimpleDialog>
                                </>
>>>>>>> ef296a8 refactored dialog
                            )}
                        </ProjectsContext.Consumer>

                    )}
                </SessionContext.Consumer>
            </div>
        );
    }
}