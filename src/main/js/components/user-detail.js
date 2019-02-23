import React, { useState } from 'react';
import Checkbox from '@material-ui/core/Checkbox';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Button from '@material-ui/core/Button';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Icon from '@material-ui/core/Icon';
import Grid from '@material-ui/core/Grid';

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
    }
});

const UserComponent = props => {

    const { classes, role, saveHandler, showEdit } = props;

    const [edit, setEdit] = useState(!props.user);
    const [user, setUser] = useState(props.user ? props.user : {firstName:'', lastName:'',email:'',telephoneNumber:'',mobileNumber:'',businessNumber:''})

    const handleSave = () => {
        if (saveHandler) {
            saveHandler({
                ...user,
                role,
            })
        }
        setEdit(false);
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
                    {saveHandler ?
                        (<Button variant="contained" type="submit" onClick={handleSave}>
                            Speichern
                    </Button>) : null}
                </>)
                :
                (<>
                    <Grid container wrap="wrap" spacing={8}>
                        <Grid item xs={6} sm={2} className={classes.userData}><span className={classes.bold}>{user ? user.lastName : ''}, {user ? user.firstName : ''}</span></Grid>
                        <Grid item xs={6} sm={2} className={classes.userData}><a href={user?'mailto:'+user.email:''}>{user?user.email:''}</a></Grid>
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
                        <Grid item sm={1} className={classes.userData}>{showEdit || true ?
                        (<Button size="small" color="secondary" onClick={() => setEdit(true)}>
                            <Icon>create</Icon>
                        </Button>) : null}</Grid>                            
                    </Grid>

                </>)
            }
        </div>
    )
}

export default withStyles(styles)(UserComponent);