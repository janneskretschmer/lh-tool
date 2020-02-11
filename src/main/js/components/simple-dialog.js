import React, { useState } from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import Slide from '@material-ui/core/Slide';

const Transition = props => (<Slide direction="up" {...props} />);

const SimpleDialog = props => {

    const { open, onOK, title, text, cancelText, okText } = props;

    const [isOpen, setOpen] = useState(!!props.open);

    const childWithOnClick = React.Children.map(props.children, (child) => {
        return React.cloneElement(child, { onClick: () => setOpen(true) });
    });


    return (<>
        {childWithOnClick}
        <Dialog
            open={isOpen}
            TransitionComponent={Transition}
            keepMounted
            onClose={() => setOpen(false)}
            aria-labelledby="alert-dialog-slide-title"
            aria-describedby="alert-dialog-slide-description"
        >
            <DialogTitle id="alert-dialog-slide-title">
                {title}
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-slide-description">
                    {text}
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                {cancelText && (
                    <Button onClick={() => setOpen(false)} color="secondary">
                        {cancelText}
                    </Button>
                )}
                {okText && (
                    <Button color="primary" onClick={() => { onOK(); setOpen(false); }}>
                        {okText}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    </>);
}

export default SimpleDialog;