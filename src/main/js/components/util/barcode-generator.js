import {
    Button,
    Checkbox, FormControlLabel, TextField,
    Typography
} from '@mui/material';
import { Box } from '@mui/system';
import bwipjs from 'bwip-js';
import React from 'react';
import SimpleDialog from '../simple-dialog';

export default class BarcodeGenerator extends React.Component {

    BWIPJS_PX_TO_MM_FACTOR = 2.83;
    QR_QUANTUM_SIZE = 21;
    barcodeQuantumSize = null;

    constructor(props) {
        super(props);

        this.qrDownloadRef = React.createRef();
        this.qrSizeRef = React.createRef();
        this.qrCanvasRef = React.createRef();
        this.barcodeDownloadRef = React.createRef();
        this.barcodeSizeRef = React.createRef();
        this.barcodeCanvasRef = React.createRef();

        this.state = {
            textSize: 15,
            textMargin: 1,
            showText: true,

            qrSize: 4,
            qrType: 'qrcode',

            barcodeWidth: 1,
            barcodeHeight: 10,
            barcodeType: 'code128',
        };
    }

    onOpen() {
        this.renderCanvas();
    }

    changeTextSize(textSize) {
        this.setState({ textSize: parseInt(textSize, 10) }, () => this.renderCanvas());
    }

    changeTextMargin(textMargin) {
        this.setState({ textMargin: parseInt(textMargin, 10) }, () => this.renderCanvas());
    }

    changeShowText(showText) {
        this.setState({ showText }, () => this.renderCanvas());
    }

    changeQrSize(qrSize) {
        this.setState({ qrSize: parseInt(qrSize, 10) }, () => this.renderCanvas());
    }

    changeBarcodeWidth(barcodeWidth) {
        this.setState({ barcodeWidth: parseInt(barcodeWidth, 10) }, () => this.renderCanvas());
    }

    changeBarcodeHeight(barcodeHeight) {
        this.setState({ barcodeHeight: parseInt(barcodeHeight, 10) }, () => this.renderCanvas());
    }

    renderCanvas() {
        const { showText, textSize, textMargin, qrSize } = this.state;
        const { content } = this.props;
        const barcodeCanvas = bwipjs.toCanvas(this.barcodeCanvasRef.current, {
            bcid: this.state.barcodeType,       // Barcode type
            text: content,    // Text to encode
            scale: 1,
            width: this.state.barcodeWidth * (this.barcodeQuantumSize || 1) / this.BWIPJS_PX_TO_MM_FACTOR,
            height: this.state.barcodeHeight,
            backgroundcolor: 'FFFFFF',
        });

        const qrCanvas = bwipjs.toCanvas(this.qrCanvasRef.current, {
            bcid: this.state.qrType,       // Barcode type
            text: content,    // Text to encode
            scale: 1,
            width: qrSize * this.QR_QUANTUM_SIZE / this.BWIPJS_PX_TO_MM_FACTOR,
            height: qrSize * this.QR_QUANTUM_SIZE / this.BWIPJS_PX_TO_MM_FACTOR,
            backgroundcolor: 'FFFFFF',
        });

        if (this.barcodeQuantumSize === null) {
            this.barcodeQuantumSize = barcodeCanvas.width;
        }

        if (showText) {
            this.appendText(qrCanvas, content, textSize, textMargin);
            this.appendText(barcodeCanvas, content, textSize, textMargin);
        }

        this.qrDownloadRef.current.href = qrCanvas.toDataURL('image/jpeg', 1.0);
        this.qrSizeRef.current.innerHTML = qrCanvas.width + ' x ' + qrCanvas.height;
        this.barcodeDownloadRef.current.href = barcodeCanvas.toDataURL('image/jpeg', 1.0);
        this.barcodeSizeRef.current.innerHTML = barcodeCanvas.width + ' x ' + barcodeCanvas.height;
    }

    // unfortunately the alttext-feature of bwipjs is not very reliable
    appendText(canvas, text, textSize, textMargin) {
        const width = canvas.width;
        const height = canvas.height;
        const context = canvas.getContext("2d");

        const data = context.getImageData(0, 0, width, height);
        canvas.height = height + textSize + textMargin;
        context.putImageData(data, 0, 0);

        // otherwise transparent background will be black
        context.fillStyle = '#FFFFFF';
        context.fillRect(0, height, width, textSize);

        context.font = textSize + "px Arial";
        context.textAlign = "center";
        context.textBaseline = 'top';
        context.fillStyle = '#000000';
        context.fillText(text, width / 2, height + textMargin);
    }

