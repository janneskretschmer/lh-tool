import React, { useState } from 'react';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Button from '@mui/material/Button';
import Slide from '@mui/material/Slide';

const Transition = React.forwardRef((props, ref) => (<Slide direction="up" ref={ref} {...props} />));

const SimpleDialog = props => {

    const { open, onOK, title, text, cancelText, okText, content, onOpen } = props;

    const [isOpen, setOpen] = useState(!!props.open);

    const childWithOnClick = React.Children.map(props.children, (child) => {
        return React.cloneElement(child, {
            onClick: () => {
                setOpen(true);
                if (onOpen) {
                    onOpen();
                }
            }
        });
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
                {content ? content : (
                    <DialogContentText id="alert-dialog-slide-description">
                        {text}
                    </DialogContentText>
                )}
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
};

export default SimpleDialog;