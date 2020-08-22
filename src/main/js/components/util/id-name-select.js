import React from 'react';
import { Select, FormControl, InputLabel, MenuItem } from '@material-ui/core';

const IdNameSelect = props => (
    <>
        <FormControl
            className={props.className}
        >
            <InputLabel htmlFor="select">{props.label}</InputLabel>
            <Select
                value={props.value || ''}
                onChange={event => props.onChange(event.target.value)}
                inputProps={{
                    name: 'select',
                    id: 'select',
                }}
            >
                {(!props.value || props.nullable) && (<MenuItem value=""></MenuItem>)}
                {(props.data instanceof Map ? [...props.data.values()] : props.data).map(idName => (
                    <MenuItem key={idName.id} value={idName.id}>{idName.name}</MenuItem>
                ))}
            </Select>
        </FormControl>
    </>
);
export default IdNameSelect;