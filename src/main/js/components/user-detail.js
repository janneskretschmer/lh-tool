import React, { useState } from 'react';
import Checkbox from '@material-ui/core/Checkbox';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Button from '@material-ui/core/Button';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Icon from '@material-ui/core/Icon';

const styles = theme => ({
    input: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
    },
    userName: {
        fontWeight: 'bold',
    }
});

const UserComponent = props => {

    const { classes, user, role, saveHandler, showEdit } = props;

    var firstName = user ? user.firstName : null;
    var lastName = user ? user.lastName : null;
    var email = user ? user.email : null;
    var telephoneNumber = user ? user.telephoneNumber : null;
    var mobileNumber = user ? user.mobileNumber : null;
    var businessNumber = user ? user.businessNumber : null;
    var isFemale = user && user.gender === 'FEMALE';

    const [edit, setEdit] = useState(!user);

    const handleSave = () => {
        if (saveHandler) {
            saveHandler({
                firstName,
                lastName,
                email,
                gender: isFemale ? 'FEMALE' : 'MALE',
                telephoneNumber,
                mobileNumber,
                businessNumber,
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
                        value={firstName}
                        label="Vorname"
                        type="text"
                        name="first_name"
                        autoComplete="first name"
                        margin="dense"
                        variant="outlined"
                        onChange={e => firstName = e.target.value}
                        className={classes.input}
                        required
                    />
                    <TextField
                        id="last_name"
                        value={lastName}
                        label="Nachname"
                        type="text"
                        name="last_name"
                        autoComplete="last name"
                        margin="dense"
                        variant="outlined"
                        onChange={e => lastName = e.target.value}
                        className={classes.input}
                        required
                    />
                    <TextField
                        id="email"
                        value={email}
                        label="Email"
                        type="email"
                        name="email"
                        autoComplete="email"
                        margin="dense"
                        variant="outlined"
                        onChange={e => email = e.target.value}
                        className={classes.input}
                        required
                    /><br />
                    <TextField
                        id="telephone_number"
                        value={telephoneNumber}
                        label="Telefon Festnetz"
                        type="text"
                        name="telephone_number"
                        autoComplete="telephone number"
                        margin="dense"
                        variant="outlined"
                        onChange={e => telephoneNumber = e.target.value}
                        className={classes.input}
                    />
                    <TextField
                        id="mobile_number"
                        value={mobileNumber}
                        label="Telefon Mobil"
                        type="text"
                        name="mobile_number"
                        autoComplete="telephone mobile number"
                        margin="dense"
                        variant="outlined"
                        onChange={e => mobileNumber = e.target.value}
                        className={classes.input}
                    />
                    <TextField
                        id="business_number"
                        value={businessNumber}
                        label="Telefon Geschäftlich"
                        type="text"
                        name="business_number"
                        autoComplete="telephone business number"
                        margin="dense"
                        variant="outlined"
                        onChange={e => businessNumber = e.target.value}
                        className={classes.input}
                    /><br />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={isFemale}

                                value="gender"
                                onChange={e => isFemale = e.target.value}
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
                    <Typography variant="body1" className={classes.userName}>{user ? user.lastName : ''}, {user ? user.firstName : ''}
                        {showEdit ?
                            (<Button size="small" color="secondary" onClick={() => setEdit(true)}>
                                <Icon>create</Icon>
                            </Button>) : null}
                    </Typography>

                </>)
            }
        </div>
    )
}

export default withStyles(styles)(UserComponent);