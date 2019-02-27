import React, { useState } from 'react';
import Checkbox from '@material-ui/core/Checkbox';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Button from '@material-ui/core/Button';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Icon from '@material-ui/core/Icon';
import Grid from '@material-ui/core/Grid';
import Hidden from '@material-ui/core/Hidden';
import { Link } from 'react-router-dom'
import IconButton from '@material-ui/core/IconButton';
import SimpleDialog from './simple-dialog.js'

const styles = theme => ({
    input: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
    },
    bold: {
        fontWeight: 'bold',
    },
    userData: {
        maxHeight: '150px',
        overflow: 'auto',
    },
    userName: {
        fontWeight: 'bold',
        marginTop: '14px',
    }

});

const UserComponent = props => {

    const { classes, role, onSave, onUpdate, showEdit, onDelete, showDelete } = props;

    const [edit, setEdit] = useState(!props.user);
    const [showDetails, setShowDetails] = useState(false);
    const newUser = !props.user;
    const [user, setUser] = useState(newUser ? {gender:'MALE', firstName:'', lastName:'',email:'',telephoneNumber:'',mobileNumber:'',businessNumber:''} : props.user)

    const disableSaveButton = !(onSave && newUser) && !(onUpdate && !newUser);
    const disableEditButton = !onUpdate;
    const disableDeleteButton = !onDelete;

    const handleSave = () => {
        if (onSave && newUser) {
            onSave({
                ...user,
                role,
            });
        } else if (onUpdate && !newUser) {
            onUpdate(user);
        } 
        setEdit(false);
    }

    const handleDelete = () => {
        if(onDelete) {
            onDelete(user)
        }
    }

    return (
        <div>
            {edit ?
                (<>
                    <TextField
                        id="first_name"
                        value={user.firstName}
                        label="Vorname"
                        type="text"
                        name="first_name"
                        autoComplete="first name"
                        margin="dense"
                        variant="outlined"
                        onChange={e => setUser({...user,firstName:e.target.value})}
                        className={classes.input}
                        required
                    />
                    <TextField
                        id="last_name"
                        value={user.lastName}
                        label="Nachname"
                        type="text"
                        name="last_name"
                        autoComplete="last name"
                        margin="dense"
                        variant="outlined"
                        onChange={e => setUser({...user,lastName:e.target.value})}
                        className={classes.input}
                        required
                    />
                    <TextField
                        id="email"
                        value={user.email}
                        label="Email"
                        type="email"
                        name="email"
                        autoComplete="email"
                        margin="dense"
                        variant="outlined"
                        onChange={e => setUser({...user,email:e.target.value})}
                        className={classes.input}
                        required
                    /><br />
                    <TextField
                        id="telephone_number"
                        value={user.telephoneNumber}
                        label="Telefon Festnetz"
                        type="text"
                        name="telephone_number"
                        autoComplete="telephone number"
                        margin="dense"
                        variant="outlined"
                        onChange={e => setUser({...user,telephoneNumber:e.target.value})}
                        className={classes.input}
                    />
                    <TextField
                        id="mobile_number"
                        value={user.mobileNumber}
                        label="Telefon Mobil"
                        type="text"
                        name="mobile_number"
                        autoComplete="telephone mobile number"
                        margin="dense"
                        variant="outlined"
                        onChange={e => setUser({...user,mobileNumber:e.target.value})}
                        className={classes.input}
                    />
                    <TextField
                        id="business_number"
                        value={user.businessNumber}
                        label="Telefon Geschäftlich"
                        type="text"
                        name="business_number"
                        autoComplete="telephone business number"
                        margin="dense"
                        variant="outlined"
                        onChange={e => setUser({...user,businessNumber:e.target.value})}
                        className={classes.input}
                    /><br />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={user.gender == 'FEMALE'}

                                value="gender"
                                onChange={e => setUser({...user,gender:e.target.checked?'FEMALE':'MALE'})}
                            />
                        }
                        label="Schwester"
                        className={classes.input}
                    />
                    {user ? (<Button size="small" color="secondary" onClick={() => setEdit(false)}>
                        Abbrechen
                    </Button>) : null}
                    {onSave ?
                        (<Button variant="contained" type="submit" onClick={handleSave} disabled={disableSaveButton}>
                            Speichern
                    </Button>) : null}
                </>)
                :
                (<>
                    <Grid container wrap="wrap" spacing={8}>
                        <Grid item xs={12} sm={2} container justify="space-between" className={classes.userData}>
                            <Grid item>
                                <Typography variant="body1" className={classes.userName}>{user ? user.lastName : ''}, {user ? user.firstName : ''}</Typography>
                            </Grid>
                            <Grid item>
                                <Hidden smUp>
                                    <EmailLink email={user?user.email:''} asIcon/>
                                    <DeleteButton shown={showDelete} disabled={disableDeleteButton} userName={user.firstName + ' ' + user.lastName} onDelete={handleDelete}/>
                                    <SimpleIconButton icon="create" shown={showEdit} disabled={disableEditButton} onClick={() => setEdit(true)}/>
                                    <IconButton size="small" color="secondary" onClick={() => setShowDetails(!showDetails)}>
                                        <Icon>{showDetails?'expand_less':'expand_more'}</Icon>
                                    </IconButton>
                                </Hidden>
                            </Grid>
                        </Grid>
                        <Hidden xsDown>
                            <Grid item xs={12} sm={2} className={classes.userData}><EmailLink email={user?user.email:''}/></Grid>
                        </Hidden>
                        <Hidden xsDown={!showDetails}>
                            <Grid item xs={12} sm={3} container className={classes.userData}>
                                    <Grid item>
                                        Festnetz:<br/>
                                        Mobil:<br/>
                                        Geschäftlich:                                
                                    </Grid>
                                    <Grid item>
                                        <a href={user ? 'tel:'+user.telephoneNumber:''}>{user ? user.telephoneNumber:''}</a><br/>
                                        <a href={user ? 'tel:'+user.mobileNumber:''}>{user ? user.mobileNumber:''}</a><br/>
                                        <a href={user ? 'tel:'+user.businessNumber:''}>{user ? user.businessNumber:''}</a>
                                    </Grid>
                            </Grid>
                            <Grid item xs={12}  sm={4} className={classes.userData}>
                                <span className={classes.bold}>Beruf:</span> {user ? user.profession : ''}<br/>
                                <span className={classes.bold}>Fähigkeiten:</span> {user ? user.skills:''}
                            </Grid>
                        </Hidden>
                        <Hidden xsDown>
                            <Grid item sm={1} className={classes.userData}>
                                <DeleteButton shown={showDelete} disabled={disableDeleteButton} userName={user.firstName + ' ' + user.lastName} onDelete={handleDelete}/>
                                <SimpleIconButton icon="create" shown={showEdit} disabled={disableEditButton} onClick={() => setEdit(true)}/>
                            </Grid>    
                        </Hidden>                        
                    </Grid>

                </>)
            }
        </div>
    )
}

const EmailLink = ({email,asIcon}) =>{ 
    if(asIcon){
        return (<IconButton component="a" href={'mailto:'+email}>
            <Icon color="secondary">email</Icon>
        </IconButton>);
    }else{
        return (<a href={'mailto:'+email}>{email}</a>);
    }
}

const DeleteButton = ({shown, disabled, userName, onDelete}) => 
    (<SimpleDialog
        title= {`Benutzer ${userName} löschen`}
        text= {`Soll der Benutzer ${userName} wirklich entfernt werden? Das lässt sich nicht rückgängig machen.`}
        cancelText="Nein"
        okText={`Ja, Benutzer ${userName} löschen`}
        onOK={onDelete}
    >
        <SimpleIconButton icon="delete" disabled={disabled} shown={shown}/>
    </SimpleDialog>)

const SimpleIconButton = ({shown, icon, disabled, onClick}) => {
    if(shown){
        return (<IconButton color="secondary" disabled={disabled} onClick={onClick}>
            <Icon>{icon}</Icon>
        </IconButton>) 
    }else{
        return null;
    }
}





export default withStyles(styles)(UserComponent);