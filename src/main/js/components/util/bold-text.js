import { Box } from '@mui/system';
import React from 'react';

const BoldText = props => (
    <Box sx={{ fontWeight: 500 }}>
        {props.children}
    </Box>
);
export default BoldText;