    render() {
        const { barcodeWidth, barcodeHeight, qrSize, showText, textSize, textMargin, qrType } = this.state;
        const { classes, content } = this.props;
        return (<>
            <SimpleDialog
                title={'Barcode herunterladen'}
                content={(
                    <>
                        <Box component="table" sx={{ borderSpacing: '8px' }}>
                            <tbody>
                                <tr>
                                    <td>
                                        <FormControlLabel
                                            control={
                                                <Checkbox
                                                    checked={showText}
                                                    onChange={event => this.changeShowText(event.target.checked)}
                                                    disableRipple
                                                />
                                            }
                                            label="Text anzeigen"
                                        />
                                    </td>
                                    <td>
                                        <TextField
                                            id="textSize"
                                            label="Textgröße"
                                            type="number"
                                            value={textSize}
                                            onChange={event => this.changeTextSize(event.target.value)}
                                            size="small"
                                            variant="outlined"
                                            disabled={!showText}
                                        />
                                    </td>
                                    <td>
                                        <TextField
                                            id="textMargin"
                                            label="Textabstand"
                                            sx={{ width: '145px' }}
                                            type="number"
                                            value={textMargin}
                                            onChange={event => this.changeTextMargin(event.target.value)}
                                            size="small"
                                            variant="outlined"
                                            disabled={!showText}
                                        />
                                    </td>
                                </tr>
                                <tr><td colSpan="3"><br /></td></tr>
                                <tr>
                                    <Box component="td" sx={{ textAlign: 'center' }} colSpan="2">Eindimensional</Box>
                                    <Box component="td" sx={{ textAlign: 'center' }}>Zweidimensional</Box>
                                </tr>
                                <tr>
                                    <td>
                                        <TextField
                                            id="barcodeWidth"
                                            label="Breite"
                                            type="number"
                                            value={barcodeWidth}
                                            onChange={event => this.changeBarcodeWidth(event.target.value)}
                                            size="small"
                                            variant="outlined"
                                        />
                                    </td>
                                    <td>
                                        <TextField
                                            id="barcodeHeight"
                                            label="Höhe"
                                            type="number"
                                            value={barcodeHeight}
                                            onChange={event => this.changeBarcodeHeight(event.target.value)}
                                            size="small"
                                            variant="outlined"
                                        />
                                    </td>
                                    <Box component="td" sx={{ verticalAlign: 'top' }}>
                                        <TextField
                                            id="qrSize"
                                            label="Größe"
                                            sx={{ width: '145px' }}
                                            type="number"
                                            value={qrSize}
                                            onChange={event => this.changeQrSize(event.target.value)}
                                            size="small"
                                            variant="outlined"
                                        />
                                    </Box>
                                </tr>
                                <tr>
                                    <td colSpan="2">
                                        <Box sx={{
                                            minWidth: '300px',
                                            minHeight: '160px',
                                            border: '1px solid',
                                            borderColor: 'primary.main',
                                            borderRadius: '4px',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                        }}>
                                            <Box component="canvas" sx={{
                                                border: '1px dashed',
                                                borderColor: 'text.disabled',
                                            }} ref={this.barcodeCanvasRef}></Box>
                                        </Box>
                                        <Typography variant="caption"><span ref={this.barcodeSizeRef}>145 x 20</span> Pixel</Typography>
                                    </td>
                                    <td>
                                        <Box sx={{
                                            minWidth: '145px',
                                            minHeight: '160px',
                                            border: '1px solid',
                                            borderColor: 'primary.main',
                                            borderRadius: '4px',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                        }}>
                                            <Box component="canvas" sx={{
                                                border: '1px dashed',
                                                borderColor: 'text.disabled',
                                            }} ref={this.qrCanvasRef}></Box><br />
                                        </Box>
                                        <Typography variant="caption"><span ref={this.qrSizeRef}>84 x 99</span> Pixel</Typography>
                                    </td>
                                </tr>
                                <tr>
                                    <Box component="td" colSpan="2" sx={{ textAlign: 'center' }}>
                                        <Button
                                            component={React.forwardRef((props, ref) => <a {...props} ref={ref}>{props.children}</a>)}
                                            color="primary"
                                            download={'Barcode_' + content + '.jpeg'}
                                            ref={this.barcodeDownloadRef}
                                        >
                                            Herunterladen
                                        </Button>
                                    </Box>
                                    <Box component="td" sx={{ textAlign: 'center' }}>
                                        <Button
                                            component={React.forwardRef((props, ref) => <a {...props} ref={ref}>{props.children}</a>)}
                                            color="primary"
                                            download={'QR_' + content + '.jpeg'}
                                            ref={this.qrDownloadRef}
                                        >
                                            Herunterladen
                                        </Button>
                                    </Box>
                                </tr>

                            </tbody>
                        </Box>
                    </>
                )}
                cancelText="Abbrechen"
                onOpen={() => this.onOpen()}
            >
                {this.props.children}
            </SimpleDialog>

        </>);
    }
}
