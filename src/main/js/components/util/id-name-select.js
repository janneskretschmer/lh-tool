import { FormControl, InputLabel, MenuItem, Select } from '@mui/material';
import React from 'react';

const IdNameSelect = props => (
    <>
        <FormControl
            sx={
                props.sx || {
                    minWidth: '100px',
                }
            }
            size="small"
        >
            <InputLabel id="select-label">{props.label}</InputLabel>
            <Select
                value={props.value || ''}
                onChange={event => props.onChange(event.target.value)}
                labelId="select-label"
                label={props.label}
                size="small"
            >
                {(!props.value || props.nullable) && (
                    // unfortunately, the invisable content is necessary for the hight. &nbsp; doesn't work either
                    <MenuItem value="" sx={{ opacity: 0 }}>[empty]</MenuItem>
                )}
                {(props.data instanceof Map ? [...props.data.values()] : props.data).map(idName => (
                    <MenuItem key={idName.id} value={idName.id}>{idName.name}</MenuItem>
                ))}
            </Select>
        </FormControl>
    </>
);
export default IdNameSelect;